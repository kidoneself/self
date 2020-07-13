package com.yimao.cloud.water.processor;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.ExportUrlConstant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.ExcelUtil;
import com.yimao.cloud.base.utils.SFTPUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordExportDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordQueryDTO;
import com.yimao.cloud.pojo.export.water.DeviceListExport;
import com.yimao.cloud.pojo.export.water.DeviceReplaceRecordExport;
import com.yimao.cloud.pojo.export.water.FilterReplaceExport;
import com.yimao.cloud.pojo.export.water.ManualPadCostExport;
import com.yimao.cloud.pojo.query.water.ManualPadCostQuery;
import com.yimao.cloud.pojo.query.water.WaterDeviceQuery;
import com.yimao.cloud.pojo.query.water.WaterDeviceReplaceRecordQuery;
import com.yimao.cloud.water.mapper.ManualPadCostMapper;
import com.yimao.cloud.water.mapper.WaterDeviceFilterChangeRecordMapper;
import com.yimao.cloud.water.mapper.WaterDeviceMapper;
import com.yimao.cloud.water.mapper.WaterDeviceReplaceRecordMapper;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 描述：导出
 *
 * @Author Zhang Bo
 * @Date 2019/11/25
 */
@Component
@Slf4j
public class ExportProcessor {

    private static ExecutorService executor = Executors.newFixedThreadPool(3);
    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private static final int pageSize = 500;

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisCache redisCache;
    @Resource
    private WaterDeviceReplaceRecordMapper waterDeviceReplaceRecordMapper;
    @Resource
    private WaterDeviceFilterChangeRecordMapper waterDeviceFilterChangeRecordMapper;
    @Resource
    private ManualPadCostMapper manualPadCostMapper;
    @Resource
    private WaterDeviceMapper waterDeviceMapper;

