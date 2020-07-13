package com.yimao.cloud.user.po;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@Table(name = "system_role__permission")
@Data
public class RolePermission {

    @Id
    private Integer roleId;
    @Id
    private Integer permissionId;

}
