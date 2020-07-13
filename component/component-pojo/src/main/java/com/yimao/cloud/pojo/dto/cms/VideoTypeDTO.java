package com.yimao.cloud.pojo.dto.cms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/21
 */
@Setter
@Getter
@ToString
@ApiModel(description = "视频类型DTO")
public class VideoTypeDTO implements Serializable {

    @ApiModelProperty(value = "ID", position = 1)
    private Integer id;
    @ApiModelProperty(value = "视频分类名称", position = 2)
    private String name;    //
    @ApiModelProperty(value = "父类id", position = 3)
    private Integer parentId;
    @ApiModelProperty(value = "等级", position = 4)
    private Integer level;
    @ApiModelProperty(value = "端 1-终端app；2-微信公众号；3-经销商APP；4-小程序；5-服务站站务系统", position = 5)
    private Integer platform;
    @ApiModelProperty(value = "排序", position = 5)
    private Integer sorts;
    @ApiModelProperty(value = "视频分类图片", position = 6)
    private String image;
    @ApiModelProperty(value = "备注", position = 7)
    private String remark;
    @ApiModelProperty(value = "是否删除 1-是  2-否", position = 8)
    private Boolean deleteFlag;
    @ApiModelProperty(value = "创建时间", position = 9)
    private Date createTime;
    @ApiModelProperty(value = "修改时间", position = 10)
    private Date updateTime;

    @ApiModelProperty(value = "二级分类", position = 11)
    private List<VideoTypeDTO> videoTypeList = new ArrayList<>();
}
