package com.yimao.cloud.base.baideApi.constant;


import com.yimao.cloud.base.baideApi.utils.GetBaiDeUtil;

public interface Constant {
//    String BAIDE_HTTP_URL = PropertiesUtil.getProperty("BAIDE_HTTP_URL");
//    String PUBLIC_KEY = PropertiesUtil.getProperty("PUBLIC_KEY");
    //String YIMAO_HTTP_URL="https://yun.yimaokeji.com/";
    //String YIMAO_HTTP_URL="http://yimao-test.yunext.com/";
    /*String BAIDE_HTTP_URL = "https://service.yimaokeji.com:81";*/
   /* String BAIDE_HTTP_URL = "https://service-test.yimaokeji.com";*/
    //本地环境
    String BAIDE_HTTP_URL_LOCAL = "http://127.0.0.1:8067";
    //开发环境
    String BAIDE_HTTP_URL_DEV = "http://192.168.10.61:8067";
    //测试环境
    String BAIDE_HTTP_URL_TEST = "http://service-test.yimaokeji.com:8680";
    //正式环境
    String BAIDE_HTTP_URL_PRO = "http://service.yimaokeji.com:81";
    //测试
    //String PUBLIC_KEY =   "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDkuWhTYcrR4Qlal430f8kAzmeKETMa62Q58PbTzQpdBSPAkmD2lvbya9qO0YsclAtJ6AXAt002mqtyXx9+ovuQFX744Z9Qy7HYLrOTJP2UOc8m0hL113yPm1zu+UKEd4LvA673X3sCiuCX8EcPKJPEwS+IzH1EzC6wa3JjrBvjVwIDAQAB";
    //String PUBLIC_KEY =   "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC08SB4I1L7NWw2sXyBZvhpFLAKUFGmPgnW1V1lbJK5nh/wqfXcwWSkO0odV0xXLTUl5t86mWYAitL2RxGu2Wskw2c+g4WIm5CGJJD765vGKqDqZGOkrtCbvK9SCkEgA3YZUQTUOBYSyzKT8/JpLwEB479lB35CCVN6nplucI6BKwIDAQAB";
    String PUBLIC_KEY_TEST = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDkuWhTYcrR4Qlal430f8kAzmeKETMa62Q58PbTzQpdBSPAkmD2lvbya9qO0YsclAtJ6AXAt002mqtyXx9+ovuQFX744Z9Qy7HYLrOTJP2UOc8m0hL113yPm1zu+UKEd4LvA673X3sCiuCX8EcPKJPEwS+IzH1EzC6wa3JjrBvjVwIDAQAB";
    //正式
    String PUBLIC_KEY_PRO = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDkuWhTYcrR4Qlal430f8kAzmeKETMa62Q58PbTzQpdBSPAkmD2lvbya9qO0YsclAtJ6AXAt002mqtyXx9+ovuQFX744Z9Qy7HYLrOTJP2UOc8m0hL113yPm1zu+UKEd4LvA673X3sCiuCX8EcPKJPEwS+IzH1EzC6wa3JjrBvjVwIDAQAB";
    /**
     * 工单接口
     */
    String WORKORDER = GetBaiDeUtil.getBaideUrl() + "/api/workOrderInstall";
    /**
     * 维护工单url地址头
     */
    String MAINTENANCE_WORKORDER_URL = GetBaiDeUtil.getBaideUrl() + "/api/workOrderMaintenance";
    /**
     * 维修工单url地址头
     */
    String REPAIR_WORKORDER_URL = GetBaiDeUtil.getBaideUrl() + "/api/workOrderRepair";
    /**
     * 续费工单url地址头
     */
    String RENEW_WORKORDER_URL = GetBaiDeUtil.getBaideUrl() + "/api/workOrderRenew";
    /**
     * 换机工单url地址头
     */
    String EXCHANGE_WORKORDER_URL = GetBaiDeUtil.getBaideUrl() + "/api/workOrderExchange";
    /**
     * 工单颜色配置信息
     */
    String WORKORDER_DECISION_RULE_INFO = GetBaiDeUtil.getBaideUrl() + "/api/workOrderDecisionRule/list";
    /**
     * 新增工单
     */
    String ADD_WORKORDER = WORKORDER + "/addWorkOrderInstall";
    /**
     * 同步工单
     */
    String SYNC_WORKORDER = WORKORDER + "/importWorkOrder";
    /**
     * 同步完款信息
     */
    String SYNC_COMPLETE_WORKORDER = WORKORDER + "/changePayComplete";
    /**
     * 删除工单
     */
    String DELETE_WORKORDER = WORKORDER + "/deleteInstallByCode";
    /**
     * 工单列表
     */
    String LIST_WORKORDER = WORKORDER + "/listPage";
    /**
     * 处理中小状态工单列表
     */
    String HANDING_LIST_WORKORDER = WORKORDER + "/listDetailStatusPage";
    /**
     * 身份证是否必填获取接口
     */
    String CONFIRM_IDCARD_WORKORDER = WORKORDER + "/confirmIdCard";
    /**
     * 派单
     */
    String ASSIGN_WORKORDER = WORKORDER + "/assignWorkOrder";
    /**
     * 编辑工单
     */
    String EDIT_WORKORDER = WORKORDER + "/editWorkOrder";
    /**
     * 接单
     */
    String ACCEPT_WORKORDER = WORKORDER + "/acceptButton";
    /**
     * 拒单
     */
    String REJECT_WORKORDER = WORKORDER + "/rejectButton";
    /**
     * 预约工单
     */
    String APPOINTMENT_WORKORDER = WORKORDER + "/appointmentButton";
    /**
     * 服务
     */
    String START_WORKORDER = WORKORDER + "/startButton";
    /**
     * 修改机型、计费方式
     */
    String CHANGETYPE_AND_MODEL_WORKORDER = WORKORDER + "/changeTypeAndModle";
    /**
     * 对内型号、生产商信息
     */
    String SUPPLIER_PRODUCT = WORKORDER + "/getEquipTypeSupplier";
    /**
     * 批次码
     */
    String BATCH_WORKORDER = WORKORDER + "/scanBatchCode";

