package com.yimao.cloud.pojo.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 安装工信息变化记录表
 *
 * @author Zhang Bo
 * @date 2019/7/8
 */
@Getter
@Setter
public class EngineerChangeRecordDTO {

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
