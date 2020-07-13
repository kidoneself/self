package com.yimao.cloud.hra.service;

import com.yimao.cloud.pojo.dto.hra.HraReportOtherDTO;

/**
 * @author Zhang Bo
 * @date 2018/10/31.
 */
public interface HraReportOtherService {

    /**
     * @param userId   用户ID
     * @param ticketNo 体检卡号
     * @param mobile   手机号
     * @Description: 添加体检报告
     * @author ycl
     * @return: HraReportOtherDTO
     */
    HraReportOtherDTO addOtherReport(Integer userId, String ticketNo, String mobile);

    /**
     * @param ticketId
     * @Description: 删除他人体检报告
     * @author ycl
     * @Return: java.lang.Integer
     * @Create: 2019/4/22 13:51
     */
    Integer deleteReport(Integer ticketId);
}
