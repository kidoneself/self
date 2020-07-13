package com.yimao.cloud.cms.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.cms.po.CmsCategory;
import com.yimao.cloud.pojo.dto.cms.CmsCategoryDTO;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * 视频 内容分类
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
public interface CmsCategoryMapper extends Mapper<CmsCategory> {

    /**
     * 批量新增
     *
     * @param
     * @param cmsCategory
     * @return
     */
    Integer batchAdd(CmsCategory cmsCategory);


    /**
     * 分页查询
     *
     * @param cmsCategory
     * @return
     */
    Page<CmsCategoryDTO> findPage(CmsCategoryDTO cmsCategory);

    Integer checkout(Integer id);

    List<CmsCategoryDTO> getCategory(CmsCategoryDTO cmsCategory);

    /**
     * 站务系统- 概况-消息通知-总部消息-消息分类筛选项获取
     * @return
     */
	List<CmsCategoryDTO> getHeadOfficeMessageType();

}
