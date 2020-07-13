package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：用户多重身份对象
 *
 * @Author Zhang Bo
 * @Date 2019/10/14
 */
@ApiModel(description = "用户多重身份对象")
@Getter
@Setter
public class UserIdentityDTO {

    private Integer id;                     //e家号
    private Integer userType;               //用户等级：0-体验版经销商；1-微创版经销商；2-个人版经销商；3-分享用户；4-普通用户；5-企业版经销商（主）；6-企业版经销商（子）；7-分销用户；
    private String userTypeName;            //用户身份：体验版经销商；微创版经销商；个人版经销商；企业版经销商；普通用户；会员用户；
    private Boolean founder;                //是否为五大创始人：0-否；1-是；
    private Boolean stationMaster;          //是否为站长：0-否；1-是；
    private Integer agentLevel;             //代理商级别：1-省代；2-市代；4-区代；3-省代+市代；5-省代+区代；6-市代+区代；7-省代+市代+区代；
    private Integer roleLevel;              //经销商角色等级：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子）
    private String province;                //所在省
    private String city;                    //所在市
    private String region;                  //所在区

}
