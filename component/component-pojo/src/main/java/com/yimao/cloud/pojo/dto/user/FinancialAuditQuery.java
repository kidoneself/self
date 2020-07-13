package com.yimao.cloud.pojo.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class FinancialAuditQuery implements Serializable {
    private static final long serialVersionUID = 6647650171902452060L;

    private Long distributorOrderId;
    private Integer orderType;
    private String name;
    private String distributorAccount;
    private Integer roleId;
    private Integer destRoleId;
    private Integer payType;
    private String payStartTime;
    private String payEndTime;

    @Override
    public String toString() {
        return "FinancialAuditQuery{" +
                "distributorOrderId=" + distributorOrderId +
                ", orderType=" + orderType +
                ", name='" + name + '\'' +
                ", distributorAccount='" + distributorAccount + '\'' +
                ", roleId=" + roleId +
                ", destRoleId=" + destRoleId +
                ", payType=" + payType +
                ", payStartTime='" + payStartTime + '\'' +
                ", payEndTime='" + payEndTime + '\'' +
                '}';
    }
}
