package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 客服问答
 *
 * @author liuhao@yimaokeji.com
 *         2018052018/5/14
 */
@Data
@ApiModel(description = "客服问答")
public class CustomerAssistantDTO implements Serializable {
    private static final long serialVersionUID = -6120373476486785919L;

    @ApiModelProperty(position = 1, value = "客服问答ID")
    private Integer id;
    @ApiModelProperty(position = 2, value = "类型")
    private List<Integer> typeCodes;
    @ApiModelProperty(position = 2, value = "类型")
    private Integer typeCode;
    @ApiModelProperty(position = 3, value = "问题")
    private String questions;
    @ApiModelProperty(position = 4, value = "答案")
    private String answers;
    @ApiModelProperty(position = 5, value = "是否删除  1-未删除 0-已经删除")
    private Integer deleteFlag;
    @ApiModelProperty(position = 6, value = "排序")
    private Integer sorts;
    @ApiModelProperty(position = 7, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 8, value = "是否推荐  1-是  0-否")
    private Integer recommend;
    @ApiModelProperty(position = 9, value = "展示端/1-服务站站务系统、2-经销商app、3-健康E家公众号")
    private Integer terminal;
    @ApiModelProperty(position = 10, value = "是否发布：1已经发布 0未发布")
    private Integer publish;
    @ApiModelProperty(position = 11, value = "类型名称")
    private String typeName;
    @ApiModelProperty(position = 12, value = "返回类型")
    private String returnTypeCode;
    @ApiModelProperty(position = 13, value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(position = 14, value = "附件")
    private String attachment;
}