    /**
     * SN码
     */
    String SNCODE_WORKORDER = WORKORDER + "/scanSnCode";
    /**
     * SIM卡
     */
    String SIMCARD_WORKORDER = WORKORDER + "/scanSimCode";
    /**
     * 支付
     */
    String PAYMEMENT_WORKORDER = WORKORDER + "/updatePaymentMeans";
    /**
     * 支付审核通知
     */
    String UPDATE_PAYMENT_WORKORDER = WORKORDER + "/updateAuditAndPayStatus";
    /**
     * 上传水质图片
     */
    String PHOTO_WORKORDER = WORKORDER + "/waterQualityPhoto";
    /**
     * 用户信息确认
     */
    String CONFIRM_USERINFO_WORKORDER = WORKORDER + "/confirmUserInformation";
    /**
     * 修改发票状态
     */
    String INVOICE_CHANGE_WORKORDER = GetBaiDeUtil.getBaideUrl() + "/api/workOrderWechat/changeBillType";
    /**
     * 开发票
     */
    String INVOICE_WORKORDER = GetBaiDeUtil.getBaideUrl() + "/api/workOrderWechat/updateEinvoiceInfo";
    /**
     * 校验批次码
     */
    String VALIDATE_BATCHCODE = GetBaiDeUtil.getBaideUrl() + "/api/workOrderWechat/validateBatchCode";
    /**
     * 签约
     */
    String CONTRACT_SIGN_TYPE = WORKORDER + "/saveContractType";
    /**
     * 用户协议
     */
    String CONTRACT_WORKORDER = WORKORDER + "/signContract";
    /**
     * 工单修改预约时间
     */
    String CHANGEDATE_WORKORDER = WORKORDER + "/changePlanServiceDate";
    /**
     * 更换设备、修改设备信息
     */
    String EDIT_INFORMATION = WORKORDER + "/changeOrEditEquipment";
    /**
     * 退单
     */
    String BACK_WORKORDER = WORKORDER + "/backOrderButton";
    /**
     * 同意退单
     */
    String AGREE_BACK_WORKORDER = WORKORDER + "/orderAuditResult";
    /**
     * 修改SN提交时间
     */
    String CHANGE_SNCODE_TIME = WORKORDER + "/changeSncodeTime";
    /**
     * 详情
     */
    String DETAIL_WORKORDER = WORKORDER + "/queryByCode";

