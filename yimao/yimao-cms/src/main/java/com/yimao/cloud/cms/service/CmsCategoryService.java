package com.yimao.cloud.cms.service;

import com.yimao.cloud.cms.po.CmsCategory;
import com.yimao.cloud.pojo.dto.cms.CmsCategoryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import java.util.List;

/**
 * (CmsCategory)表服务接口
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
public interface CmsCategoryService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    CmsCategory queryById(Integer id);


    /**
     *  新增 card
     * @param cmsCategory
     * @return
     */
    Integer add(CmsCategory cmsCategory);


    /**
     * @param
     * @param cmsCategory
     * @return
     */
    Integer batchAdd(CmsCategory cmsCategory);


    /**
     *  修改
     * @param cmsCategory
     * @return
     */
    Integer update(CmsCategory cmsCategory);


    /**
     *  批量更新
     * @param list
     * @param cmsCategory
     * @return
     */
    Integer batchUpdate(List<Integer> list,CmsCategory cmsCategory);


    /**
     * @param  id
     * @return
     */
    Integer delete(Integer id);

    /**
     *  分页查询
     * @param pageNum
     * @param pageSize
     * @param cmsCategory
     * @return
     */
    PageVO<CmsCategoryDTO> findPage(Integer pageNum,Integer pageSize,CmsCategoryDTO cmsCategory);

    /**
     * 获取所有以及类目
     * * @return
     */
    List<CmsCategoryDTO> getCategory(CmsCategoryDTO cmsCategory);

    CmsCategoryDTO getTopCategoryName(Integer id);

    /**
     * 站务系统- 概况-消息通知-总部消息-消息分类筛选项获取
     * @return
     */
	List<CmsCategoryDTO> getHeadOfficeMessageType();
}
