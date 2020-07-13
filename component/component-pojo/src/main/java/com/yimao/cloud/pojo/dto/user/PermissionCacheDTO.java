package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/11/2.
 */
@Data
public class PermissionCacheDTO implements Serializable {

    private static final long serialVersionUID = -6299160203474077260L;

    private String code;
    private String method;

}
