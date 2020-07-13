package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.pojo.dto.system.CustomerMessageDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.CustomerMessageMapper;
import com.yimao.cloud.system.mapper.DictionaryMapper;
import com.yimao.cloud.system.service.CustomerMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author liuhao@yimaokeji.com
 * @date 2018/5/17
 */
@Service
@Slf4j
public class CustomerMessageServiceImpl implements CustomerMessageService {

    @Resource
    private CustomerMessageMapper customerMessageMapper;
    @Resource
    private DictionaryMapper dictionaryMapper;


    /**
     * 分页查询留言列表
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
     * @return
     */
    @Override
    public PageVO<CustomerMessageDTO> listCustomerMessage(String province, String city, String region, String customerName, String mobile, Integer joinType, String beginTime, String endTime, Integer terminal, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<CustomerMessageDTO> customerMessageDTOS = customerMessageMapper.listCustomerMessage(province, city, region, customerName, mobile, joinType, beginTime, endTime, terminal);
        return new PageVO<>(pageNum, customerMessageDTOS);
    }

    /* *//**
     * 加盟类型
     *
     * @param joinType
     * @return
     *//*
    private String swJoinType(Integer joinType) {
        Example exam = new Example(Dictionary.class);
        Example.Criteria criteria = exam.createCriteria();
        criteria.andEqualTo("dicType", "joinType");
        criteria.andEqualTo("conditions", 0);
        criteria.andEqualTo("dicCode", joinType);
        List<Dictionary> dPage = dictionaryMapper.selectByExample(exam);
        if (CollectionUtil.isNotEmpty(dPage)) {
            return dPage.get(0).getDicValue();
        }
        return "";
    }*/

    /*@Override
    public CustomerMessage saveCustomerMessage(CustomerMessage customerMessage) {
        if (customerMessage == null || customerMessage.getCustomerName() == null || customerMessage.getMobile() == null) {
            throw new BadRequestException("姓名或手机号不能为空");
        }

        customerMessage.setCreateTime(new Date());
        customerMessageMapper.insertSelective(customerMessage);
        return customerMessage;
    }*/
}
