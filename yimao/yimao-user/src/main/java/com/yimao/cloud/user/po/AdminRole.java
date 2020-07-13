package com.yimao.cloud.user.po;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Zhang Bo
 * @date 2018/11/2.
 */
@Table(name = "system_admin__role")
@Data
public class AdminRole {

    @Id
    private Integer adminId;
    @Id
    private Integer roleId;

}