    /**
     * 完成工单
     */
    String COMPLETE_WORKORDER = WORKORDER + "/finishButton";
    /**
     * 工程师工单数
     */
    String COUNT_WORKORDER = WORKORDER + "/countForOrderStatus";
    /**
     * 工程师维修建单权限
     */
    String CREATE_REPAIR_AUTHORIZE = REPAIR_WORKORDER_URL + "/verifyWorkOrderRepairAdd";
    /**
     * 同步安装工程师数据
     */
    String SYNC_ENGINEER = GetBaiDeUtil.getBaideUrl() + "/api/engineer/synchronizeEngineer";
    /**
     * 同步服务站数据
     */
    String SYNC_SERVICESITE = GetBaiDeUtil.getBaideUrl() + "/api/serviceStation/synchronizeServiceStation";
    /**
     * 同步产品数据
     */
    String SYNC_PRODUCT = GetBaiDeUtil.getBaideUrl() + "/api/product/synchronizeProduct";
    /**
     * 保存发票信息
     */
    String SAVE_INVOICE = WORKORDER + "/saveInvoiceInfo";
    /**
     * 维护工单新增
     */
    String MAINTENANCE_WORKORDER_ADD = MAINTENANCE_WORKORDER_URL + "/addWorkOrderMaintenance";
    /**
     * 维护工单扫描二维码
     */
    String MAINTENANCE_WORKORDER_SCANBATCHCODE = MAINTENANCE_WORKORDER_URL + "/scanBatchCode";
    /**
     * 是否必须上传销毁图片
     */
    String MAINTENANCE_WORKORDER_MUSTUPLOADIMG = MAINTENANCE_WORKORDER_URL + "/mustUploading";
    /**
     * 维护工单上传滤芯销毁图片
     */
    String MAINTENANCE_WORKORDER_UPLOAD_MATERIEL_IMG = MAINTENANCE_WORKORDER_URL + "/imgUploading";







    /* 维修工单接口 start */
    /**
     * 维修工单创建
     */
    String REPAIR_WORKORDER_ADD = REPAIR_WORKORDER_URL + "/addWorkOrderRepair";
    /**
     * 维修工单修改经销商信息
     */
    String REPAIR_WORKORDER_UPDATE_DEALERINFO = REPAIR_WORKORDER_URL + "/updateDealerAndChildDealer";
    /**
     * 维修工单填写故障信息
     */
    String REPAIR_WORKORDER_FACTFAULTDESCRIBE = REPAIR_WORKORDER_URL + "/saveFaultDescribeAndMaterielInfo";
    /**
     * 维修工单保存更换物料明细接口
     */
    String REPAIR_WORKORDER_SAVE_MATERIELINFO = REPAIR_WORKORDER_URL + "/saveMaterielInfo";
    /**
     * 维修工单换机申请
     */
    String REPAIR_WORKORDER_APPLY_EXCHANGE_DEVICE = REPAIR_WORKORDER_URL + "/applyExchange";
    /**
     * 维修工单故障信息列表
     */
    String REPAIR_WORKORDER_FACTFAULTDESCRIBE_LIST = REPAIR_WORKORDER_URL + "/backFactFaultDescribeList";
    /**
     * 维修工单耗材数据信息列表
     */
    String REPAIR_WORKORDER_MATERIEL_DATA_LIST = REPAIR_WORKORDER_URL + "/getMaterielList";
    /**
     * 维修工单耗材联动列表
     */
    String REPAIR_WORKORDER_MATERIEL_DATA_TREE = REPAIR_WORKORDER_URL + "/getMaterielTreeByType";
    /**
     * 维修工单根据维修工单id查询工单详情
     */
    String REPAIR_WORKORDER_QUERY_BY_REPAIRID = REPAIR_WORKORDER_URL + "/queryByCode";
    /**
     * 二级联动故障菜单
     */
    String REPAIR_WORKORDER_QUERY_FAULTTYPE = REPAIR_WORKORDER_URL + "/queryFaultType";
    /**
     * 三级联动故障现象惨淡
     */
    String REPAIR_WORKORDER_QUERY_FAULTPHENOMENON = REPAIR_WORKORDER_URL + "/getFaultPhenomenon";
    /**
     * 更换设备
     */
    String REPAIR_WORKORDER_PUT_NEWDEVICE_SN_BA_IC = REPAIR_WORKORDER_URL + "/scanBCAndSNAndIccid";
    /**
     * 推送tds异常,制水故障信息到百得
     */
    String REPAIR_WORKORDER_INSERT_SYSTEM_TIP = REPAIR_WORKORDER_URL + "/insertSystemTip";
    /* 维修工单接口 end */