    /**
     * 导出
     */
    @RabbitListener(queues = RabbitConstant.EXPORT_ACTION_WATER)
    @RabbitHandler
    public void processor(final Map<String, Object> map) {
        executor.submit(() -> {
            ExportRecordDTO record = (ExportRecordDTO) map.get("exportRecordDTO");
            String url = record.getUrl();
            Integer recordId = record.getId();
            Integer adminId = record.getAdminId();

            // 导出中
            record.setStatus(ExportRecordStatus.IN_EXPORT.value);
            record.setStatusName(ExportRecordStatus.IN_EXPORT.name);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);
            // 设置下载进度
            redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, 0);
            SXSSFWorkbook workbook = null;
            try {
                String[] titles;
                String[] beanProperties;
                // url区分是哪项导出
                switch (url) {
                    case "/waterdevice/replacerecord/export":
                        WaterDeviceReplaceRecordQuery query = (WaterDeviceReplaceRecordQuery) map.get("query");
                        List<DeviceReplaceRecordExport> list = this.waterDeviceReplaceRecordExport(query, adminId, recordId);
                        titles = new String[]{"新SN码", "新ICCID", "新生产批次号", "旧SN码", "旧ICCID", "旧生产批次号", "操作人", "添加时间"};
                        beanProperties = new String[]{"newSn", "newIccid", "newBatchCode", "oldSn", "oldIccid",
                                "oldBatchCode", "creator", "createTime"};
                        // 导出成功
                        this.exportSuccessful(record, titles, beanProperties, list);
                        break;
                    case "/waterdevice/filterReplace/export":

                        titles = new String[]{"SN码", "滤芯名", "省份", "城市", "地区", "更换时间", "设备添加时间"};
                        beanProperties = new String[]{"sn", "filterName", "province", "city", "region", "createTime",
                                "activatingTime"};
                        WaterDeviceQuery queryPrams = (WaterDeviceQuery) map.get("query");
                        workbook = filterReplaceExport(queryPrams, adminId, recordId, titles, beanProperties);
                        // 导出成功
                        this.uploadExportData(record, workbook);
                        break;
                    case "/padcost/export":
                        ManualPadCostQuery manualPadCostQuery = (ManualPadCostQuery) map.get("query");
                        List<ManualPadCostExport> manualPadCostExportList = manualPadCostExport(manualPadCostQuery, adminId,
                                recordId);
                        titles = new String[]{"SN码", "余额", "已使用流量", "是否开启", "同步时间", "同步状态", "同步失败原因", "创建时间"};
                        beanProperties = new String[]{"sn", "balance", "discharge", "open", "syncTime", "syncStatus",
                                "syncFailReason", "createTime"};
                        // 导出成功
                        this.exportSuccessful(record, titles, beanProperties, manualPadCostExportList);
                        break;
                    case ExportUrlConstant.EXPORT_FILTERCHANGERECORD_URL:
                        WaterDeviceFilterChangeRecordQueryDTO filterChangeRecordQuery = (WaterDeviceFilterChangeRecordQueryDTO) map
                                .get("query");
                        List<WaterDeviceFilterChangeRecordExportDTO> changeRecordExportList = waterDeviceFilterChangeRecordExport(
                                filterChangeRecordQuery, adminId, recordId);
                        beanProperties = new String[]{"maintenanceWorkOrderId", "province", "city", "region", "address",
                                "filterName", "consumerName", "consumerPhone", "deviceBatchCode", "deviceModelName",
                                "deviceSncode", "sourceTxt", "effectiveTxt", "createTimeTxt", "activatingTime", "deviceScope",
                                "deviceSimcard"};
                        titles = new String[]{"维护工单号", "省", "市", "区", "详细地址", "滤芯类型", "客户姓名", "客户电话", "批次码", "设备类型", "SN码",
                                "来源", "是否有效", "更换时间", "设备激活时间", "产品范围", "设备ICCID"};
                        // 导出成功
                        this.exportSuccessful(record, titles, beanProperties, changeRecordExportList);
                        break;
                    case "/waterdevice/list/export":
                        titles = new String[]{"SN码", "批次码", "计费方式", "省份", "城市", "地区", "经销商登录名", "经销商姓名", "用户姓名", "用户手机号",
                                "客服姓名", "客服手机号", "型号", "激活时间", "SIM卡号", "最后时间", "是否在线", "金额", "时长", "流量", "版本号", "是否更换计费方式",
                                "当前计费方式", "sim卡运营商", "网络状态"};
                        beanProperties = new String[]{"sn", "logisticsCode", "costName", "province", "city", "region",
                                "distributorName", "distributorRealName", "deviceUserName", "deviceUserPhone", "engineerName",
                                "engineerPhone", "deviceModel", "snEntryTime", "iccid", "lastOnlineTime", "online", "money",
                                "currentTotalTime", "currentTotalFlow", "version", "costChanged", "costType", "simCompany",
                                "connectionType"};
                        // 导出成功
                        WaterDeviceQuery deviceQuery = (WaterDeviceQuery) map.get("query");
                        workbook = this.waterDeviceListExport(deviceQuery, adminId, recordId, titles, beanProperties);
                        this.uploadExportData(record, workbook);
                        break;
                }
            } catch (Exception e) {
                // 导出失败
                record.setStatus(ExportRecordStatus.FAILURE.value);
                record.setStatusName(ExportRecordStatus.FAILURE.name);
                if (e instanceof YimaoException) {
                    record.setReason(e.getMessage());
                } else {
                    record.setReason("导出失败");
                }
                rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);
            }
        });
    }

    /**
     * 更新导出记录
     */
    private void exportSuccessful(ExportRecordDTO record, String[] titles, String[] beanProperties, List list) {
        String path = ExcelUtil.exportToFtp(list, record.getTitle(), titles.length - 1, titles, beanProperties);
        if (StringUtil.isEmpty(path)) {
            throw new YimaoException("上传导出数据到FTP服务器发生异常");
        }
        String downloadLink = domainProperties.getImage() + path;
        // 导出成功
        record.setStatus(ExportRecordStatus.SUCCESSFUL.value);
        record.setStatusName(ExportRecordStatus.SUCCESSFUL.name);
        record.setDownloadLink(downloadLink);
        rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);
    }

    private SXSSFWorkbook filterReplaceExport(WaterDeviceQuery query, Integer adminId, Integer recordId,
                                              String[] titles, String[] beanProperties) {
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            int pageNum = 1;
            Page<FilterReplaceExport> page = null;
            String title = "滤芯更换导出";
            SXSSFWorkbook workbook = null;
            String activatingTime = "";
            boolean isFistBatch;
            int pages = 0;
            int size = 5000;
            List<String> ids = new ArrayList<>();
            log.info("============滤芯更换导出开始==============");
            boolean flag = true;
            while (flag) {
                if (pageNum == 1) {
                    query.setPageSize(null);
                    PageHelper.startPage(pageNum, size);
                    page = waterDeviceMapper.filterReplaceExport(query);
                    isFistBatch = true;
                    pages = page.getPages();
                } else {
                    query.setPageSize(size);
                    query.setActivatingTime(activatingTime);
                    page = waterDeviceMapper.filterReplaceExport(query);
                    isFistBatch = false;
                }
                if (page == null || page.getResult().isEmpty() || pageNum > pages + 1) {
                    flag = false;
                    break;
                }

                workbook = ExcelUtil.generateWorkBook(workbook, distinctFilterReplaceData(page.getResult(), ids), title,
                        titles.length - 1, titles, beanProperties, isFistBatch);
                if (page != null && !page.getResult().isEmpty() && page.getResult().size() > 0) {
                    activatingTime = page.getResult().get(page.getResult().size() - 1).getActivatingTime();
                }
                if (pageNum < pages) {
                    // 设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId,
                            df.format((double) pageNum / pages * 100));
                }
                pageNum++;
            }
            log.info("============滤芯更换导出结束==============");
            return workbook;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List<ManualPadCostExport> manualPadCostExport(ManualPadCostQuery query, Integer adminId, Integer recordId) {
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            List<ManualPadCostExport> list = new ArrayList<>();
            int pageNum = 1;
            Page<ManualPadCostExport> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = manualPadCostMapper.export(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    // 设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId,
                            df.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List<DeviceReplaceRecordExport> waterDeviceReplaceRecordExport(WaterDeviceReplaceRecordQuery query,
                                                                           Integer adminId, Integer recordId) {
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            List<DeviceReplaceRecordExport> list = new ArrayList<>();
            int pageNum = 1;
            Page<DeviceReplaceRecordExport> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = waterDeviceReplaceRecordMapper.export(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    // 设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId,
                            df.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List<WaterDeviceFilterChangeRecordExportDTO> waterDeviceFilterChangeRecordExport(
            WaterDeviceFilterChangeRecordQueryDTO query, Integer adminId, Integer recordId) {
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            List<WaterDeviceFilterChangeRecordExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<WaterDeviceFilterChangeRecordExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = waterDeviceFilterChangeRecordMapper.waterDeviceFilterChangeRecordExport(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    // 设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId,
                            df.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    private SXSSFWorkbook waterDeviceListExport(WaterDeviceQuery deviceQuery, Integer adminId, Integer recordId,
                                                String[] titles, String[] beanProperties) {
        try {
            int pageNum = 1;
            int pages = 0;
            int pageSize = 5000;
            Page<DeviceListExport> page = null;
            String createTime = null;
            String title = "设备列表";
            SXSSFWorkbook workbook = null;
            boolean isFistBatch;
            List<Long> ids = new ArrayList<>();
            while (true) {
                if (pageNum == 1) {
                    isFistBatch = true;
                    deviceQuery.setPageSize(null);
                    PageHelper.startPage(pageNum, pageSize);
                    page = waterDeviceMapper.waterDeviceListExport(deviceQuery);
                    pages = page.getPages();
                } else {
                    isFistBatch = false;
                    deviceQuery.setPageSize(pageSize);
                    deviceQuery.setCreateTime(createTime);
                    page = waterDeviceMapper.waterDeviceListExport(deviceQuery);
                }
                if (page == null || page.getResult().isEmpty() || pageNum > pages + 1) {
                    break;
                }

                workbook = ExcelUtil.generateWorkBook(workbook, distinctDeviceId(page.getResult(), ids), title, titles.length - 1, titles, beanProperties, isFistBatch);
                createTime = page.getResult().get(page.getResult().size() - 1).getCreateTime();
                if (pageNum < pages) {
                    // 设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / pages * 100));
                }
                pageNum++;
            }
            return workbook;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List distinctDeviceId(List<DeviceListExport> result, List<Long> ids) {
        if (ids.isEmpty()) {
            for (DeviceListExport device : result) {
                ids.add(device.getId());
            }
            return result;
        }
        List<DeviceListExport> datas = new ArrayList<>();
        for (DeviceListExport device : result) {
            if (!ids.contains(device.getId())) {
                datas.add(device);
            }
        }
        ids.clear();
        for (DeviceListExport device : result) {
            ids.add(device.getId());
        }
        return datas;
    }

    /***
     * 上传导出文件并更新导出状态
     *
     * @param record
     * @param workbook
     */
    private void uploadExportData(ExportRecordDTO record, SXSSFWorkbook workbook) {
        try {
        	if(workbook==null){
        		throw new YimaoException("导出的数据不能为空");
        	}
            String path = SFTPUtil.upload(workbook, record.getTitle());
            if (StringUtil.isEmpty(path)) {
                throw new YimaoException("上传导出数据到FTP服务器发生异常");
            }
            String downloadLink = domainProperties.getImage() + path;
            // 导出成功
            record.setStatus(ExportRecordStatus.SUCCESSFUL.value);
            record.setStatusName(ExportRecordStatus.SUCCESSFUL.name);
            record.setDownloadLink(downloadLink);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);
        } catch (Exception e) {
        	throw new YimaoException("导出的数据不能为空");
        }
    }

    /**
     * 去重
     *
     * @param result
     * @return
     */
    private List distinctFilterReplaceData(List<FilterReplaceExport> result, List<String> ids) {
        if (ids.isEmpty()) {
            for (FilterReplaceExport filterReplace : result) {
                ids.add(getkeys(filterReplace));
            }
            return result;
        }

        List<FilterReplaceExport> datas = new ArrayList<>();
        for (FilterReplaceExport filterReplace : result) {
            if (!ids.contains(getkeys(filterReplace))) {
                datas.add(filterReplace);
            }
        }
        ids.clear();
        for (FilterReplaceExport filterReplace : result) {
            ids.add(getkeys(filterReplace));
        }
        return datas;
    }

    /****
     *
     * @param filterReplace
     * @return
     */
    private String getkeys(FilterReplaceExport filterReplace) {
        StringBuffer sb = new StringBuffer();
        return sb.append(filterReplace.getSn()).append("-").append(filterReplace.getFilterName()).append("-")
                .append(filterReplace.getCreateTime()).toString();
    }

}
