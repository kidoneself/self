package com.yimao.cloud.base.constant;

/**
 * @author Zhang Bo
 * @date 2018/8/16
 */
public class RabbitConstant {

    /**
     * 操作日志队列
     */
    public static final String SYSTEM_OPERATION_LOG = "direct.system.operationlog";

    /**
     * 请求接口路径日志
     */
    public static final String REQUEST_PATH_RECORD = "direct.system.req.path.record";

    /**
     * 消息推送
     */
    public static final String SYSTEM_MESSAGE_PUSH = "direct.system.messagepush";

    /**
     * 短信推送
     */
    public static final String SMS_MESSAGE_PUSH = "direct.sms.messagepush";

    /**
     * 安装工APP通知推送
     */
    public static final String ENGINEER_APP_MESSAGE_PUSH = "direct.engineer.app.messagepush";

    /**
     * 站务系统通知推送
     */
    public static final String STATION_MESSAGE_PUSH = "direct.station.messagepush";

    /**
     * 翼猫APP通知推送
     */
    public static final String YIMAO_APP_MESSAGE_PUSH = "direct.yimao.app.messagepush";

    public static final String ADMIN_LOG = "direct.admin.log";

    public static final String TRANSFER_OPERATION_LOG = "direct.transfer.operation.log";

    // public static final String USER_UPDATE_ID = "direct.user.update.id";

    public static final String USER_UPDATE = "direct.user.update";

    public static final String DISTRIBUTOR_UPDATE_ID = "direct.distributor.update.id";

    public static final String DISTRIBUTOR_UPDATE = "direct.distributor.update";

    public static final String ADMIN_UPDATE_ID = "direct.admin.update.id";

    public static final String ADMIN_UPDATE = "direct.admin.update";

    public static final String HRA_ALLOT = "direct.hra.allot";

    public static final String HRA_STEP = "direct.hra.step";

    public static final String WX_TEMPLATE_MESSAGE_QUEUE = "direct.wx.template.message";

    public static final String USER_DISTRIBUTOR_ORDER_QUEUE = "direct.distributor.order";

    public static final String SHOPCARD_COUNT_UPDATE = "direct.shopcard.count.update";

    public static final String WITHDRAW_AUDIT = "direct.withdraw.audit";

    public static final String USER_UPGRADEVIP = "direct.user.upgradevip";

    /**
     * 用户关注
     */
    public static final String USER_SUBSCRIBE_QUEUE = "direct.user.subscribe";
    /**
     * 用户身份变化记录
     */
    public static final String USER_CHANGE_QUEUE = "direct.user.change.record";

    /**
     * 取消关注
     */
    public static final String USER_UNSUBSCRIBE_QUEUE = "direct.user.unsubscribe";

    /**
     * 点赞，分享，人数累加
     */
    public static final String VIDEO_COUNT_ADD = "direct.video.count.add";

    /**
     * 资讯操作相关 人数累加
     */
    public static final String CONTENT_COUNT_ADD = "direct.content.count.add";

    public static final String WATER_OPERATION_LOG = "direct.water.operationlog";

    /**
     * 同步水机设备队列
     */
    public static final String SYNC_DEVICE = "direct.sync.device";

    /**
     * 水机设备故障信息
     */
    public static final String DEVICE_FAULT = "direct.device.fault";

    /**
     * 修改设备上的经销商信息
     */
    public static final String CHANGE_DEVICE_DISTRIBUTOR = "direct.device.change.distributor";

    /**
     * 客户信息修改时同步修改设备上的客户信息
     */
    public static final String CHANGE_DEVICE_USER = "direct.device.change.user";

    /**
     * 同步经销商信息队列
     */
    public static final String SYNC_DISTRIBUTOR = "direct.sync.distributor";
    /**
     * 同步安装工信息队列
     */
    public static final String SYNC_ENGINEER = "direct.sync.engineer";
    /**
     * 同步服务站队列
     */
    public static final String SYNC_STATION = "direct.sync.station";


    public static final String ENGINEER_CHANGE_RECORD = "direct.engineer.change.record";

    /**
     * 安装工程师正在服务的工单数
     */
    public static final String ENGINEER_BUSY_COUNT = "direct.engineer.busy.count";

