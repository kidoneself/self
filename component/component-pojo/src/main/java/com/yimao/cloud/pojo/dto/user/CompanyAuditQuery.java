package com.yimao.cloud.pojo.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompanyAuditQuery implements Serializable {

    private static final long serialVersionUID = 6647650171902312065L;

    private Long orderId;
    private Integer orderType;
    private String companyName;
    private String account;
    private Integer roleLevel;

    @Override
    public String toString() {
        return "CompanyAuditQuery{" +
                "orderId=" + orderId +
                ", orderType=" + orderType +
                ", companyName='" + companyName + '\'' +
                ", account='" + account + '\'' +
                ", roleLevel=" + roleLevel +
                '}';
    }
}
