package com.yimao.cloud.water.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：水机滤芯流量数据
 *
 * @Author Zhang Bo
 * @Date 2019/5/8
 */
@Table(name = "filter_data")
@Getter
@Setter
public class FilterData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //SN码
    private String sn;
    private Integer deviceId;
    private Integer ppFlow;
    private Integer udfFlow;
    private Integer ctoFlow;
    private Integer threeFlow;
    private Integer roFlow;
    private Date createTime;

}
