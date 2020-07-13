package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.CustomerMessageDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.CustomerMessage;

/**
 * @author liuhao@yimaokeji.com
 *         2018052018/5/17
 */
public interface CustomerMessageService {

    /**
     * 客户消息列表
     *
     * @param province     省
     * @param city         市
     * @param region       区
     * @param customerName 名称
     * @param mobile       手机号
     * @param joinType     加盟类型
     * @param beginTime    开始时间
     * @param endTime      结束时间
     * @param pageNum      页码
     * @param pageSize     页数
     * @return page
     */
    PageVO<CustomerMessageDTO> listCustomerMessage(String province, String city, String region, String customerName, String mobile, Integer joinType, String beginTime, String endTime, Integer terminal, Integer pageNum, Integer pageSize);

    /*  *//**
     * 保存客户留言
     *
     * @param customerMessage 留言
     * @return bean
     *//*
    CustomerMessage saveCustomerMessage(CustomerMessage customerMessage);*/
}
