package com.yimao.cloud.pojo.dto.system;

import lombok.Data;

import java.util.Date;

/**
 * 服务站公司物资申请表
 *
 * @author Liu Long Jie
 * @Date 2020-6-18
 */
@Data
public class StationCompanyGoodsApplyDTO {
    private String id; //申请单号
    private Integer stationCompanyId; //服务站公司id
    private String stationCompanyName; //服务站公司名称
    private String applicantAccount; //申请人账号
    private String applicantName; //申请人姓名
    private String province; //收货地省
    private String city; //收货地市
    private String region; //收货地区
    private String address; //收货地址
    private Integer firstCategoryId; //一级分类id
    private String firstCategoryName; //一级分类名称
    private Integer twoCategoryId; //二级分类id
    private String twoCategoryName; //二级分类名称
    private Integer goodsId; //物资id
    private String goodsName; //物资名称
    private Boolean isAfterAudit; //是否售后审核 0-否 ；1-是
    private Integer count; //申请数量
    private Integer status; //申请状态 1-待初审 2-待复核 3-等待发货 4-配送中 5-已完成 6-打回 0-已取消
    private String cause; //打回原因（只有"打回"状态才有值）
    private String accessory; //附件
    private String confirmImg; //确认收货图片
    private String firstAuditor; //初审人（取最近一次）
    private Date firstAuditTime; //初审时间（取最近一次）
    private String twoAuditor; //复审人（取最近一次）
    private Date twoAuditTime; //复审时间（取最近一次）
    private String twoAuditNotPassCause; //复审不通过原因（取最近一次）
    private Date shipmentsTime; //发货时间
    private Date applyTime; //申请时间
    private Date completionTime; //完成时间
    private Date createTime; //创建时间

}
