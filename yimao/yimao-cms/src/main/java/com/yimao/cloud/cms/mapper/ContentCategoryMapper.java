package com.yimao.cloud.cms.mapper;

import com.yimao.cloud.cms.po.ContentCategory;
import com.yimao.cloud.pojo.dto.cms.CategoryRelationDTO;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 *
 * 内容分类信息
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
public interface ContentCategoryMapper extends Mapper<ContentCategory> {

    /**
     * @description 批量保存内容分类关系
     * @author liulin
     * @date 2019/1/28 18:27
     * @param result
     * @return java.lang.Integer
    */
    Integer batchSave(@Param("result") List result);

    /**
     * @description 查询关系数组
     * @author liulin
     * @date 2019/1/28 18:27
     * @param contentId
     * @return java.lang.Integer[]
    */
    Integer[] findByContentId(@Param("contentId") Integer contentId);


    /**
     * @description 查询内容分类管理信息
     * @author liulin
     * @date 2019/1/29 11:04
     * @param contentId
     * @return java.util.List<com.yimao.cloud.pojo.dto.cms.CategoryRelationDTO>
    */
    List<CategoryRelationDTO> findContentRelationInfo(@Param("contentId") Integer contentId);

    CategoryRelationDTO findContentCategoryInfo(Integer id);
}
