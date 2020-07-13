package com.yimao.cloud.system.processor;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.ExportUrlConstant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.enums.ProductActivityType;
import com.yimao.cloud.base.enums.ProductModeEnum;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.ExcelUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.*;
import com.yimao.cloud.pojo.query.system.*;
import com.yimao.cloud.pojo.vo.system.StoreHouseVO;
import com.yimao.cloud.system.feign.ProductFeign;
import com.yimao.cloud.system.mapper.*;
import com.yimao.cloud.system.po.StoreHouse;
import com.yimao.cloud.system.service.StationService;
import lombok.extern.slf4j.Slf4j;
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

    private static final int pageSize = 500;
    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    @Resource
    private MessagePushMapper messagePushMapper;
    @Resource
    private DomainProperties domainProperties;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisCache redisCache;
    @Resource
    private StoreHouseMapper storeHouseMapper;
    @Resource
    private StockOperationMapper stockOperationMapper;
    @Resource
    private StationService stationService;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private StationCompanyMapper stationCompanyMapper;
    @Resource
    private StoreHouseAllMapper storeHouseAllMapper;
    @Resource
    private StationCompanyStoreHouseMapper stationCompanyStoreHouseMapper;
    @Resource
    private TransferOperationLogMapper transferOperationLogMapper;

    /**
     * 导出
     */
    @RabbitListener(queues = RabbitConstant.EXPORT_ACTION_SYSTEM)
    @RabbitHandler
    public void processor(final Map<String, Object> map) {
        executor.submit(() -> {
            ExportRecordDTO record = (ExportRecordDTO) map.get("exportRecordDTO");
            String url = record.getUrl();
            Integer recordId = record.getId();
            Integer adminId = record.getAdminId();
            String title = record.getTitle();

            //导出中
            record.setStatus(ExportRecordStatus.IN_EXPORT.value);
            record.setStatusName(ExportRecordStatus.IN_EXPORT.name);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);
            //设置下载进度
            redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, 0);
            try {
                String[] titles;
                String[] beanProperties;
                switch (url) {
                    //订单列表导出
                    case ExportUrlConstant.EXPORT_MESSAGEPUSH_URL:
                        MessagePushQuery query = (MessagePushQuery) map.get("query");
                        List<MessagePushExportDTO> list = messagePushExportData(query, adminId, recordId);
                        beanProperties = new String[]{"title", "content", "deviceTypeStr", "createTimeStr"};
                        titles = new String[]{"类别", "内容", "设备", "推送时间"};
                        //导出成功
                        exportSuccessful(record, titles, beanProperties, list);
                        break;
                    case ExportUrlConstant.EXPORT_STATION_COMPANY_URL:
                        StationCompanyQuery query2 = (StationCompanyQuery) map.get("query");
                        List<StationCompanyExportDTO> list2 = stationCompanyExportData(query2, adminId, recordId);
                        beanProperties = new String[]{"code", "name", "type", "province", "city", "region", "address", "serviceProvince",
                                "serviceCity", "serviceRegion", "serviceType", "contact", "contactIdCard", "contactPhone", "email", "creditCode", "legalPerson", "bank", "bankAccount", "bankNumber", "taxNumber", "onlineTime", "signUp", "yunSignTime"};
                        titles = new String[]{"服务站门店编号", "服务站公司名称", "服务站公司类型", "所在区域（省）", "所在区域（市）", "所在区域（区）",
                                "详细地址", "服务区域（省）", "	服务区域（市）", "服务区域（区）", "服务类型", "联系人", "身份证号", "联系方式", "服务站公司邮箱", "统一社会信用码", "法人代表姓名", "公司开户行",
                                "开户行账号", "开户行行号", "开户行税号", "上线时间", "是否注册过云签", "注册云签时间"};
                        //导出成功
                        exportSuccessful(record, titles, beanProperties, list2);
                        break;
                    case "/store/house/export":
                        List<ProductDTO> productList = productFeign.findProductList();
                        productList.removeIf(dto -> dto.getMode() != ProductModeEnum.LEASE.value || dto.getActivityType() == null || dto.getActivityType() != ProductActivityType.PRODUCT_COMMON.value);
                        titles = getStoreHouseRowTitles(productList);
                        String province = (String) map.get("province");
                        String city = (String) map.get("city");
                        String region = (String) map.get("region");
                        Integer special = (Integer) map.get("special");
                        List<StoreHouseVO> userExportList = getStoreHouseExportData(adminId, recordId, province, city, region, special, productList);
                        String path = ExcelUtil.exportStoreHouseSXSSF(userExportList, title, titles.length - 1, titles);
                        if (StringUtil.isEmpty(path)) {
                            throw new YimaoException("上传导出数据到FTP服务器发生异常");
                        }
                        String downloadLink = domainProperties.getImage() + path;
                        //导出成功
                        record.setStatus(ExportRecordStatus.SUCCESSFUL.value);
                        record.setStatusName(ExportRecordStatus.SUCCESSFUL.name);
                        record.setDownloadLink(downloadLink);
                        rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);
                        break;
                    case "/station/export/info":
                        StationQuery query3 = (StationQuery) map.get("query");
                        List<StationExportDTO> resultList = stationService.getStationInfoToExport(query3);
                        beanProperties = new String[]{"code", "stationName", "stationCompanyName", "type", "province", "city", "region", "address", "serviceProvince",
                                "serviceCity", "serviceRegion", "serviceTypeStr", "contact", "contactPhone", "contract", "contractStartTime", "contractEndTime", "contractor", "contractorPhone",
                                "contractorIdCard", "masterName", "masterPhone", "masterIdCard", "masterAccount", "createTime", "businessHoursStart", "businessHoursEnd", "employeeNum", "stationArea", "online", "onlineTime"};
                        titles = new String[]{"服务站门店编号", "服务站门店名称", "服务站公司名称", "服务站门店类型", "所在区域（省）", "所在区域（市）", "所在区域（区）",
                                "门店详细地址", "服务区域（省）", "	服务区域（市）", "服务区域（区）", "服务类型", "联系人", "联系人手机号", "承包状态", "承包开始时间", "承包结束时间", "承包人姓名", "承包人手机号", "承包人身份证号",
                                "站长姓名", "站长手机号", "站长身份证号", "站长账号", "服务站门店成立时间", "服务站门店营业开始时间", "服务站门店营业结束时间", "员工数量", "门店规模", "上线状态", "上线时间"};
                        //导出成功
                        exportSuccessful(record, titles, beanProperties, resultList);
                        break;
                    case "/store/operation/export":
                        List<StockOperationExportDTO> stockExportList = exportStockOperation(map, adminId, recordId);
                        titles = new String[]{"原库存省", "原库存市", "原库存区", "操作库存省", "操作库存市", "操作库存区", "产品", "数量", "操作", "操作者", "创建时间"};
                        beanProperties = new String[]{"originalProvince", "originalCity", "originalRegion", "operateProvince", "operateCity", "operateRegion", "deviceName", "count", "operation", "admin", "createTime"};
                        //导出成功
                        exportSuccessful(record, titles, beanProperties, stockExportList);
                        break;
                    case "/store/house/all/export":
                        StoreHouseAllQuery storeHouseAllQuery = (StoreHouseAllQuery) map.get("query");
                        List<StoreHouseAllExportDTO> storeHouseAllExportDTOS = storeHouseAllMapper.exportHouseAll(storeHouseAllQuery);
                        if (storeHouseAllQuery.getType() == 1) {
                            //净水设备库存导出
                            beanProperties = new String[]{"firstCategoryName", "twoCategoryName", "goodsName", "stockCount"};
                            titles = new String[]{"一级分类", "二级分类", "物品名称", "库存数量"};
                            //导出成功
                            exportSuccessful(record, titles, beanProperties, storeHouseAllExportDTOS);
                        } else if (storeHouseAllQuery.getType() == 2) {
                            //物资配件库存导出
                            beanProperties = new String[]{"adaptationModel", "firstCategoryName", "twoCategoryName", "goodsName", "stockCount"};
                            titles = new String[]{"适用型号", "一级分类", "二级分类", "物品名称", "库存数量"};
                            //导出成功
                            exportSuccessful(record, titles, beanProperties, storeHouseAllExportDTOS);
                        } else if (storeHouseAllQuery.getType() == 3) {
                            //展示机库存导出
                            beanProperties = new String[]{"firstCategoryName", "twoCategoryName", "goodsName", "stockCount"};
                            titles = new String[]{"一级分类", "二级分类", "物品名称", "库存数量"};
                            //导出成功
                            exportSuccessful(record, titles, beanProperties, storeHouseAllExportDTOS);
                        }
                        break;
                    case "/store/house/stationCompany/export":
                        StationCompanyStoreHouseQuery stationCompanyStoreHouseQuery = (StationCompanyStoreHouseQuery) map.get("query");
                        List<StationCompanyStoreHouseExportDTO> stationCompanyStoreHouseExportDTOS = stationCompanyStoreHouseMapper.exportStationCompanyStoreHouse(stationCompanyStoreHouseQuery);
                        if (stationCompanyStoreHouseQuery.getType() == 1) {
                            //净水设备库存导出
                            beanProperties = new String[]{"stationCompanyName", "address", "stationCount", "firstCategoryName", "twoCategoryName", "goodsName", "stockCount"};
                            titles = new String[]{"服务站公司", "所在地区", "门店数", "一级分类", "二级分类", "物品名称", "未分配库存"};
                            //导出成功
                            exportSuccessful(record, titles, beanProperties, stationCompanyStoreHouseExportDTOS);
                        } else if (stationCompanyStoreHouseQuery.getType() == 2) {
                            //物资配件库存导出
                            beanProperties = new String[]{"stationCompanyName", "address", "stationCount", "adaptationModel", "firstCategoryName", "twoCategoryName", "goodsName", "stockCount"};
                            titles = new String[]{"服务站公司", "所在地区", "门店数","适用型号", "一级分类", "二级分类", "物品名称", "库存数量"};
                            //导出成功
                            exportSuccessful(record, titles, beanProperties, stationCompanyStoreHouseExportDTOS);
                        } else if (stationCompanyStoreHouseQuery.getType() == 3) {
                            //展示机库存导出
                            beanProperties = new String[]{"stationCompanyName", "address", "stationCount", "firstCategoryName", "twoCategoryName", "goodsName", "stockCount"};
                            titles = new String[]{"服务站公司", "所在地区", "门店数", "一级分类", "二级分类", "物品名称", "未分配库存"};
                            //导出成功
                            exportSuccessful(record, titles, beanProperties, stationCompanyStoreHouseExportDTOS);
                        }
                        break;
                    case "/transfer/operation/log/export":
                        TransferOperationLogQuery transferOperationLogQuery = (TransferOperationLogQuery) map.get("query");
                        List<TransferOperationLogExportDTO> transferOperationLogExportDTOS = transferOperationLogMapper.exportTransferOperationLog(transferOperationLogQuery);
                        beanProperties = new String[]{"destStationCompanyCode", "destStationCompanyName","description", "origStationCompanyCode", "origStationCompanyName", "operator", "createTime"};
                        titles = new String[]{"承包公司编号", "承包公司名称", "操作详情", "原公司编号", "原公司名称", "操作人", "操作完成时间"};
                        //导出成功
                        exportSuccessful(record, titles, beanProperties, transferOperationLogExportDTOS);
                        break;
                }
            } catch (Exception e) {
                //导出失败
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
        //导出成功
        record.setStatus(ExportRecordStatus.SUCCESSFUL.value);
        record.setStatusName(ExportRecordStatus.SUCCESSFUL.name);
        record.setDownloadLink(downloadLink);
        rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);
    }


    /**
     * 分页查询库存导出数据
     */
    private List<StoreHouseVO> getStoreHouseExportData(Integer adminId, Integer recordId, String province, String city, String region, Integer special, List<ProductDTO> productList) {
        try {
            List<StoreHouseVO> list = new ArrayList<>();
            int pageNum = 1;
            Page<StoreHouse> page = null;
            StoreHouse storeHouse = new StoreHouse();
            storeHouse.setProvince(province);
            storeHouse.setCity(city);
            storeHouse.setRegion(region);
            storeHouse.setSpecial(special);
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = (Page<StoreHouse>) storeHouseMapper.select(storeHouse);
                list.addAll(getStoreHouseVOList(page.getResult(), productList));
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("共导出库存管理数据" + list.size() + "条");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public List<StoreHouseVO> getStoreHouseVOList(List<StoreHouse> list, List<ProductDTO> productList) {
        List<StoreHouseVO> storeHouseVOList = Lists.newArrayList();
        Gson gson = new Gson();
        for (StoreHouse store : list) {
            List<StockCountDTO> stockCounts = Lists.newArrayList();
            Map<String, Integer> stocks = gson.fromJson(store.getStocks(), new TypeToken<Map<String, Integer>>() {
            }.getType());
            for (ProductDTO dto : productList) {
                String productModel = dto.getCategoryName();
                if (stocks.containsKey(productModel)) {
                    stockCounts.add(new StockCountDTO(dto.getId(), dto.getName(), stocks.get(productModel)));
                    stocks.remove(productModel);
                } else {
                    stockCounts.add(new StockCountDTO(dto.getId(), dto.getName(), 0));
                }
            }
            StoreHouseVO StoreHouseVo = new StoreHouseVO(store.getId(), Integer.toString(store.getPlace()), store.getProvince(), store.getCity(), store.getRegion(), stockCounts, store.getUpdateTime());
            storeHouseVOList.add(StoreHouseVo);
        }
        return storeHouseVOList;
    }

    /**
     * 组装库存导出列名
     */
    private String[] getStoreHouseRowTitles(List<ProductDTO> productList) {
        int size = productList.size();
        String[] titles = new String[size + 3];
        titles[0] = "序号";
        titles[1] = "仓库";
        for (int i = 0; i < size; i++) {
            titles[i + 2] = productList.get(i).getName();
        }
        titles[size + 2] = "更新时间";
        return titles;
    }

    /**
     * 消息通知
     *
     * @param query
     * @param adminId
     * @param recordId
     * @return
     */
    private List<MessagePushExportDTO> messagePushExportData(MessagePushQuery query, Integer adminId, Integer recordId) {
        //设置下载进度
        redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, 0);
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            List<MessagePushExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<MessagePushExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = messagePushMapper.exportMessagePush(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, df.format((double) pageNum / page.getPages() * 100));
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


    /**
     * 消息通知
     *
     * @param query
     * @param adminId
     * @param recordId
     * @return
     */
    private List<StationCompanyExportDTO> stationCompanyExportData(StationCompanyQuery query, Integer adminId, Integer recordId) {
        //设置下载进度
        redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, 0);
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            List<StationCompanyExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<StationCompanyExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = stationCompanyMapper.getStationCompanyInfoToExport(query.getProvince(), query.getCity(), query.getRegion(),
                        query.getName(), query.getContact(), query.getContactPhone(),
                        query.getAreaId(), query.getOnline(), query.getSignUp(), query.getStartTime(),
                        query.getEndTime(), query.getLocationProvince(), query.getLocationCity(), query.getLocationRegion(), query.getServiceType(), query.getType());
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, df.format((double) pageNum / page.getPages() * 100));
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

    private List<StockOperationExportDTO> exportStockOperation(Map<String, Object> map, Integer adminId, Integer recordId) {
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            List<StockOperationExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<StockOperationExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = stockOperationMapper.stockOperationExport(map);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, df.format((double) pageNum / page.getPages() * 100));
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
}
