package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.CustomerAssistantTypeDTO;
import com.yimao.cloud.system.po.CustomerAssistantType;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author liuhao@yimaokeji.com
 * 2018052018/5/14
 */
public interface CustomerAssistantTypeMapper extends Mapper<CustomerAssistantType> {

    /**
     * 查询问答类型
     *
     * @param typeName
     * @return page
     */
    Page<CustomerAssistantTypeDTO> listCustomerAssistantType(@Param("typeName") String typeName);

    CustomerAssistantType getCustomerAssistantTypeByTypeCode(@Param("typeCode") Integer typeCode);//

    CustomerAssistantType getCustomerAssistantTypeBySorts(@Param("sorts") Integer sorts);//

    List<CustomerAssistantTypeDTO> getClassifyList(@Param("terminal") Integer terminal);//

    Page<CustomerAssistantTypeDTO> page(@Param("terminal") Integer terminal, @Param("typeName") String typeName);
    
    /**
     * 获取排序第一的站务系统问答分类
     * @return
     */
	CustomerAssistantType getFirstSortStationAssistantType();
}
