package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.station.StationHelperDataDTO;
import com.yimao.cloud.pojo.dto.system.CustomerAssistantDTO;
import com.yimao.cloud.pojo.dto.system.CustomerAssistantTypeDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.CustomerAssistant;

import java.util.List;

/**
 * @@author lizhiqiang
 * @date 2019/1/14
 */
public interface CustomerAssistantService {

    /**
     * 分页查询问答列表
     *
     * @param typeCode   code
     * @param questions  问题
     * @param publish 状态
     * @param recommend  推荐
     * @param pageNum    页码
     * @param pageSize   页数
     * @return page
     */
    PageVO<CustomerAssistantDTO> listCustomerAssistant(Integer typeCode, String questions, Integer publish, Integer recommend, Integer terminal, String typeName, Integer pageNum, Integer pageSize);

    /**
     * 新增问答
     *
     * @param customerAssistantDTO 问答
     * @return Boolean
     */
    void saveCustomerAssistant(CustomerAssistantDTO customerAssistantDTO);

    /**
     * 修改问答
     *
     * @param customerAssistant 问答
     * @return Boolean
     */
    void updateCustomerAssistant(CustomerAssistant customerAssistant);

    /**
     * 删除问答（逻辑）
     *
     * @param id 问答
     */
    void removeCustomerAssistant(Integer id);

    /**
     * 问答发布
     *
     * @param id 问答
     */
    void issueCustomerAssistant(Integer id);

    /**
     * 设为推荐
     *
     * @param id 编码
     * @return Boolean
     */
    CustomerAssistantDTO recommendCustomerAssistant(Integer id);

    /**
     * app:查询问答列表
     *
     * @return list
     */
    List<CustomerAssistantTypeDTO> listAssistant(Integer terminal);


    /**
     * 逻辑删除问答（批量）
     *
     * @param ids
     */
    void batchRemoveCustomerAssistant(List<Integer> ids);

    /**
     * 批量发布
     *
     * @param ids
     */
    void batchRecommendCustomerAssistant(List<Integer> ids);
    
    /**
     * 站务系统-帮助中心-更多-根据分类查询所有问题列表
     * @param pageNum
     * @param pageSize
     * @param typeCode
     * @return
     */
	PageVO<CustomerAssistantDTO> helpCenterMoreByTypeCode(Integer pageNum, Integer pageSize, Integer typeCode);

	/**
	 * 站务系统-帮助中心-默认类型推荐列表
	 * @return
	 */
	List<StationHelperDataDTO> helpCenterData();

	/**
	 * 站务系统-帮助中心根据关键字查询问题列表
	 * @param pageNum
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	PageVO<CustomerAssistantDTO> helpCenterQuestionSearchList(Integer pageNum, Integer pageSize, String keywords);

	/**
	 * 站务系统-全局帮助与服务展示列表(展示问答分类排序第一下的前五推荐问答)
	 * @return
	 */
	List<CustomerAssistantDTO> getHelpAndServiceList();
}
