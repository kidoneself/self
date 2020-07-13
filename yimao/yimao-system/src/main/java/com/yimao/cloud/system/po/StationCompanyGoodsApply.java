package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.StationCompanyGoodsApplyDTO;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 服务站公司物资申请表
 *
 * @author Liu Long Jie
 * @Date 2020-6-18
 */
@Data
@Table(name = "station_company_goods_apply")
public class StationCompanyGoodsApply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "`count`")
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


    public StationCompanyGoodsApply() {
    }

    /**
     * 用业务对象StationCompanyGoodsApplyDTO初始化数据库对象StationCompanyGoodsApply。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StationCompanyGoodsApply(StationCompanyGoodsApplyDTO dto) {
        this.id = dto.getId();
        this.stationCompanyId = dto.getStationCompanyId();
        this.stationCompanyName = dto.getStationCompanyName();
        this.applicantAccount = dto.getApplicantAccount();
        this.applicantName = dto.getApplicantName();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.address = dto.getAddress();
        this.firstCategoryId = dto.getFirstCategoryId();
        this.firstCategoryName = dto.getFirstCategoryName();
        this.twoCategoryId = dto.getTwoCategoryId();
        this.twoCategoryName = dto.getTwoCategoryName();
        this.goodsId = dto.getGoodsId();
        this.goodsName = dto.getGoodsName();
        this.isAfterAudit = dto.getIsAfterAudit();
        this.count = dto.getCount();
        this.status = dto.getStatus();
        this.cause = dto.getCause();
        this.accessory = dto.getAccessory();
        this.confirmImg = dto.getConfirmImg();
        this.firstAuditor = dto.getFirstAuditor();
        this.firstAuditTime = dto.getFirstAuditTime();
        this.twoAuditor = dto.getTwoAuditor();
        this.twoAuditTime = dto.getTwoAuditTime();
        this.twoAuditNotPassCause = dto.getTwoAuditNotPassCause();
        this.shipmentsTime = dto.getShipmentsTime();
        this.applyTime = dto.getApplyTime();
        this.completionTime = dto.getCompletionTime();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StationCompanyGoodsApplyDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StationCompanyGoodsApplyDTO dto) {
        dto.setId(this.id);
        dto.setStationCompanyId(this.stationCompanyId);
        dto.setStationCompanyName(this.stationCompanyName);
        dto.setApplicantAccount(this.applicantAccount);
        dto.setApplicantName(this.applicantName);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAddress(this.address);
        dto.setFirstCategoryId(this.firstCategoryId);
        dto.setFirstCategoryName(this.firstCategoryName);
        dto.setTwoCategoryId(this.twoCategoryId);
        dto.setTwoCategoryName(this.twoCategoryName);
        dto.setGoodsId(this.goodsId);
        dto.setGoodsName(this.goodsName);
        dto.setIsAfterAudit(this.isAfterAudit);
        dto.setCount(this.count);
        dto.setStatus(this.status);
        dto.setCause(this.cause);
        dto.setAccessory(this.accessory);
        dto.setConfirmImg(this.confirmImg);
        dto.setFirstAuditor(this.firstAuditor);
        dto.setFirstAuditTime(this.firstAuditTime);
        dto.setTwoAuditor(this.twoAuditor);
        dto.setTwoAuditTime(this.twoAuditTime);
        dto.setTwoAuditNotPassCause(this.twoAuditNotPassCause);
        dto.setApplyTime(this.applyTime);
        dto.setCompletionTime(this.completionTime);
        dto.setCreateTime(this.createTime);
        dto.setShipmentsTime(this.shipmentsTime);
    }
}
