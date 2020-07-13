package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@ApiModel(description = "消息模版")
@Setter
@Getter
public class MessageTemplateDTO implements Serializable {
    private static final long serialVersionUID = 7968692876208263005L;
    @ApiModelProperty(value = "主键")
    private Integer id;
    @ApiModelProperty(value = "类型：1-阈值提醒,2-续费推送,3-报警推送,4-创建账号,5-新工单分配,6-工单完成,7-审核通过,8-审核不通过,9-经销商续配额", required = true)
    private Integer type;
    @ApiModelProperty(value = "机制：1-到达续费阀值,2-续费成功, 3-超过期限未续费, 4-余额为0, 5-滤芯更换成功(滤芯更换后), 6-滤芯更换前（PP 、 UDF 、 CTO 、 T33 ）,7-设备故障（即制水故障),8-TDS异常,9-用户下单,10-账号创建成功,11-快速上线财务审核(通过), 12-快速上线企业资质审核(通过),13-快速上线财务审核(不通过),14-快速上线企业资质审核(不通过),15-选择线下支付(商城购物时审核通过),16-选择线下支付(商城购物时审核不通过),17-经销商续配额通知,18-新工单分配,19-工单完成", required = true)
    private Integer mechanism;
    @ApiModelProperty(value = "推送对象:1-用户,2-经销商,3-安装工程师,4-,下单人,5-水机用户,6-后台展示(系统保存),7-账号创建成功发送给代理商/经销商/创始人", required = true)
    private Integer pushObject;
    @ApiModelProperty(value = "推送方式：1-短信,2-翼猫APP,3-翼猫服务APP,4-翼猫业务系统", required = true)
    private Integer pushMode;
    @ApiModelProperty(value = "模版内容")
    private String template;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