    /**
     * 同步水机设备故障记录
     */
    public static final String SYNC_DEVICE_FAULT = "direct.sync.device.fault";

    /**
     * 同步维护工单
     */
    public static final String SYNC_MAINTENANCE_WORK_ORDER = "direct.sync.maintenance.work.order";

    /**
     * 订单支付成功回调
     */
    public static final String ORDER_PAY_CALLBACK = "direct.order.pay.callback";

    /**
     * 续费订单支付成功回调
     */
    public static final String RENEWORDER_PAY_CALLBACK = "direct.order.renew.pay.callback";

    /**
     * 经销商订单支付成功回调
     */
    public static final String DISTRIBUTOR_ORDER_PAY_CALLBACK = "direct.order.distributor.pay.callback";

    /**
     * 工单支付成功回调
     */
    public static final String WORK_ORDER_PAY_CALLBACK = "direct.order.workorder.pay.callback";

    /**
     * 工单操作记录
     */
    public static final String WORK_ORDER_OPERATION = "direct.order.workorder.operation";

    /**
     * 业务系统工单数据同步到售后系统
     */
    public static final String SYNC_WORK_ORDER = "direct.order.workorder.sync";

    /**
     * 发送短信
     */
    public static final String PHONE_MESSAGE_SEND = "direct.system.phonemsgsend";

    /**
     * 退款回调
     */
    public static final String REFUND_CALLBACK = "direct.order.refund.callback";

    /**
     * 配额变化
     */
//    public static final String QUOTA_CHANGE_RECORD = "direct.order.quota.change.record";

    /**
     * 老流程补库存后，重置工单step
     */
    public static final String RESET_WORK_ORDER_STEP = "direct.order.reset.workorder.step";

    /**
     * 增/减库存
     */
    public static final String INCREASE_OR_DECREASE_STOCK = "direct.system.increase.decrease.stock";

    /**
     * 开票
     */
    public static final String INSERT_WORK_ORDER_INVOICE = "direct.order.orderinvoice";

    /**
     * 地区上线同步工单
     */
    public static final String AREA_ONLINE_SYNC_WORK_ORDER = "direct.system.workorder.sync";

    /**
     * 地区上线同步工单后更新地区上线状态和同步状态
     */
    public static final String AREA_ONLINE_STATUS_SYNC = "direct.system.online.area.status";

    /**
     * 体验版经销商升级之后收益结算数据的处理
     */
    public static final String DISTRIBUTOR_UPGRADE_INCOME_STATUS = "direct.distributor.upgrade.income.status";

    /**
     * 体验版经销商升级之后给经销商下的工单“第一次升级时间”字段赋值
     */
    public static final String WORK_ORDER_SET_FIRST_UPGRADE_TIME = "direct.order.distributor.upgrade.set.firstupgradetime";

    /**
     * 导出动作触发
     */
    public static final String EXPORT_ACTION_HRA = "direct.export.action.hra";
    public static final String EXPORT_ACTION_ORDER = "direct.export.action.order";
    public static final String EXPORT_ACTION_SYSTEM = "direct.export.action.system";
    public static final String EXPORT_ACTION_USER = "direct.export.action.user";
    public static final String EXPORT_ACTION_WATER = "direct.export.action.water";
    /**
     * 导出记录
     */
    public static final String EXPORT_RECORD = "direct.export.record";

    /**
     * 经销商订单合同翼猫自动签署
     */
    public static final String DISTRIBUTOR_ORDER_PROTOCOL_YIMAO_SIGN = "direct.distributor.order.protocol.yimao.sign";

    /**
     * 库存操作记录
     */
    public static final String STORE_HOUSE_OPERATION_LOG = "direct.system.store.house.operation.log";
    
    /**
     * 退机完成生成退机库存纪录
     */
    public static final String STATION_BACK_STOCK_RECORD = "direct.system.station.back.stock.record";
    
    /**
     * 水机推送故障创建维修工单
     */
    public static final String WATERDEVICE_PUSH_REPAIRORDER_CREATE = "direct.water.push.repairorder.create";

    /**
     * 更新安装工表中服务站名称
     */
    public static final String ENGINEER_UPDATE_STATION_NAME = "direct.user.engineer.update.station.name";

}
