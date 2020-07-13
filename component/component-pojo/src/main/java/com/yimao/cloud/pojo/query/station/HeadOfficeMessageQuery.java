package com.yimao.cloud.pojo.query.station;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "服务站总部消息查询类")
@Data
public class HeadOfficeMessageQuery {
	 @ApiModelProperty(value = "消息分类id")
	 private Integer categoryId;
	 @ApiModelProperty(value = "标题")
	 private String title;
	 @ApiModelProperty(value = "开始时间")
	 private Date startTime;
	 @ApiModelProperty(value = "结束时间")
	 private Date endTime;
	 @ApiModelProperty(value = "排序 0-时间倒序 1-时间正序")
	 private Integer sortType;
	 @ApiModelProperty(value = "推送角色")
	 private String tag;
	 
	 private String sort;
}
