package com.yimao.cloud.pojo.dto.station;

import java.util.Map;

import com.yimao.cloud.pojo.dto.system.MessagePushDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "站务系统消息")
public class StationMessageDTO extends MessagePushDTO {
	
    @ApiModelProperty("消息类型 0- 消息插入1-发送短信")
    private Integer messageType;
    @ApiModelProperty("消息推送id")
    private Integer messagePushId;
    @ApiModelProperty(value = "机制：1-到达续费阀值,2-续费成功, 3-超过期限未续费, 4-余额为0, 5-滤芯更换成功(滤芯更换后), 6-滤芯更换前（PP 、 UDF 、 CTO 、 T33 ）,7-设备故障（即制水故障),8-TDS异常,9-用户下单,10-账号创建成功,11-快速上线财务审核(通过), 12-快速上线企业资质审核(通过),13-快速上线财务审核(不通过),14-快速上线企业资质审核(不通过),15-选择线下支付(商城购物时审核通过),16-选择线下支付(商城购物时审核不通过),17-经销商续配额通知,18-新工单分配,19-工单完成", required = true)
    private Integer mechanism;
    @ApiModelProperty(value = "推送对象:1-用户,2-经销商,3-安装工程师,4-,下单人,5-水机用户,6-后台展示(系统保存),7-账号创建成功发送给代理商/经销商/创始人", required = true)
    private Integer pushObject;
    /*@ApiModelProperty(value = "推送方式：2-翼猫APP,3-翼猫服务APP", hidden = true)
    private Integer pushMode;*/
    @ApiModelProperty(value = "消息内容", required = true)
    private  Map<String, String> contentMap;

}
