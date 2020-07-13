package com.yimao.cloud.hra.service.impl;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.hra.mapper.HraCustomerMapper;
import com.yimao.cloud.hra.mapper.HraReportOtherMapper;
import com.yimao.cloud.hra.po.HraCustomer;
import com.yimao.cloud.hra.po.HraReportOther;
import com.yimao.cloud.hra.service.HraReportOtherService;
import com.yimao.cloud.hra.service.HraTicketService;
import com.yimao.cloud.pojo.dto.hra.HraReportOtherDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 他人体检报告 实现类
 * @author: yu chunlei
 * @create: 2018-05-10 15:23:12
 **/
@Service
@Slf4j
public class HraReportOtherServiceImpl implements HraReportOtherService {

    @Resource
    private HraReportOtherMapper hraReportOtherMapper;
    @Resource
    private HraTicketService hraTicketService;
    @Resource
    private HraCustomerMapper hraCustomerMapper;


    @Override
    public HraReportOtherDTO addOtherReport(Integer userId, String ticketNo, String mobile) {
        log.info("userId=" + userId + ",ticketNo=" + ticketNo + ",mobile=" + mobile);
        HraTicketDTO hraTicket = hraTicketService.findByTicketNoAndStatus(ticketNo);
        if (hraTicket == null) {
            log.info("######该体检卡未使用或不存在######");
            throw new NotFoundException("该体检卡未使用或不存在");
        }

        //校验体检卡号和手机号是否一致
        HraCustomer hraCustomer = hraCustomerMapper.selectByPrimaryKey(hraTicket.getCustomerId());
        if (hraCustomer != null) {
            if (!mobile.equals(hraCustomer.getPhone())) {
                throw new NotFoundException("体检卡号和手机号不一致");
            }
        }

        List<HraTicketDTO> hraTicketList = hraTicketService.findTicketListByUserId(userId);
        if (CollectionUtil.isNotEmpty(hraTicketList)) {
            for (HraTicketDTO ticket : hraTicketList) {
                if (ticket.getTicketNo().equals(ticketNo)) {
                    log.info("*******不能添加自己的体检卡*******");
                    throw new NotFoundException("不能添加自己的体检报告!");
                }
            }
        }

        Map<String, Object> map = new HashMap<>(8);
        map.put("ticketNo", ticketNo);
        map.put("userId", userId);
        HraReportOther report = hraReportOtherMapper.getOtherReportByNoAndUid(map);
        if (report != null) {
            throw new NotFoundException("不能添加重复报告!");
        }

        HraReportOther reportOther = new HraReportOther();
        reportOther.setUserId(userId);
        reportOther.setTicketNo(ticketNo);
        reportOther.setMobile(mobile);
        reportOther.setCreateTime(new Date());
        int i = hraReportOtherMapper.insert(reportOther);
        log.info("========变更记录数,i" + i + "===========");
        if (i < 1) {
            log.info("**********添加报告失败,操作记录数i=" + i + "*********");
            throw new NotFoundException("添加报告失败!");
        }
        HraReportOtherDTO otherDTO = new HraReportOtherDTO();
        reportOther.convert(otherDTO);
        return otherDTO;
    }


    @Override
    public Integer deleteReport(Integer ticketId) {
        log.debug("======进入删除他人体检报告方法deleteReport()======");
        return hraReportOtherMapper.deleteByPrimaryKey(ticketId);
    }
}
