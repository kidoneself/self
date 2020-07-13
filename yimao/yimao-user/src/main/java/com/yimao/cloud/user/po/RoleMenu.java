package com.yimao.cloud.user.po;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Zhang Bo
 * @date 2018/11/2.
 */
@Table(name = "system_role__menu")
@Data
public class RoleMenu {

    @Id
    private Integer roleId;
    @Id
    private Integer menuId;

}
