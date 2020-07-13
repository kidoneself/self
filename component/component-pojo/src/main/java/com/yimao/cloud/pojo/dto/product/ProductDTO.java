package com.yimao.cloud.pojo.dto.product;

import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 产品DTO对象
 *
 * @author Zhang Bo
 * @date 2018/11/8.
 */
@ApiModel(description = "产品DTO对象")
@Getter
@Setter
public class ProductDTO {

    @ApiModelProperty(value = "产品id")
    private Integer id;

    @ApiModelProperty(position = 1, value = "产品名称")
    private String name;

    @ApiModelProperty(position = 2, value = "产品CODE值")
    private String code;

    @ApiModelProperty(position = 3, value = "产品模式：1-实物；2-虚拟；3-租赁；")
    private Integer mode;

    @ApiModelProperty(position = 4, value = "产品后台三级类目ID")
    private Integer categoryId;

    @ApiModelProperty(position = 4, value = "产品后台三级类目名称")
    private String categoryName;

    @ApiModelProperty(position = 5, value = "产品公司ID")
    private Integer companyId;

    @ApiModelProperty(position = 6, value = "产品公司名称")
    private String companyName;

    @ApiModelProperty(position = 7, value = "产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；")
    private String supplyCode;

    @ApiModelProperty(position = 8, value = "是否是新产品：1-是；0-否；")
    private Boolean newProduct;

    @ApiModelProperty(position = 9, value = "产品轮播图")
    private String img;

    @ApiModelProperty(position = 10, value = "产品封面图")
    private String coverImg;

    @ApiModelProperty(position = 11, value = "产品封面视频")
    private String coverVideo;

    @ApiModelProperty(position = 12, value = "产品详情图")
    private String detailImg;

    @ApiModelProperty(position = 13, value = "产品价格")
    private BigDecimal price;

    @ApiModelProperty(position = 14, value = "市场价格")
    private BigDecimal marketPrice;

    @ApiModelProperty(position = 15, value = "库存")
    private Integer stock;

    @ApiModelProperty(position = 16, value = "产品状态：1-未上架；2-已上架；3-已下架；4-已删除；")
    private Integer status;

    @ApiModelProperty(position = 17, value = "上架类型：1-立即上架；2-自定义上架时间；3-暂不上架；")
    private Integer onShelfType;

    @ApiModelProperty(position = 17, value = "自定义上架时间")
    private Date willOnShelfTime;

    @ApiModelProperty(position = 18, value = "上架时间")
    private Date onShelfTime;

    @ApiModelProperty(position = 19, value = "下架时间")
    private Date offShelfTime;

    @ApiModelProperty(position = 20, value = "运费")
    private BigDecimal logisticsFee;

    @ApiModelProperty(position = 21, value = "产品描述")
    private String description;

    @ApiModelProperty(position = 22, value = "排序字段")
    private Integer sorts;

    @ApiModelProperty(position = 23, value = "最小起订量")
    private Integer minMoq;

    @ApiModelProperty(position = 24, value = "最大限购数")
    private Integer maxMoq;

    @ApiModelProperty(position = 25, value = "运输方式（运费模板）：1-包邮；2-货到付款；")
    private Integer transportType;

    @ApiModelProperty(position = 26, value = "提货方式：1-送货上门；2-到店自提；")
    private Integer takeType;

    @ApiModelProperty(position = 27, value = "活动类型：1-普通产品；2-折机产品；3-180产品；5-限时抢购")
    private Integer activityType;

    @ApiModelProperty(position = 28, value = "售出量")
    private Integer saleCount;

    @ApiModelProperty(position = 29, value = "产品的富文本详情内容")
    private String textDetail;

    @ApiModelProperty(position = 30, value = "前端分类ID")
    private Set<Integer> frontCategoryIds;

    @ApiModelProperty(position = 31, value = "收益分配模板ID")
    private Set<Integer> incomeRuleIds;

    @ApiModelProperty(position = 32, value = "可购买人群：购买权限接口返回数据中的CODE值，以逗号分隔")
    private String buyPermission;

    @ApiModelProperty(position = 33, value = "水机计费方式ID")
    private Set<Integer> costIds;

    @ApiModelProperty(position = 50, value = "产品后台类目")
    private ProductCategoryDTO backstageCategory;

    @ApiModelProperty(position = 51, value = "产品前台类目")
    private List<ProductCategoryDTO> frontCategoryList;

    @ApiModelProperty(position = 52, value = "水机产品计费方式")
    private List<ProductCostDTO> productCostList;

    @ApiModelProperty(position = 53, value = "虚拟产品配置")
    private VirtualProductConfigDTO virtualProductConfig;

    @ApiModelProperty(position = 54, value = "收益分配")
    private List<IncomeRuleDTO> incomeRuleList;

    @ApiModelProperty(position = 61, value = "后台产品类目名称级联显示")
    private String cascadeBackstageCategoryName;

    @ApiModelProperty(position = 62, value = "前台产品类目名称级联显示")
    private List<String> cascadeFrontCategoryNameList;

    @ApiModelProperty(position = 100, value = "创建人")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新人")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(position = 104, value = "是否为热销：")
    private Integer hot;


    @ApiModelProperty(position = 200, value = "前台一级类目名称")
    private String frontFirstCategoryName;

    @ApiModelProperty(position = 201, value = "老的产品id")
    private String oldId;

    @ApiModelProperty(position = 202, value = "老的产品类型id")
    private String oldCategoryId;

    @ApiModelProperty(position = 203, value = "折机经销商id")
    private Set<Integer> distributorIds;

    @ApiModelProperty(position = 204, value = "折机经销商")
    private List<DistributorDTO> distributorList;

}
