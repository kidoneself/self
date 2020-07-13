package com.yimao.cloud.hra.processor;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.ExcelUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.hra.feign.OrderFeign;
import com.yimao.cloud.hra.feign.SystemFeign;
import com.yimao.cloud.hra.feign.UserFeign;
import com.yimao.cloud.hra.mapper.HraDeviceMapper;
import com.yimao.cloud.hra.mapper.HraTicketMapper;
import com.yimao.cloud.pojo.dto.hra.HraAllotTicketExportDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceExportDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceQuery;
import com.yimao.cloud.pojo.dto.hra.HraExportPhysicalDTO;
import com.yimao.cloud.pojo.dto.hra.HraExportQuery;
import com.yimao.cloud.pojo.dto.hra.HraExportReservationDTO;
import com.yimao.cloud.pojo.dto.hra.HraQueryDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisCache redisCache;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private HraTicketMapper hraTicketMapper;
    @Resource
    private HraDeviceMapper hraDeviceMapper;

    /**
     * 导出
     */
    @RabbitListener(queues = RabbitConstant.EXPORT_ACTION_HRA)
    @RabbitHandler
    public void processor(final Map<String, Object> map) {
        executor.submit(() -> {
            ExportRecordDTO record = (ExportRecordDTO) map.get("exportRecordDTO");
            String url = record.getUrl();
            Integer recordId = record.getId();
            Integer adminId = record.getAdminId();

            //导出中
            record.setStatus(ExportRecordStatus.IN_EXPORT.value);
            record.setStatusName(ExportRecordStatus.IN_EXPORT.name);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);
            //设置下载进度
            redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, 0);

            try {
                String[] titles;
                String[] beanProperties;
                //url区分是哪项导出
                switch (url) {
                    case "/reservation/export":
                        //预约列表导出
                        HraExportQuery hraExportQuery = (HraExportQuery) map.get("query");
                        List<HraExportReservationDTO> list = hraExportReservationData(hraExportQuery, adminId, recordId);
                        beanProperties = new String[]{"stationProvince", "stationCity", "stationRegion", "ticketNo", "userId", "customerUserName", "customerIdCard", "customerPhone",
                                "customerSex", "customerHeight", "customerWeight", "customerBirthDate", "reserveTime", "reserveStatus", "reserveFrom"};
                        titles = new String[]{"预约区域（省）", "预约区域（市）", "预约区域（区）", "体检卡号", "用户ID", "体检人", "身份证号",
                                "手机号", "性别", "身高", "体重", "出生日期", "预约体检日期", "预约状态", "用户来源"};
                        //导出成功
                        exportSuccessful(record, titles, beanProperties, list);
                        break;
                    //评估列表导出
                    case "/ticket/used/export":
                        log.info("=========评估列表导出开始=========");
                        HraExportQuery evaluationQuery = (HraExportQuery) map.get("query");
                        List<HraExportReservationDTO> reservationlist = hraExportReservationData(evaluationQuery, adminId, recordId);
                        titles = new String[]{"评估区域（省）", "评估区域（市）", "评估区域（区）", "体检卡号", "用户ID", "体检人", "身份证号",
                                "手机号", "性别", "身高", "体重", "出生日期", "实际体检日期", "体检卡状态", "体检卡型号", "用户来源"};
                        beanProperties = new String[]{"stationProvince", "stationCity", "stationRegion", "ticketNo", "userId", "customerUserName", "customerIdCard", "customerPhone",
                                "customerSex", "customerHeight", "customerWeight", "customerBirthDate", "examDate", "ticketStatus", "ticketType", "reserveFrom"};
                        //导出成功
                        exportSuccessful(record, titles, beanProperties, reservationlist);
                        log.info("=========导出成功=========");
                        break;
                    //体检卡列表导出
                    case "/ticket/physical/export":
                        HraQueryDTO hraQuery = (HraQueryDTO) map.get("query");
                        List<HraExportPhysicalDTO> physicalList = exportPhysicalData(hraQuery, adminId, recordId);
                        titles = new String[]{"创建时用户ID", "当前用户ID", "体检卡型号", "体检卡状态",
                                "到期日期", "创建时间", "分配端", "订单号", "支付状态", "支付时间"};

                        beanProperties = new String[]{"createUserId", "currentUserId", "cardType", "ticketStatus",
                                "endTime", "setTime", "reserveFrom", "orderId", "pay", "payTime"};
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, physicalList);
                        break;
                    //F卡导出
                    case "/ticket/special/export":
                        HraQueryDTO specialQuery = (HraQueryDTO) map.get("query");
                        List<HraAllotTicketExportDTO> resultList = this.exportSpecialTicketData(specialQuery, adminId, recordId);
                        titles = new String[]{"门店区域（省）", "门店区域（市）", "门店区域（区）", "服务站门店名称", "体检卡号", "有效期", "到期时间",
                                "是否到期", "总可用次数", "剩余可用次数", "是否限制仅该服务站使用", "分配时间", "是否禁用"};
                        beanProperties = new String[]{"stationProvince", "stationCity", "stationRegion", "stationName", "ticketNo", "days", "validEndTime", "expired", "total",
                                "useCount", "selfStation", "createTime", "forbidden"};
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, resultList);
                        break;
                    case "/device/export":
                        HraDeviceQuery hraDeviceQuery = (HraDeviceQuery) map.get("query");
                        List<HraDeviceExportDTO> hraDeviceList = this.hraDeviceExport(hraDeviceQuery, adminId, recordId);

                        titles = new String[]{"设备ID", "设备型号", "设备状态", "设备描述", "服务站名称", "服务站地址", "服务站联系电话",
                                "备注", "服务站ID", "区县级公司ID", "区县级公司名称", "省", "市","区县","创建人","创建时间","更新人","更新时间"};
                        beanProperties = new String[]{"deviceId", "deviceType", "deviceStatus", "deviceDesc", "stationName", "stationAddress", "stationTel", "remark", "stationId",
                                "stationCompanyId", "stationCompanyName", "province", "city","region","creator","createTime","updater","updateTime"};
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, hraDeviceList);
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


    private List<HraExportReservationDTO> hraExportReservationData(HraExportQuery query, Integer adminId, Integer recordId) {
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            List<HraExportReservationDTO> list = new ArrayList<>();
            //服务站ids
            if (StringUtil.isNotEmpty(query.getProvince()) || StringUtil.isNotEmpty(query.getCity()) || StringUtil.isNotEmpty(query.getRegion())) {
                List<Integer> stationIds = systemFeign.findStationIdsByPCR(query.getProvince(), query.getCity(), query.getRegion(), null);
                if (CollectionUtil.isNotEmpty(stationIds)) {
                    query.setStationIds(stationIds);
                }
            }
            int pageNum = 1;
            Page<HraExportReservationDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = hraTicketMapper.listTicketExport(query);
                log.info("========size=======" + page.getResult().size());
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

    private List<HraExportPhysicalDTO> exportPhysicalData(HraQueryDTO hraQuery, Integer adminId, Integer recordId) {
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            List<HraExportPhysicalDTO> list = new ArrayList<>();
            List<Integer> stationIds = systemFeign.findStationIdsByPCR(hraQuery.getProvince(), hraQuery.getCity(), hraQuery.getRegion(), hraQuery.getStationName());
            List<Integer> ids = null;
            if (Objects.nonNull(hraQuery.getUserType())) {
                ids = userFeign.getUserByUserType(hraQuery.getUserType());
            }
            hraQuery.setStationIds(stationIds);
            hraQuery.setIds(ids);

            int pageNum = 1;
            Page<HraExportPhysicalDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = hraTicketMapper.exportPhysical(hraQuery);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, df.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");

            Iterator<HraExportPhysicalDTO> iterator = list.iterator();
            while (iterator.hasNext()) {
                HraExportPhysicalDTO next = iterator.next();
                if (next.getOrderId() != null) {
                    OrderSubDTO basicOrderInfoById = orderFeign.findBasicOrderInfoById(next.getOrderId());//payStatus   payTime//支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败
                    switch (basicOrderInfoById.getPayStatus()) {
                        case 1:
                            next.setPay("未支付");
                            break;
                        case 2:
                            next.setPay("待审核");
                            break;
                        case 3:
                            next.setPay("支付成功");
                            break;
                        case 4:
                            next.setPay("支付失败");
                            break;
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String payTime = sdf.format(basicOrderInfoById.getPayTime());
                    next.setPayTime(payTime);
                }
            }
            return list;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List<HraAllotTicketExportDTO> exportSpecialTicketData(HraQueryDTO hraQuery, Integer adminId, Integer recordId) {
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            List<HraAllotTicketExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<HraAllotTicketExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = hraTicketMapper.hraAllotTicketExport(hraQuery);
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


    private List<HraDeviceExportDTO> hraDeviceExport(HraDeviceQuery query, Integer adminId, Integer recordId) {
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            List<HraDeviceExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<HraDeviceExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = hraDeviceMapper.exportDevice(query);
                for (HraDeviceExportDTO hraDeviceExportDTO : page.getResult()) {
                    String stationCompanyName = systemFeign.getStationCompanyNameById(hraDeviceExportDTO.getStationId());
                    hraDeviceExportDTO.setStationCompanyName(stationCompanyName);
                }
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
