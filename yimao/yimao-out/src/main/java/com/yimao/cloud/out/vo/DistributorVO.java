package com.yimao.cloud.out.vo;

import lombok.Data;

/**
 * @author Liu Yi
 * @version 1.0
 * @className DistributorVO$
 * @description
 * @date 2019/10/14$ 13:54$
 **/
@Data
public class DistributorVO {
    private Integer distributorId;
    private String oldDistributorId;
    private String distributorName;
    private String distributorPhone;
    private String distributorIdCard;
    private String distributorAccount;
    private Integer distributorRoleId;
    private String distributorRoleName;

    private Integer  childDistributorId;
    private String  oldChildDistributorId;
    private String childDistributorName;
    private String childDistributorPhone;
    private String childDistributorAccount;
    private Integer childDistributorRoleId;
    private String childDistributorRoleName;
    private String childDistributorIdCard;
}
