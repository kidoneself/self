package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.PrecisionAdvertisingDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 描述：广告精准投放。
 *
 * @Author Zhang Bo
 * @Date 2019/1/29 17:41
 * @Version 1.0
 */
@Table(name = "precision_advertising")
@Data
public class PrecisionAdvertising {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String snCode;//水机SN码
    private Integer advertisingId;//广告配置ID

}
