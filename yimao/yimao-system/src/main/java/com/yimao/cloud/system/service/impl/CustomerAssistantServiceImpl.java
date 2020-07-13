package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.station.StationHelperDataDTO;
import com.yimao.cloud.pojo.dto.system.CustomerAssistantDTO;
import com.yimao.cloud.pojo.dto.system.CustomerAssistantTypeDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.CustomerAssistantMapper;
import com.yimao.cloud.system.mapper.CustomerAssistantTypeMapper;
import com.yimao.cloud.system.po.CustomerAssistant;
import com.yimao.cloud.system.po.CustomerAssistantType;
import com.yimao.cloud.system.service.CustomerAssistantService;
import lombok.Data;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author lizhiqiang
 * @date 2019/1/14
 */
@Service
@Data
public class CustomerAssistantServiceImpl implements CustomerAssistantService {

    @Resource
    private CustomerAssistantMapper assistantMapper;
    @Resource
    private CustomerAssistantTypeMapper assistantTypeMapper;


    /**
     * 分页查询问答列表
     *
     * @param typeCode  code
     * @param questions 问题
     * @param
     * @param recommend 推荐
     * @param pageNum   页码
     * @param pageSize  页数
     * @return
     */
    @Override
    public PageVO<CustomerAssistantDTO> listCustomerAssistant(Integer typeCode, String questions, Integer publish, Integer recommend, Integer terminal, String typeName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<CustomerAssistantDTO> page = assistantMapper.listCustomerAssistant(typeCode, typeName, questions, publish, recommend, terminal);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 新增客服问答
     *
     * @param customerAssistantDTO 问答
     * @return
     */
    @Override
    public void saveCustomerAssistant(CustomerAssistantDTO customerAssistantDTO) {
        if (StringUtil.isEmpty(customerAssistantDTO.getAnswers()) || customerAssistantDTO.getTypeCodes() == null) {
            throw new YimaoException("信息不全请检查");
        }
        CustomerAssistant customerAssistant = null;
        List<Integer> typeCodesList = customerAssistantDTO.getTypeCodes();
        for (Integer typeCode : typeCodesList) {
            customerAssistant = new CustomerAssistant(customerAssistantDTO);
            customerAssistant.setTypeCode(typeCode);
            customerAssistant.setPublish(1);
            customerAssistant.setDeleteFlag(0);
            customerAssistant.setCreateTime(new Date());
            //获取展示端
            CustomerAssistantType customerAssistantType = new CustomerAssistantType();
            customerAssistantType.setTypeCode(typeCode);
            CustomerAssistantType query = assistantTypeMapper.selectOne(customerAssistantType);

            customerAssistant.setTerminal(query.getTerminal());
            assistantMapper.insertSelective(customerAssistant);
        }
    }

    /**
     * 修改客服问答
     *
     * @param customerAssistant 问答
     * @return
     */
    @Override
    public void updateCustomerAssistant(CustomerAssistant customerAssistant) {
        if (customerAssistant == null || customerAssistant.getId() == null) {
            throw new BadRequestException("对象不存在");
        }
        customerAssistant.setCreateTime(new Date());
        assistantMapper.updateByPrimaryKeySelective(customerAssistant);
    }

    /**
     * 逻辑删除问答
     *
     * @param id 问答
     */
    @Override
    public void removeCustomerAssistant(Integer id) {
        CustomerAssistant customerAssistant = assistantMapper.selectByPrimaryKey(id);
        if (customerAssistant.getDeleteFlag() == 0) {
            customerAssistant.setDeleteFlag(1);
        } else {
            customerAssistant.setDeleteFlag(1);
        }
        customerAssistant.setCreateTime(new Date());
        assistantMapper.updateByPrimaryKeySelective(customerAssistant);
    }

    /**
     * 设置推荐
     *
     * @param id
     * @return
     */
    @Override
    public CustomerAssistantDTO recommendCustomerAssistant(Integer id) {
        CustomerAssistant customerAssistant = assistantMapper.selectByPrimaryKey(id);
        if (customerAssistant != null) {
            if (customerAssistant.getRecommend() == 1) {
                customerAssistant.setRecommend(0);
            } else {
                customerAssistant.setRecommend(1);
            }
            customerAssistant.setCreateTime(new Date());
            assistantMapper.updateByPrimaryKeySelective(customerAssistant);
            CustomerAssistantDTO dto = new CustomerAssistantDTO();
            customerAssistant.convert(dto);
            return dto;
        } else {
            throw new NotFoundException("对象不存在！");
        }
    }


    /**
     * 问答发布
     *
     * @param id 问答
     */
    @Override
    public void issueCustomerAssistant(Integer id) {
        CustomerAssistant customerAssistant = assistantMapper.selectByPrimaryKey(id);
        if (customerAssistant != null) {
            if (customerAssistant.getPublish() == 1) {
                customerAssistant.setPublish(0);
            } else {
                customerAssistant.setPublish(1);
            }
            customerAssistant.setCreateTime(new Date());
            assistantMapper.updateByPrimaryKeySelective(customerAssistant);
            CustomerAssistantDTO dto = new CustomerAssistantDTO();
            customerAssistant.convert(dto);
        } else {
            throw new NotFoundException("对象不存在！");
        }

    }

    @Override
    public List<CustomerAssistantTypeDTO> listAssistant(Integer terminal) {
        List<CustomerAssistantTypeDTO> assistantTypesDTO = new ArrayList<>();
        CustomerAssistantTypeDTO typeDTO = new CustomerAssistantTypeDTO();
        typeDTO.setTypeCode(0);
        typeDTO.setId(9999);
        typeDTO.setTypeName("热门咨询");
        typeDTO.setDeleteFlag(false);
        typeDTO.setCustomerAssistantList(this.listRecommend(terminal));
        assistantTypesDTO.add(typeDTO);
        assistantTypesDTO.addAll(this.listAssistantType(terminal));
        return assistantTypesDTO;
    }


    private List<CustomerAssistantTypeDTO> listAssistantType(Integer terminal) {
        Example example = new Example(CustomerAssistantType.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("terminal", terminal);
        criteria.andEqualTo("deleteFlag", 0);
        List<CustomerAssistantTypeDTO> customerAssistantTypes = new ArrayList<>();
        CustomerAssistantTypeDTO assistantTypeDTO;
        List<CustomerAssistantType> assistantTypes = assistantTypeMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(assistantTypes)) {
            for (CustomerAssistantType type : assistantTypes) {
                assistantTypeDTO = new CustomerAssistantTypeDTO();
                type.convert(assistantTypeDTO);
                assistantTypeDTO.setCustomerAssistantList(this.assistantDTOList(type.getTypeCode()));
                customerAssistantTypes.add(assistantTypeDTO);
            }
        }
        return customerAssistantTypes;
    }

    /**
     * 根据typeCode获取客服问答
     *
     * @param typeCode
     * @return
     */
    private List<CustomerAssistantDTO> assistantDTOList(Integer typeCode) {
        Example example = new Example(CustomerAssistant.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("typeCode", typeCode);
        criteria.andEqualTo("deleteFlag", 0);
        criteria.andEqualTo("publish", 1);
        List<CustomerAssistantDTO> customerAssistantTypes = new ArrayList<>();
        CustomerAssistantDTO assistantTypeDTO;
        List<CustomerAssistant> assistantTypes = assistantMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(assistantTypes)) {
            for (CustomerAssistant assistant : assistantTypes) {
                assistantTypeDTO = new CustomerAssistantDTO();
                assistant.convert(assistantTypeDTO);
                customerAssistantTypes.add(assistantTypeDTO);
            }
            return customerAssistantTypes;
        }
        return customerAssistantTypes;
    }

    /**
     * 热门
     *
     * @return list
     */
    private List<CustomerAssistantDTO> listRecommend(Integer terminal) {
        return assistantMapper.listRecommend(terminal);
    }

    /**
     * 批量逻辑删除
     *
     * @param ids
     */
    @Override
    public void batchRemoveCustomerAssistant(List<Integer> ids) {
        Example example = new Example(CustomerAssistant.class);
        example.createCriteria().andIn("id", ids);
        CustomerAssistant customerAssistant = new CustomerAssistant();
        customerAssistant.setDeleteFlag(1);
        customerAssistant.setUpdateTime(new Date());
        int i = assistantMapper.updateByExampleSelective(customerAssistant, example);
        if (i < 1) {
            throw new YimaoException("批量删除失败");
        }

    }

    /**
     * 批量发布
     *
     * @param ids
     */
    @Override
    public void batchRecommendCustomerAssistant(List<Integer> ids) {
        Example example = new Example(CustomerAssistant.class);
        example.createCriteria().andIn("id", ids);
        CustomerAssistant customerAssistant = new CustomerAssistant();
        customerAssistant.setPublish(1);
        customerAssistant.setUpdateTime(new Date());
        int i = assistantMapper.updateByExampleSelective(customerAssistant, example);
        if (i < 1) {
            throw new YimaoException("批量发布失败");
        }
    }
    
    /**
     *  站务系统-帮助中心-更多-根据分类查询所有问题列表
     */
	public PageVO<CustomerAssistantDTO> helpCenterMoreByTypeCode(Integer pageNum, Integer pageSize, Integer typeCode) {
		PageHelper.startPage(pageNum, pageSize);
        Page<CustomerAssistantDTO> page = assistantMapper.helpCenterMoreByTypeCode(typeCode);
        return new PageVO<>(pageNum, page);
	}

	/**
	 * 站务系统-帮助中心-默认类型推荐列表
	 * @return
	 */
	public List<StationHelperDataDTO> helpCenterData() {
		
		return assistantMapper.helpCenterData();
	}

	/**
	 * 站务系统-帮助中心根据关键字查询问题列表
	 * @param pageNum
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public PageVO<CustomerAssistantDTO> helpCenterQuestionSearchList(Integer pageNum, Integer pageSize, String keywords) {
		PageHelper.startPage(pageNum, pageSize);
		Page<CustomerAssistantDTO> page = assistantMapper.helpCenterQuestionSearchList(keywords);
        return new PageVO<>(pageNum, page);
	}

	/**
	 * 站务系统-全局帮助与服务展示列表(展示问答分类排序第一下的前五推荐问答)
	 */
	public List<CustomerAssistantDTO> getHelpAndServiceList() {
		
		//获取排序第一的问答分类
		CustomerAssistantType type = assistantTypeMapper.getFirstSortStationAssistantType();
		
		if(Objects.isNull(type)) {
			return null;
		}
		
		if(Objects.isNull(type.getTypeCode())) {
			return null;
		}
		return assistantMapper.getHelpAndServiceList(type.getTypeCode());
	}
}
