package com.yimao.cloud.framework.aop.dto;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志实体类
 */
@ApiModel("操作日志DTO")
@Getter
@Setter
@ToString
public class OperationLogDTO implements Serializable {

    private static final long serialVersionUID = -1532039321656806457L;
    private Integer id;
    private String operationPage;//操作页面
    private String operationType;//操作类型
    private String operationObject;//操作对象
    private String description;//描述
    private String operator;//操作人
    private Date operationDate;//操作日期
    private String operationIp;//操作IP

}
