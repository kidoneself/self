// package com.yimao.cloud.out.entity;
//
// import lombok.Getter;
// import lombok.Setter;
// import org.springframework.data.annotation.Id;
//
// import java.io.Serializable;
// import java.util.Date;
//
// /**
//  * @author Zhang Bo
//  * @date 2017/12/15.
//  */
// @Getter
// @Setter
// public class Distributor implements Serializable {
//
//     private static final long serialVersionUID = -7880254618306333422L;
//
//     @Id
//     private String id;//
//     private String name;//登录名
//     private String realName;//真实姓名
//     private String longitude;//经纬度
//     private String latitude;//经纬度
//     private String[] distype;//
//     private Integer level;//1-省代；2-市代；3-区代；4省代+市代；5-省代+市代+区代；6-市代+区代
//     private String password;//密码
//     private String imageName;//头像
//     private String sex;//性别
//     private String phone;//手机
//     private String province;//省
//     private String city;//市
//     private String region;//区
//     private String address;//地址
//     private String company;//公司
//     private String job;//工作
//     private String workId;//身份证
//     private String mail;//邮箱
// //    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//     private Date createTime;//创建时间
// //    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//     private Date updateTime;//
//     private Integer type;//0-代理商，1-经销商，2-经销商+代理商
//     private Integer search;//
//     private Integer look;//
//     private Integer appType;//app类型
//     private Integer count;//水机剩余配额
//     private String admin;//后台管理员
//     private String referee;//推荐人
//     private Integer pnumber;//
//     private Integer cnumber;//
//     private Integer rnumber;//
//     private Integer poriginal;//
//     private Integer coriginal;//
//     private Integer roriginal;//
//     private Integer isSponsors;//发起人(1是2不是)
//     private Integer ispremium;//是否溢价股东
//     //    private Integer special;//
//    private Integer fuhuishun;//
// //    private Integer fuhuishun_level;//
//     private Integer loginCount;//登录次数
// //    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//     private Date lastLoginTime;//最后登录时间
//     private String version;//
//     private String refereeName;//推荐人姓名
//     private String refereeProvince;//推荐人省
//     private String refereeCity;//推荐人市
//     private String refereeRegion;//推荐人区
// //    @JSONField(format = "yyyy-MM-dd HH:mm:ss")//
//     private Date regionTime;//
// //    @JSONField(format = "yyyy-MM-dd HH:mm:ss")//
//     private Date disTime;//
//     private String assistant;//智慧助理编号
//     private String assistantName;//智慧助理姓名
//     private String assistantProvince;//智慧助理省
//     private String assistantCity;//智慧助理市
//     private String assistantRegion;//智慧助理区
//     private String assistantWorkId;//智慧助理身份证编号
//     private String refereeAssistant;//智慧助理推荐人编号
//     private String refereeAssistantName;//智慧助理推荐人姓名
//     private String refereeAssistantProvince;//智慧助理推荐人省
//     private String refereeAssistantCity;//智慧助理推荐人市
//     private String refereeAssistantRegion;//智慧助理推荐人区
//     private String refereeAssistantWorkId;//智慧助理推荐人身份证编号
// //    @JSONField(format = "yyyy-MM-dd HH:mm:ss")//
//     private Date synchronousTime;//同步时间
//     private Boolean synchronousState;//同步状态
//     private Boolean primaryAccount;//主账号
//     private Integer onlineType;//
//     private String refereeWorkId;//
//     private Boolean tryout;//
//     private Object[] quotaList;//配额列表
//     private Integer roleLevel;//角色级别：50体验版、350微创版、650个人版、950企业版
//     private String roleConfId;//引用经销商表"distributorroleconf")
//     private Double payMoney;//支付金额
//     private Integer accountType;
//     private String pid;
//     private int special;//是否为五大股东(1-是，2-不是)
//     private String[] costRoles;//水机计费方式权限
//     private Boolean forbidden;//是否被禁止（冻结）
//     private Boolean forbiddenOrder;//是否被禁止下单（冻结）
//
//     private Distributorroleconf roleConf;
//
//     private Distributor recommend;//推荐人详细信息
//
// }
