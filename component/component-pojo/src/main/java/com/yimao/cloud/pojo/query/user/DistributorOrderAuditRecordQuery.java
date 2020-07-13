package com.yimao.cloud.pojo.query.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @description   经销商订单审核记录
 * @author Liu Yi
 * @date 2019/11/26 16:31
 */
@Getter
@Setter
public class DistributorOrderAuditRecordQuery  implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long distributorOrderId;
    private Integer orderType;
    private String distributorAccount;
    private Integer roleId;
    private Integer auditType;
    private Integer status;

    @Override
    public String toString() {
        return "DistributorOrderAuditRecordQuery{" +
                "distributorOrderId=" + distributorOrderId +
                ", orderType=" + orderType +
                ", distributorAccount='" + distributorAccount + '\'' +
                ", roleId=" + roleId +
                ", auditType=" + auditType +
                ", status=" + status +
                '}';
    }
}
