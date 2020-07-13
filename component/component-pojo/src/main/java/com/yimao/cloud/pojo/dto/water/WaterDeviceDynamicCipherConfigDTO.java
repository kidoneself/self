package com.yimao.cloud.pojo.dto.water;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：水机设备动态密码配置
 *
 * @Author Zhang Bo
 * @Date 2019/3/12
 */
@Getter
@Setter
public class WaterDeviceDynamicCipherConfigDTO {

    private Integer id;
    private Integer typeCode;     //类型code
    private String typeValue;     //类型值
    private Integer validMinute;  //有效期长度

    private Boolean deleted;      //删除状态：0-未删除；1-删除
    private String creator;       //创建人
    private Date createTime;      //创建时间
    private String updater;       //更新人
    private Date updateTime;      //更新时间


}
