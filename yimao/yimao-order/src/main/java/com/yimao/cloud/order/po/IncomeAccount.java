//package com.yimao.cloud.order.po;
//
//import com.yimao.cloud.pojo.dto.order.IncomeAccountDTO;
//import lombok.Getter;
//import lombok.Setter;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import java.io.Serializable;
//import java.util.Date;
//
///**
// * @description: 普通用户下单, 配置所属经销商
// * @author: yu chunlei
// * @create: 2019-04-09 13:58:20
// **/
//@Table(name = "income_account")
//@Getter
//@Setter
//public class IncomeAccount implements Serializable {
//
//    private static final long serialVersionUID = 2414670523855534805L;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//    private Integer userId;
//    private Integer distribitorId;
//    private Boolean onLine;//是否在线
//    private Date createTime;
//    private String creator;
//    private Date updateTime;
//    private String updater;
//
//
//    public IncomeAccount() {
//    }
//
//    /**
//     * 用业务对象IncomeAccountDTO初始化数据库对象IncomeAccount。
//     * plugin author ylfjm.
//     *
//     * @param dto 业务对象
//     */
//    public IncomeAccount(IncomeAccountDTO dto) {
//        this.id = dto.getId();
//        this.userId = dto.getUserId();
//        this.distribitorId = dto.getDistribitorId();
//        this.onLine = dto.getOnLine();
//        this.createTime = dto.getCreateTime();
//        this.creator = dto.getCreator();
//        this.updateTime = dto.getUpdateTime();
//        this.updater = dto.getUpdater();
//    }
//
//    /**
//     * 将数据库实体对象信息拷贝到业务对象IncomeAccountDTO上。
//     * plugin author ylfjm.
//     *
//     * @param dto 业务对象
//     */
//    public void convert(IncomeAccountDTO dto) {
//        dto.setId(this.id);
//        dto.setUserId(this.userId);
//        dto.setDistribitorId(this.distribitorId);
//        dto.setOnLine(this.onLine);
//        dto.setCreateTime(this.createTime);
//        dto.setCreator(this.creator);
//        dto.setUpdateTime(this.updateTime);
//        dto.setUpdater(this.updater);
//    }
//}
