package com.yimao.cloud.pojo.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/11/1.
 */
@Data
public class WaterUserDTO implements Serializable {

    private static final long serialVersionUID = 1387553178144189306L;
    private String id;//
    private String name;//姓名
    private String sex;//性别
    private String phone;//电话
    private String province;//省
    private String city;//市
    private String region;//区
    private String address;//地址
    private String job;//部门职位
    private Integer age;//年龄
    private String degree;//文化程度
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private Integer count;//家庭人口
    private Integer order;//
    private DistributorDTO distributorDTO;//经销商
    private String buildingTag;//建筑标签
    private String company;//公司
    private String hobby;//爱好
    private Integer childAge;//子女年龄
    private Boolean haveChild;//是否有子女
    private Boolean haveOld;//是否有老人
    private String childSex;//子女性别
    private Boolean marry;//子女是否结婚
    private Boolean studyAbroad;//是否考虑留学
    private String image;//头像
    private String industry;//公司行业
    private String legal;//公司法人
    private String dimensions;//
    private Integer isUserSelfHelpAdd;//是否是用户自主下单:1-否，2-是
    private String childDistributorId;//企业版子账户ID

    private String distributorId;//经销商Id

}
