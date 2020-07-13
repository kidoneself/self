package com.yimao.cloud.hra.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.JsonUtil;
import com.yimao.cloud.hra.constant.HraConstant;
import com.yimao.cloud.hra.feign.UserFeign;
import com.yimao.cloud.hra.mapper.*;
import com.yimao.cloud.hra.po.*;
import com.yimao.cloud.hra.service.HraReportService;
import com.yimao.cloud.hra.service.HraTicketService;
import com.yimao.cloud.pojo.dto.hra.*;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author ycl
 * @Description:
 * @param: * @param null
 * @Return:
 * @Create: 2019/4/22 11:48
 */

@Service
@Slf4j
public class HraReportServiceImpl implements HraReportService {

    @Resource
    private HraReportMapper hraReportMapper;
    @Resource
    private HraReportRecordMapper hraReportRecordMapper;
    @Resource
    private HraTicketService hraTicketService;
    @Resource
    private HraCustomerMapper customerMapper;
    @Resource
    private HraReportJsonMapper jsonMapper;
    @Resource
    private HraReportOtherMapper otherMapper;
    @Resource
    private ReportMapper reportMapper;
    @Resource
    private UserFeign userFeign;

    @Override
    public List<ReportDTO> getMyReportList(Integer uid, Integer flag) {
        log.info("**************传入参数:uid=" + uid + ",flag=" + flag);
        log.info("########获取我的报告列表getMyReportList()########");
        List<ReportDTO> dtoList = new ArrayList<>();
        if (flag == 0) {
            log.debug("#####获取自己的评估报告列表#####");
            //模板体检报告数据
            dtoList.add(this.modelReport());

            List<HraTicketDTO> ticketList = hraTicketService.findTicketListByUserId(uid);
            if (CollectionUtil.isNotEmpty(ticketList)) {
                //得到体检卡号
                for (HraTicketDTO ticket : ticketList) {
                    ReportDTO report = hraReportMapper.findReportByTicketNo(ticket.getTicketNo());
                    log.debug("=====JsonUtil.objectToJson(report)=====" + JsonUtil.objectToJson(report));
                    if (report != null) {
                        dtoList.add(report);
                    }
                }
            }
            return dtoList;
        } else if (flag == 1) {
            log.info("######获取他人的评估报告列表开始######");
            List<HraReportOtherDTO> otherList = otherMapper.getOtherReportList(uid);
            if (CollectionUtil.isNotEmpty(otherList)) {
                for (HraReportOtherDTO reportOther : otherList) {
                    //处理
                    ReportDTO reportDto = hraReportMapper.findReportByTicketNo(reportOther.getTicketNo());
                    if (reportDto != null) {
                        reportDto.setId(reportOther.getId());
                        dtoList.add(reportDto);
                    }
                }
                return dtoList;
            }
        }
        log.info("#######获取体检券列表resultList：为空！##########");
        throw new NotFoundException("获取体检券列表resultList：为空！");
    }

    @Override
    public ReportDetailDTO getReportDetail(String ticketNo) {
        ReportDetailDTO detailDto = new ReportDetailDTO();
        HraReport report = hraReportMapper.findCustomByTicketNo(ticketNo);
        if (report == null) {
            log.info("*****体检报告为空******");
            throw new NotFoundException("体检报告为空");
        }
        HraReportDTO reportDTO = new HraReportDTO();
        report.convert(reportDTO);

        Integer customerId = reportDTO.getCustomerId();
        log.debug("========customerId=====" + customerId);
        HraCustomer hraCustomer = customerMapper.selectByPrimaryKey(customerId);
        if (hraCustomer == null) {
            log.debug("*****未获取到关联客户******");
            throw new NotFoundException("未获取到关联客户");
        }
        HraCustomerDTO customerDTO = new HraCustomerDTO();
        hraCustomer.convert(customerDTO);

        detailDto.setUsername(customerDTO.getUsername());
        detailDto.setSex(customerDTO.getSex());
        detailDto.setHeight(customerDTO.getHeight());
        detailDto.setWeight(customerDTO.getWeight());
        detailDto.setBirthdate(customerDTO.getBirthdate());
        detailDto.setExamDate(reportDTO.getExamDate());
        detailDto.setReportPdf(reportDTO.getReportPdf());
        //耳鼻喉
        detailDto.setPic2(reportDTO.getPic2());
        //骨骼
        detailDto.setPic3(reportDTO.getPic3());
        //呼吸
        detailDto.setPic4(reportDTO.getPic4());
        //泌尿生殖
        detailDto.setPic5(reportDTO.getPic5());
        //神经
        detailDto.setPic6(reportDTO.getPic6());
        //消化
        detailDto.setPic7(reportDTO.getPic7());
        //心血管
        detailDto.setPic8(reportDTO.getPic8());
        //体检报告id
        Integer reportId = reportDTO.getId();
        log.debug("*******体检报告reportId==" + reportId + "*******");
        HraReportJson hraReportJson = jsonMapper.selectByPrimaryKey(reportId);
        if (hraReportJson == null) {
            log.debug("******查询hraReportJson为空!*******");
            throw new NotFoundException("查询hraReportJson为空!");
        }
        HraReportJsonDTO dto = new HraReportJsonDTO();
        hraReportJson.convert(dto);
        detailDto.setReportJsonDTO(dto);
        return detailDto;
    }

