package com.yimao.cloud.pojo.dto.station;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 站务系统-流水统计返回类
 * @author yaoweijun
 *
 */
@Data
@ApiModel(description = "流水统计返回类")
public class FlowStatisticsDTO {
	@ApiModelProperty(position = 1, required = true, value = "区域id")
	private Integer areaId;
	@ApiModelProperty(position = 2, required = true, value = "流水统计表格日期")
	private String date;
	@ApiModelProperty(position = 3, required = true, value = "服务站名称")
	private String stationName;
	@ApiModelProperty(position = 4, required = true, value = "流水统计图日期")
	private String completeTime;
	//分类名称
	private String categoryName;
		
	//---- 产品流水 -----
	@ApiModelProperty(position = 5, required = true, value = "净水服务销量")
	private Integer jsfwSaleNum;
	@ApiModelProperty(position = 6, required = true, value = "养未来销量")
	private Integer ywlSaleNum;
	@ApiModelProperty(position = 7, required = true, value = "生物科技销量")
	private Integer swkjSaleNum;
	@ApiModelProperty(position = 8, required = true, value = "净水服务销售额")
	private BigDecimal jsfwSaleFee;
	@ApiModelProperty(position = 9, required = true, value = "养未来销售额")
	private BigDecimal ywlSaleFee;
	@ApiModelProperty(position = 10, required = true, value = "生物科技销售额")
	private BigDecimal swkjSaleFee;
	@ApiModelProperty(position = 11, required = true, value = "产品总销量")
	private Integer productTotalSaleNum;
	@ApiModelProperty(position = 12, required = true, value = "产品总销售额")
	private BigDecimal productTotalSaleFee;
	
	//---- hra流水 ---------
	@ApiModelProperty(position = 13, required = true, value = "hra体检卡销量")
	private Integer pgSaleNum;
	@ApiModelProperty(position = 14, required = true, value = "hra优惠卡销量")
	private Integer yhSaleNum;
	@ApiModelProperty(position = 15, required = true, value = "hra体检卡销售额")
	private BigDecimal pgSaleFee;
	@ApiModelProperty(position = 16, required = true, value = "hra优惠卡销售额")
	private BigDecimal yhSaleFee;
	@ApiModelProperty(position = 17, required = true, value = "hra总销量")
	private Integer hraTotalSaleNum;
	@ApiModelProperty(position = 18, required = true, value = "hra总销售额")
	private BigDecimal hraTotalSaleFee;
	
	//---- 招商-----
	@ApiModelProperty(position = 19, required = true, value = "经销商注册数")
	private Integer registNum;
	@ApiModelProperty(position = 20, required = true, value = "经销商升级数")
	private Integer upgradeNum;
	@ApiModelProperty(position = 21, required = true, value = "经销商注册总额")
	private BigDecimal registFee;
	@ApiModelProperty(position = 22, required = true, value = "经销商升级总额")
	private BigDecimal upgradeFee;
	@ApiModelProperty(position = 23, required = true, value = "经销商订单总量（升级+注册）")
	private Integer distributorTotalSaleNum;
	@ApiModelProperty(position = 24, required = true, value = "经销商订单总额（升级+注册）")
	private BigDecimal distributorTotalSaleFee;
	
	//---- 全部 ----
	@ApiModelProperty(position = 25, required = true, value = "总数")
	private Integer totalNum;
	@ApiModelProperty(position = 26, required = true, value = "总营业额")
	private BigDecimal totalFee;
	
	
	//封装返回order服务统计返回结果
	List<FlowStatisticsDTO> productRes;		
	List<FlowStatisticsDTO> hraRes;
	List<FlowStatisticsDTO> distributorOrderRes;
	List<FlowStatisticsDTO> productPicRes;		
	List<FlowStatisticsDTO> hraPicRes;
	List<FlowStatisticsDTO> distributorOrderPicRes;
	List<FlowStatisticsDTO> totalDistributorOrderPicRes;
	List<FlowStatisticsDTO> totalProductAndHraPicRes;

	public Integer getJsfwSaleNum() {
		if(Objects.isNull(jsfwSaleNum)) {
			return 0;
		}
		return jsfwSaleNum;
	}

	public Integer getYwlSaleNum() {
		if(Objects.isNull(ywlSaleNum)) {
			return 0;
		}
		return ywlSaleNum;
	}

