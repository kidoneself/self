package com.yimao.cloud.pojo.dto.cms;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * 内容表，用于存放比如文章
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@ApiModel(description = "分类关系")
public class CategoryRelationDTO implements Serializable {
    private static final long serialVersionUID = 3559658819214876289L;

    private Integer platform;

    private Integer categoryId;

    private String categoryName;

    private String parentCategoryName;

    private Integer level;

    private Integer parentId;


}
