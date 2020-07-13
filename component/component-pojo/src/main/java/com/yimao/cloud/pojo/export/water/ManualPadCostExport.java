package com.yimao.cloud.pojo.export.water;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：手动修改水机配额导出实体类
 *
 * @Author Zhang Bo
 * @Date 2019/10/9
 */
@Getter
@Setter
public class ManualPadCostExport {

    //SN码
    private String sn;
    //余额
    private String balance;
    //已使用流量
    private String discharge;
    //是否开启：0-关闭；1-开启
    private String open;
    //同步状态：0-未同步；1-同步完成；2-同步失败；
    private String syncStatus;
    //同步失败的原因
    private String syncFailReason;
    //同步到水机pad上的时间
    private String syncTime;
    //创建时间
    private String createTime;

}