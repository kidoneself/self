package com.yimao.cloud.user.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 安装工信息变化记录表
 *
 * @author Zhang Bo
 * @date 2019/7/8
 */
@Table(name = "user_engineer_change_record")
@Getter
@Setter
public class EngineerChangeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 原用户e家号
     */
    private Integer engineerId;
    /**
     * 操作类型
     */
    private String operationType;
    /**
     * 操作对象
     */
    private String operationObject;
    /**
     * 描述
     */
    private String description;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 操作日期
     */
    private Date operationDate;
    /**
     * 操作IP
     */
    private String operationIp;

}
