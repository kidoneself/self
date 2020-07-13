package com.yimao.cloud.product.po;

import com.yimao.cloud.pojo.dto.product.ProductActivityDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/***
 * 产品活动
 * 
 * @author zhangbaobao
 * @date 2020/3/22
 */
@Data
public class ProductActivity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;// '字典值：1.抢购活动，2其他',
	private String name;// '活动名称',
	private Integer status;// '1.未开始,2.进行中,3.已结束,4.已终止',
	private Boolean opening;// '0.未激活,1.已激活',
	private Integer productId;// '产品id',
	private String label;// '活动标签',
	private Integer activityType;// '活动类型：5-限时抢购',
	private Integer activityStock;// '如果活动设置了重复时间，那么活动库存指的是单次活动的库存',
	private Integer remainingStock;// '如果活动设置了重复时间，那么活动库存指的是本次活动的剩余库存',
	private Integer soldNum;// '是指该活动从活动开始的累计售卖总数量，并非周期性活动的单次售卖数量',
	private Date startTime;// '时间格式：YYYY-MM-DD hh:mm:ss',
	private Date endTime;// '时间格式：YYYY-MM-DD hh:mm:ss',
	private Boolean cycle;// '是否周期重复:0.否，1.是',
	private Integer cycleType;// '1.每天，2.每周，3.每月几号，如果选择了是周期重复，该字段非空',
	private String cycleTime;//重复周期的日期格式{"startTime": "hh:mm:ss","endTime": "hh:mm:ss","weekDay": "1","day": 1}
	private Boolean limitStatus;// '0.不限制，1，限制',
	private Integer limitNum;// '只有选择了限制的时候，才会有值',
	private BigDecimal activityPrice;// '产品活动价格',
	private Date createTime;// 创建时间
	private String creator;// 创建者
	private Date updateTime;// 更新时间
	private String updater;// 更新者
	private Integer version;// '用于乐观锁，更新已售数量时用到',
	private Integer terminal;//来源：1-终端app；2-微信公众号；3-翼猫APP；4-小程序；
	
	public void convert(ProductActivityDTO dto){
		dto.setId(id);
		dto.setName(name);
		dto.setActivityPrice(activityPrice);
		dto.setActivityStock(activityStock);
		dto.setRemainingStock(remainingStock);
		dto.setActivityType(activityType);
		dto.setCycle(cycle);
		dto.setCycleTime(cycleTime);
		dto.setStartTime(startTime);
		dto.setEndTime(endTime);
		dto.setLabel(label);
		dto.setLimitNum(limitNum);
		dto.setLimitStatus(limitStatus);
		dto.setOpening(opening);
		dto.setProductId(productId);
		dto.setSoldNum(soldNum);
		dto.setStatus(status);
		dto.setCreateTime(createTime);
		dto.setCreator(creator);
		dto.setUpdater(updater);
		dto.setUpdateTime(updateTime);
		dto.setVersion(version);
	}
}
