package com.yimao.cloud.cms.po;

import lombok.Data;

import javax.persistence.Table;

/**
 * 视频
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@Table(name = "t_content__category")
public class ContentCategory {

    private Integer contentId;
    private Integer categoryId;
}