    /* 库存url start*/

    /**
     * 工程师设备可用良品库
     */
    String ENGINEER_STOCK = GetBaiDeUtil.getBaideUrl() + "/api/stock/engineerStock";
    /**
     * 工程师服务质量得分排名
     */
    String ENGINEER_RANKING = GetBaiDeUtil.getBaideUrl() + "/api/stock/engineerRanking";
    /**
     * 安装工程师产品库存
     */
    String ENGINEER_STOCK_MATERIEL = GetBaiDeUtil.getBaideUrl() + "/api/stock/engineerStockMateriel";
    /**
     * 服务站产品体系库存
     */
    String SERVICESITE_PRODUCT_STOCK = GetBaiDeUtil.getBaideUrl() + "/api/stock/findStationProductConsumable";
    /**
     * 服务站物料体系库存
     */
    String SERVICESITE_MATERIEL_STOCK = GetBaiDeUtil.getBaideUrl() + "/api/stock/findStationConsumable";
    /**
     * 服务站下安装工产品体系库存
     */
    String SERVICESITE_ENGINERR_PRODUCT_STOCK = GetBaiDeUtil.getBaideUrl() + "/api/stock/findEngineerProductConsumable";
    /**
     * 服务站下安装工的物料体系库存
     */
    String SERVICESITE_ENGINEER_CONSUMABLE_FOR_STATION = GetBaiDeUtil.getBaideUrl() + "/api/stock/findEngineerConsumableForStation";
    /**
     * 安装工申请/借调接口
     */
    String ENGINEER_APPLY_LIST = GetBaiDeUtil.getBaideUrl() + "/api/stock/findEngineerApplyListById";
    /**
     *安装工的同意、拒绝借调接口
     */
    String ENGINEER_APPLY_OPERATE = GetBaiDeUtil.getBaideUrl() + "/api/stock/engineerApplyOperate";
    /**
     * 向服务站申请设备和物料申请接口
     */
    String MATERIEL_APPLY = GetBaiDeUtil.getBaideUrl() + "/api/stock/materielApply";
    /**
     * 同服务站安装工之间设备的借调申请接口
     */
    String ENGINEER_MATERIEL_ALLOT = GetBaiDeUtil.getBaideUrl() + "/api/stock/engineerMaterielAllot";
    /**
     * 安装工产品和物料扫描入库接口
     */
    String ENGINEER_PUT_STOCK = GetBaiDeUtil.getBaideUrl() + "/api/stock/engineerPutStock";
    /**
     * 扫描批次码查看设备和滤芯状态接口
     */
    String MATERIEL_STATUS = GetBaiDeUtil.getBaideUrl() + "/api/stock/getMaterielStatus";
    /**
     * 设备/物料库存详情接口
     */
    String MATERIEL_STOCK_LIST = GetBaiDeUtil.getBaideUrl() + "/api/stock/getMaterielStockList";
    /**
     * 工程师入库历史记录
     */
    String ENGINEER_STOCK_HISTORY_INFO = GetBaiDeUtil.getBaideUrl() + "/api/stock/getStockInHistory";
    /**
     * 工程师不良品转良品
     */
    String ENGINEER_MATERIEL_STATUS_CHANGE = GetBaiDeUtil.getBaideUrl() + "/api/stock/engineerMaterielStatusChange";
    /**
     * 通过sncode查询设备、物料基本信息
     */
    String MATERIEL_INFO_BY_SNCODE = GetBaiDeUtil.getBaideUrl() + "/api/stock/getMaterielInfoBySnCode";
    /**
     * 安装工禁用,离职转让
     */
    String ENGINEER_DIMISSION = WORKORDER + "/dimission";
    /**
     * 新增续费工单
     */
    String RENEW_WORKORDER_ADD = RENEW_WORKORDER_URL + "/addWorkOrderRenew";
    /**
     * 续费工单申请开票
     */
    String RENEW_WORKORDER_APPLY_INVOICE = RENEW_WORKORDER_URL + "/openTicket";
    
    /**
     * 续费工单更新设备余额
     */
    String RENEW_WORKORDER_UPDATE_MONEY = RENEW_WORKORDER_URL + "/updateMoneyOfSncode";
    
}
