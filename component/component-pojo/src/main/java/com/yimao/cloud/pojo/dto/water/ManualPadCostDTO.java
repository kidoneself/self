package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：手动修改水机配额
 *
 * @Author Zhang Bo
 * @Date 2019/5/7
 */
@ApiModel(description = "手动修改水机配额DTO")
@Getter
@Setter
public class ManualPadCostDTO {

    private Integer id;
    //SN码
    private String sn;
    //余额
    private BigDecimal balance;
    //已使用流量
    private Integer discharge;
    //是否开启：0-关闭；1-开启
    private Boolean open;
    // private Boolean check;
    //同步状态：0-未同步；1-同步完成；2-同步失败；
    private Integer syncStatus;
    //同步失败的原因
    private String syncFailReason;
    //同步到水机pad上的时间
    private Date syncTime;
    //创建时间
    private Date createTime;

}