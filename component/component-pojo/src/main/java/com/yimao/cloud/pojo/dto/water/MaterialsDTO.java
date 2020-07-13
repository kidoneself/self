package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 物料
 *
 * @author Chen Hui Yang
 * @date 2019/2/15
 */
@Data
@ApiModel(description = "物料")
public class MaterialsDTO implements Serializable {

    private static final long serialVersionUID = 1573836755485035727L;

    @ApiModelProperty(position = 1,value = "id")
    private Integer id;
    @ApiModelProperty(position = 2,value = "物料名称")
    private String name;
    @ApiModelProperty(position = 3,value = "物料文件大小（单位：KB）")
    private Integer size;
    @ApiModelProperty(position = 4,value = "广告时长（单位：秒）")
    private Integer duration;
    @ApiModelProperty(position = 5,value = "屏幕位置：1-大屏；2-小屏")
    private Integer screenLocation;
    @ApiModelProperty(position = 6,value = "关联图")
    private String image;
    @ApiModelProperty(position = 7,value = "广告主名称")
    private String advertisers;
    @ApiModelProperty(position = 8,value = "物料地址")
    private String url;
    @ApiModelProperty(position = 11,value = "删除状态：0-未删除；1-删除")
    private Boolean deleted;
    @ApiModelProperty(position = 12, value = "上传开始时间")
    private Date upStartTime;
    @ApiModelProperty(position = 13, value = "上传结束时间")
    private Date upEndTime;
    @ApiModelProperty(position = 14,value = "物料类型：1-视频；2-图片;3-链接")
    private Integer materielType;
    @ApiModelProperty(position = 15, value = "物料说明")
    private String description;
    @ApiModelProperty(position = 16, value = "支付审核(0-未审核，1-审核通过，2-审核不通过)")
    private Integer payAudit;
    @ApiModelProperty(position = 17, value = "内容审核(0-未审核，1-审核通过，2-审核不通过)")
    private Integer contentAudit;
    @ApiModelProperty(position = 18, value = "规格审核  (0-未审核，1-审核通过，2-审核不通过)")
    private Integer specificationAudit;
    @ApiModelProperty(position = 19, value = "关联图大小(单位：KB)")
    private Integer imageSize;
    @ApiModelProperty(position = 20, value = "支付审核不通过原因")
    private String payAuditReason;
    @ApiModelProperty(position = 21, value = "内容审核不通过原因")
    private String contentAuditReason;
    @ApiModelProperty(position = 22, value = "规格审核不通过原因")
    private String specificationAuditReason;

    @ApiModelProperty(position = 100, value = "创建者")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新者")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;
}