	public Integer getSwkjSaleNum() {
		if(Objects.isNull(swkjSaleNum)) {
			return 0;
		}
		return swkjSaleNum;
	}

	public BigDecimal getJsfwSaleFee() {
		if(Objects.isNull(jsfwSaleFee)) {
			return new BigDecimal(0);
		}
		return jsfwSaleFee;
	}

	public BigDecimal getYwlSaleFee() {
		if(Objects.isNull(ywlSaleFee)) {
			return new BigDecimal(0);
		}
		return ywlSaleFee;
	}

	public BigDecimal getSwkjSaleFee() {
		if(Objects.isNull(swkjSaleFee)) {
			return new BigDecimal(0);
		}
		return swkjSaleFee;
	}
	
	public Integer getProductTotalSaleNum() {
		if(Objects.isNull(productTotalSaleNum)) {
			return 0;
		}
		return productTotalSaleNum;
	}

	public BigDecimal getProductTotalSaleFee() {
		if(Objects.isNull(productTotalSaleFee)) {
			return new BigDecimal(0);
		}
		return productTotalSaleFee;
	}
	

	public Integer getPgSaleNum() {
		if(Objects.isNull(pgSaleNum)) {
			return 0;
		}
		return pgSaleNum;
	}

	public Integer getYhSaleNum() {
		if(Objects.isNull(yhSaleNum)) {
			return 0;
		}
		return yhSaleNum;
	}

	public BigDecimal getPgSaleFee() {
		if(Objects.isNull(pgSaleFee)) {
			return new BigDecimal(0);
		}
		return pgSaleFee;
	}

	public BigDecimal getYhSaleFee() {
		if(Objects.isNull(yhSaleFee)) {
			return new BigDecimal(0);
		}
		return yhSaleFee;
	}

	public Integer getHraTotalSaleNum() {
		if(Objects.isNull(hraTotalSaleNum)) {
			return 0;
		}
		return hraTotalSaleNum;
	}

	public BigDecimal getHraTotalSaleFee() {
		if(Objects.isNull(hraTotalSaleFee)) {
			return new BigDecimal(0);
		}
		return hraTotalSaleFee;
	}
	
	
	
	public Integer getRegistNum() {
		if(Objects.isNull(registNum)) {
			return 0;
		}
		return registNum;
	}

	public Integer getUpgradeNum() {
		if(Objects.isNull(upgradeNum)) {
			return 0;
		}
		return upgradeNum;
	}

	public BigDecimal getRegistFee() {
		if(Objects.isNull(registFee)) {
			return new BigDecimal(0);
		}
		return registFee;
	}
	
	public BigDecimal getUpgradeFee() {
		if(Objects.isNull(upgradeFee)) {
			return new BigDecimal(0);
		}
		return upgradeFee;
	}

	public Integer getDistributorTotalSaleNum() {
		if(Objects.isNull(distributorTotalSaleNum)) {
			return 0;
		}
		return distributorTotalSaleNum;
	}

	public BigDecimal getDistributorTotalSaleFee() {
		if(Objects.isNull(distributorTotalSaleFee)) {
			return new BigDecimal(0);
		}
		return distributorTotalSaleFee;
	}
	
	public Integer getTotalNum() {
		if(Objects.isNull(productTotalSaleNum)) {
			productTotalSaleNum=0;
		}
		
		if(Objects.isNull(hraTotalSaleNum)) {
			hraTotalSaleNum=0;
		}
		
		if(Objects.isNull(distributorTotalSaleNum)) {
			distributorTotalSaleNum=0;
		}
		return productTotalSaleNum+hraTotalSaleNum+distributorTotalSaleNum;
	}
	
	public BigDecimal getTotalFee() {
		BigDecimal totalFee=new BigDecimal(0);
		
		if(Objects.isNull(productTotalSaleFee)) {
			productTotalSaleFee=new BigDecimal(0);
		}
		
		if(Objects.isNull(hraTotalSaleFee)) {
			hraTotalSaleFee=new BigDecimal(0);
		}
		
		if(Objects.isNull(distributorTotalSaleFee)) {
			distributorTotalSaleFee=new BigDecimal(0);
		}
		
		totalFee=productTotalSaleFee.add(hraTotalSaleFee).add(distributorTotalSaleFee);
		
		return totalFee;
	}
}
