package com.yimao.cloud.pojo.dto.system;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 描述：上线地区
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
@Getter
@Setter
public class OnlineAreaDTO implements Serializable {

    private static final long serialVersionUID = 285432848464071353L;

    private Integer id;
    private String province; //省
    private String city;     //市
    private String region;   //区
    private Integer level;   //区域等级：1-省；2-省市；3-省市区
    private String syncState;//同步状态:N Y  FAILURE
    private Integer status;//状态  0-否 1-是

}
