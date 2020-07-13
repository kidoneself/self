package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.station.StationHelperDataDTO;
import com.yimao.cloud.pojo.dto.system.CustomerAssistantDTO;
import com.yimao.cloud.system.po.CustomerAssistant;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author liuhao@yimaokeji.com
 * 2018052018/5/14
 */
public interface CustomerAssistantMapper extends Mapper<CustomerAssistant> {

    /**
     * 分页查询客服问答列表
     *
     * @param typeCode   代码
     * @param questions  问题
     * @param publish 是否显示
     * @param recommend  是否推荐
     * @return
     */
    Page<CustomerAssistantDTO> listCustomerAssistant(@Param("typeCode") Integer typeCode,
                                                     @Param("typeName") String typeName,
                                                     @Param("questions") String questions,
                                                     @Param("publish") Integer publish,
                                                     @Param("recommend") Integer recommend,
                                                     @Param("terminal") Integer terminal);

    /**
     * 热门的客服问答
     *
     * @return list
     */
    List<CustomerAssistantDTO> listRecommend(@Param("terminal") Integer terminal);
    

	Page<CustomerAssistantDTO> helpCenterMoreByTypeCode(@Param("typeCode")Integer typeCode);

	List<StationHelperDataDTO> helpCenterData();

	Page<CustomerAssistantDTO> helpCenterQuestionSearchList(String keywords);

	List<CustomerAssistantDTO> getHelpAndServiceList(Integer typeCode);
}