    @Override
    public List<ReportDTO> getAllReport(Integer userId) {
        log.debug("===========进入getAllReport()方法===========");
        List<ReportDTO> dtoList = new ArrayList<>();
        List<HraTicketDTO> tickets = hraTicketService.findTicketListByUserId(userId);
        List<HraReportOtherDTO> otherReportList = otherMapper.getOtherReportList(userId);

        if (CollectionUtil.isNotEmpty(tickets)) {
            //得到体检卡号
            for (HraTicketDTO ticket : tickets) {
                ReportDTO dto = hraReportMapper.findReportByTicketNo(ticket.getTicketNo());
                if (dto != null) {
                    dtoList.add(dto);
                }
            }
        }
        if (CollectionUtil.isNotEmpty(otherReportList)) {
            for (HraReportOtherDTO reportOther : otherReportList) {
                ReportDTO reportByTicketNo = hraReportMapper.findReportByTicketNo(reportOther.getTicketNo());
                if (reportByTicketNo != null) {
                    dtoList.add(reportByTicketNo);
                }
            }
        }

        //模板报告
        dtoList.add(this.modelReport());
        return dtoList;
    }

    @Override
    public List<ReportDetailDTO> getReportDetailList(String[] ticketNos) {
        List<ReportDetailDTO> detailDtoList = new ArrayList<>();
        for (int i = 0; i < ticketNos.length; i++) {
            log.debug("=====ticketNos[i]======" + ticketNos[i]);
            ReportDetailDTO detailDto = new ReportDetailDTO();
            HraReport hraReport = hraReportMapper.findCustomByTicketNo(ticketNos[i]);
            if (hraReport != null) {
                HraCustomer customer = customerMapper.selectByPrimaryKey(hraReport.getCustomerId());
                HraReportJson hraReportJson = jsonMapper.selectByPrimaryKey(hraReport.getId());
                if (hraReportJson == null) {
                    throw new NotFoundException("查询hraReportJson为空!");
                }
                detailDto.setUsername(customer.getUsername());
                detailDto.setSex(customer.getSex());
                detailDto.setHeight(customer.getHeight());
                detailDto.setWeight(customer.getWeight());
                detailDto.setBirthdate(customer.getBirthdate());
                detailDto.setExamDate(hraReport.getExamDate());
                detailDto.setReportPdf(hraReport.getReportPdf());
                HraReportJsonDTO dto = new HraReportJsonDTO();
                hraReportJson.convert(dto);
                detailDto.setReportJsonDTO(dto);
            }
            detailDtoList.add(detailDto);
        }
        return detailDtoList;

    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Object shareReport(Integer fromUserId, String ticketNo, Integer sharedUserId) {
        log.debug("====进入分享体检报告方法,shareReport()====");
        Map<String, Object> map = new HashMap<>(16);
        map.put("ticketNo", ticketNo);
        map.put("sharedUserId", sharedUserId);
        HraReportOther reportOther = otherMapper.findReportByNoAndsharedUserId(map);
        if (reportOther != null) {
            log.debug("======已经存在该报告=======");
            throw new NotFoundException("已经存在该报告");
        }

        UserDTO userDTO = userFeign.getUserById(sharedUserId);
        HraReportOther other = new HraReportOther();
        other.setUserId(sharedUserId);
        other.setShareUserId(fromUserId);
        other.setTicketNo(ticketNo);
        other.setMobile(userDTO.getMobile());
        other.setCreateTime(new Date());
        int i = otherMapper.insert(other);
        log.debug("====操作记录数:i = " + i + "=====");
        if (i > 0) {
            log.debug("===报告添加成功====");
            return i;
        } else {
            throw new NotFoundException("报告添加失败");
        }
    }

    @Override
    public PageVO<ReportDTO> listReport(Integer pageNum, Integer pageSize, String phone) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ReportDTO> page = reportMapper.listReport(phone);
        if (page == null || page.getResult() == null || page.getResult().size() < 1) {
            return null;
        }
        return new PageVO<>(pageNum, page);
    }

