package com.yimao.cloud.framework.aop.po;

import com.yimao.cloud.framework.aop.dto.OperationLogDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2019/4/11
 */
@Table(name = "sys_operation_log")
@Getter
@Setter
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //操作页面
    private String operationPage;
    //操作类型
    private String operationType;
    //操作对象
    private String operationObject;
    //描述
    private String description;
    //操作人
    private String operator;
    //操作日期
    private Date operationDate;
    //操作IP
    private String operationIp;

    public OperationLog() {
    }

    /**
     * 用业务对象OperationLogDTO初始化数据库对象OperationLog。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public OperationLog(OperationLogDTO dto) {
        this.id = dto.getId();
        this.operationPage = dto.getOperationPage();
        this.operationType = dto.getOperationType();
        this.operationObject = dto.getOperationObject();
        this.description = dto.getDescription();
        this.operator = dto.getOperator();
        this.operationDate = dto.getOperationDate();
        this.operationIp = dto.getOperationIp();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象OperationLogDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(OperationLogDTO dto) {
        dto.setId(this.id);
        dto.setOperationPage(this.operationPage);
        dto.setOperationType(this.operationType);
        dto.setOperationObject(this.operationObject);
        dto.setDescription(this.description);
        dto.setOperator(this.operator);
        dto.setOperationDate(this.operationDate);
        dto.setOperationIp(this.operationIp);
    }
}
