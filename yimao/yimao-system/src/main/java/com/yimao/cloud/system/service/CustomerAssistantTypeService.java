package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.CustomerAssistantTypeDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.CustomerAssistantType;

import java.util.List;

/**
 * @author lizhiqiang
 * @date 2019-1-15
 */
public interface CustomerAssistantTypeService {

    /**
     * 新增客服类型
     *
     * @param customerAssistantType 客服类型
     * @return Boolean
     */
    void saveCustomerAssistantType(CustomerAssistantType customerAssistantType);

    /**
     * 修改客服类型
     *
     * @param customerAssistantType 客服类型
     * @return Boolean
     */
    CustomerAssistantType updateCustomerAssistantType(CustomerAssistantType customerAssistantType);

    /**
     * 获取展示端下所有分类
     *
     * @param terminal
     * @return
     */
    List<CustomerAssistantTypeDTO> getClassifyList(Integer terminal);

    /**
     * 删除展示端分类
     *
     * @param id
     */
    void deleteClassifyById(Integer id);

    /**
     * 问答分类分页展示
     *
     * @param pageNum
     * @param pageSize
     * @param terminal
     * @param typeName
     * @return
     */
    PageVO<CustomerAssistantTypeDTO> page(Integer pageNum, Integer pageSize, Integer terminal, String typeName);
}
