package com.yimao.cloud.base.auth;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhang Bo
 * @date 2018/7/31.
 */
@Data
public class JWTInfo implements Serializable {

    private static final long serialVersionUID = -5277712364271453933L;
    // private String subject;
    private Integer id;
    private String realName;
    private Integer type;
    private Integer stationCompanyId;
    
    public JWTInfo() {
    }

}
