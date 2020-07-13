package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.system.CustomerAssistantTypeDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.CustomerAssistantTypeMapper;
import com.yimao.cloud.system.po.CustomerAssistantType;
import com.yimao.cloud.system.service.CustomerAssistantTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

/**
 * @author liuhao@yimaokeji.com
 * @date 2018/5/16
 */
@Service
@Slf4j
public class CustomerAssistantTypeServiceImpl implements CustomerAssistantTypeService {

    @Resource
    private CustomerAssistantTypeMapper assistantTypeMapper;

    /**
     * 添加问答分类
     *
     * @param customerAssistantType 客服类型
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void saveCustomerAssistantType(CustomerAssistantType customerAssistantType) {
        //检查code是否已经存在
        Random random = new Random();
        customerAssistantType.setTypeCode(random.nextInt(100));
        CustomerAssistantType customerAssistantTypeByTypeCode = assistantTypeMapper.getCustomerAssistantTypeByTypeCode(customerAssistantType.getTypeCode());
        if (customerAssistantTypeByTypeCode != null) {
            throw new YimaoException("类型值已存在");
        }
        customerAssistantType.setDeleteFlag(false);
        assistantTypeMapper.insert(customerAssistantType);
        customerAssistantType.setTypeCode(customerAssistantType.getId());
        System.out.println(customerAssistantType.getTypeCode());
        int insert = assistantTypeMapper.updateByPrimaryKeySelective(customerAssistantType);
        if (insert < 1) {
            throw new YimaoException("插入失败");
        }

    }


    /**
     * 修改客服分类
     *
     * @param customerAssistantType 客服类型
     * @return
     */
    @Override
    public CustomerAssistantType updateCustomerAssistantType(CustomerAssistantType customerAssistantType) {
        CustomerAssistantType customerAssistantType1 = assistantTypeMapper.selectByPrimaryKey(customerAssistantType.getId());
        if (customerAssistantType1 == null) {
            throw new YimaoException("该分类不存在");
        }
        assistantTypeMapper.updateByPrimaryKeySelective(customerAssistantType);
        return customerAssistantType;
    }

    /**
     * 获取相应展示端分类
     *
     * @param terminal
     * @return
     */
    @Override
    public List<CustomerAssistantTypeDTO> getClassifyList(Integer terminal) {
        List<CustomerAssistantTypeDTO> classifyList = assistantTypeMapper.getClassifyList(terminal);
        if (classifyList == null) {
            throw new YimaoException("该展示端下无分类");
        }
        return classifyList;
    }


    /**
     * 删除分类
     *
     * @param id
     */
    @Override
    public void deleteClassifyById(Integer id) {
        CustomerAssistantType customerAssistantType = assistantTypeMapper.selectByPrimaryKey(id);
        if (customerAssistantType == null) {
            throw new YimaoException("未找到该分类");
        }
        System.out.println(customerAssistantType.getDeleteFlag());
        customerAssistantType.setDeleteFlag(true);
        System.out.println(customerAssistantType.getDeleteFlag());
        int i = assistantTypeMapper.updateByPrimaryKey(customerAssistantType);
        if (i < 1) {
            throw new YimaoException("删除失败");
        }
    }

    /**
     * 分页查询问答分类
     *
     * @param pageNum
     * @param pageSize
     * @param terminal
     * @param typeName
     * @return
     */
    @Override
    public PageVO<CustomerAssistantTypeDTO> page(Integer pageNum, Integer pageSize, Integer terminal, String typeName) {
        PageHelper.startPage(pageNum, pageSize);
        Page<CustomerAssistantTypeDTO> page = assistantTypeMapper.page(terminal, typeName);
        return new PageVO<>(pageNum, page);
    }

}
