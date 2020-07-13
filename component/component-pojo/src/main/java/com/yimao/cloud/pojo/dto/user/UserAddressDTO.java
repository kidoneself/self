package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @author liuhao@yimaokeji.com
 * 2017/11/23
 */
@Data
public class UserAddressDTO implements Serializable {
    private static final long serialVersionUID = 5900720949141049541L;

    private Integer id;
    @ApiModelProperty(position = 1, value = "用户e家号")
    private Integer userId;
    @ApiModelProperty(position = 2, value = "联系人")
    private String contact;
    @ApiModelProperty(position = 3, value = "手机号")
    private String mobile;
    @ApiModelProperty(position = 4, value = "性别    1男 2 女")
    private Integer sex;
    @ApiModelProperty(position = 5, value = "省")
    private String province;
    @ApiModelProperty(position = 6, value = "市")
    private String city;
    @ApiModelProperty(position = 7, value = "区")
    private String region;
    @ApiModelProperty(position = 8, value = "街道")
    private String street;
    @ApiModelProperty(position = 9, value = "默认地址")
    private Boolean defaultAddress;
    @ApiModelProperty(position = 10, value = "是否删除 1:是 2:否")
    private Boolean deleted;
    @ApiModelProperty(position = 11, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 12, value = "修改时间")
    private Date updateTime;
}