    @Override
    public int showReport(String phone, String ticketNo) {
        HraReport hraReport = hraReportMapper.findHraReport(phone, ticketNo, 0);
        if (hraReport == null) {
            hraReport = hraReportMapper.findHraReport(phone, ticketNo, 1);
            if (hraReport == null) {
                return -1;
            }
            return -2;
        }
        hraReport.setShowFlag(1);
        return hraReportMapper.updateByPrimaryKeySelective(hraReport);
    }

    @Override
    public int addReportRecord(String phone, String ticketNo, Integer userId) {
        HraReport hraReport = hraReportMapper.findHraReportRecord(phone, ticketNo, 0);
        if (hraReport == null) {
            return -1;
        }
        //在报告记录查询是否已存在
        List<HraReportRecord> records = hraReportRecordMapper.findHraReportRecord(hraReport.getId(), userId);
        if (CollectionUtil.isNotEmpty(records)) {
            HraReportRecord reportRecord = records.get(0);
            //如果是已删除状态-修改为
            if (reportRecord.getDeleteStatus()) {
                reportRecord.setDeleteStatus(false);
                return hraReportRecordMapper.updateByPrimaryKey(reportRecord);
            }
            //已存在
            return -2;
        }
        HraReportRecord record = new HraReportRecord();
        record.setReportId(hraReport.getId());
        record.setUserId(userId);
        record.setDeleteStatus(false);
        return hraReportRecordMapper.insertSelective(record);
    }

    @Override
    public PageVO<ReportDTO> listReportRecord(Integer pageNum, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ReportDTO> page = reportMapper.listReportRecord(userId);
        if (page == null) {
            return new PageVO<>(pageNum, null);
        }
        return new PageVO<>(pageNum, page);
    }

    @Override
    public void saveReportRecord(Integer userId) {
        //查询已完成，还没添加体检报告的体检卡，自动添加
        List<ReportDTO> hraReport = reportMapper.listByNoSaveRecord(userId);
        if (CollectionUtil.isNotEmpty(hraReport)) {
            List<HraReportRecord> record;
            Example example;
            Example.Criteria criteria;
            for (ReportDTO dto : hraReport) {
                example = new Example(HraReportRecord.class);
                criteria = example.createCriteria();
                criteria.andEqualTo("userId", userId);
                criteria.andEqualTo("reportId", dto.getId());
                record = hraReportRecordMapper.selectByExample(example);
                //如果没有记录，则添加
                if (CollectionUtil.isEmpty(record)) {
                    HraReportRecord reportRecord = new HraReportRecord();
                    reportRecord.setReportId(dto.getId());
                    reportRecord.setUserId(userId);
                    reportRecord.setDeleteStatus(false);
                    hraReportRecordMapper.insertSelective(reportRecord);
                }
            }
        }
    }

    @Override
    public Integer deleteRecord(Integer reportRecordId) {
        HraReportRecord hraReportRecord = hraReportRecordMapper.selectByPrimaryKey(reportRecordId);
        if (hraReportRecord != null) {
            hraReportRecord.setDeleteStatus(true);
            return hraReportRecordMapper.updateByPrimaryKey(hraReportRecord);
        }
        throw new YimaoException("删除体检报告失败");
    }

    /**
     * 获取模板体检报告
     *
     * @return Y357299499875550
     */
    private ReportDTO modelReport() {
        Map<String, String> map = new HashMap<>(8);
        map.put("ticketNo", HraConstant.TEMPLATE_TICKET_NO);
        ReportDTO tempReport = hraReportMapper.findReportByMap(map);
        if (tempReport != null) {
            tempReport.setFlag(0);
            return tempReport;
        }
        return null;
    }
}
