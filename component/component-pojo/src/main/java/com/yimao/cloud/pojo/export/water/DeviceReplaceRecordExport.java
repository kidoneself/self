package com.yimao.cloud.pojo.export.water;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：设备更换记录导出实体类
 *
 * @Author Zhang Bo
 * @Date 2019/11/5
 */
@Getter
@Setter
public class DeviceReplaceRecordExport {

    private String newSn;
    private String newIccid;
    private String newBatchCode;
    private String oldSn;
    private String oldIccid;
    private String oldBatchCode;
    private String creator;
    private String createTime;

}
