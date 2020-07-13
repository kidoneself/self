package com.yimao.cloud.pojo.query.order;

import com.yimao.cloud.pojo.query.station.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @description 退机工单查询条件
 * @author Liu Yi
 * @date 2020/6/22 15:18
 */
@Getter
@Setter
@ApiModel(description = "退机工单条件查询")
public class WorkOrderBackQueryDTO extends BaseQuery implements Serializable {
	private static final long serialVersionUID = -6927458680529455450L;
	@ApiModelProperty(value = "退机工单号")
	private String id;                 //退机id
	@ApiModelProperty(value = "退机工单号")
	private String code;                 //工单号
	@ApiModelProperty(position = 100, value = "工单号")
	private String workOrderId;
	@ApiModelProperty(value = "用户姓名")
	private String userName;           //用户姓名
	@ApiModelProperty(value = "用户手机号")
	private String userPhone;          //用户手机号
	@ApiModelProperty(value = "经销商姓名")
	private String distributorName;     //经销商姓名
	/*@ApiModelProperty(value = "经销商姓名")
	private String distributorName;    //经销商姓名
	@ApiModelProperty(value = "经销商手机号")
	private String distributorPhone;   //经销商手机号*/
	@ApiModelProperty(value = "省")
	private String province;           //省
	@ApiModelProperty(value = "市")
	private String city;               //市
	@ApiModelProperty(value = "区县")
	private String region;             //区县
	@ApiModelProperty(value = "产品型号")
	private Integer productCategoryName;         //产品型号
	@ApiModelProperty(value = "安装工ID")
	private Integer engineerId;             //安装工ID
	/*@ApiModelProperty(value = "安装工手机号")
	private Integer engineerPhone; */          //安装工手机号
	@ApiModelProperty(value = "工单状态")
	private Integer status;                 //工单状态
	@ApiModelProperty(value = "搜索字段")
	private String search;                  //搜索字段
	@ApiModelProperty(value = "SN码")
	private String sn;
	@ApiModelProperty(value = "开始时间", example = "2018-12-28 11:00:00")
	private Date startTime;                //开始时间
	@ApiModelProperty(value = "结束时间", example = "2018-12-28 11:00:00")
	private Date endTime;                  //结束时间
	@ApiModelProperty(value = "安装工id集合")
	private List<Integer> engineerIds;
	@ApiModelProperty(value = "纬度")
	private String latitude;//纬度
	@ApiModelProperty(value = "经度")
	private String longitude;//经度
	@ApiModelProperty(value = "排序类型:1-升序 2-降序")
	private Integer sortType;
	@ApiModelProperty(value = "排序规则：1-创建时间 2-距离 3-完成时间 ")
	private Integer sortBy;
}
