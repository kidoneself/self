package com.yimao.cloud.engineer.enums;

public enum ServiceStatusCode {
    SUCCESS("000", "SUCCESS", "成功"),
    SER_FORBIDDEN("001", "FORBIDDEN", "拒绝访问"),
    SER_FAILURE("002", "FAILURE", "失败"),
    SER_EXECUTE_TIMEOUT("003", "EXECUTE TIMEOUT", "执行超时"),
    SER_PARAM_ERROR("004", "PARAM LOST", "参数缺失或错误"),
    SER_BUSY("005", "BUSY", "系统繁忙"),
    SER_EXCEPTION("006", "EXCEPTION", "系统异常"),
    SER_DISK_ERROR("007", "DISK ERROR", "系统磁盘异常"),
    SER_NO_DATA("008", "NO DATA FOF DISPLAY", "没有可供展示的数据"),
    SER_UNKNOW("009", "UNKNOW", "未知状态"),
    ACCESS_LOST("010", "ACCESS LOST", "连接丢失"),
    ACCESS_NO_RESPONSE("011", "ACCESS NO RESPONSE", "连接无响应"),
    ACCESS_TIME_OUT("012", "ACCESS TIME OUT", "连接超时"),
    ACCESS_LIMITED("013", "ACCESS LIMITED", "连接超限"),
    DB_LOST("020", "DB LOST", "数据库连接丢失"),
    DB_INIT_ERROR("021", "DB INIT ERROR", "数据库初始化失败"),
    DB_TIMEOUT("022", "DB TIMEOUT", "数据库访问超时"),
    DB_EXCEPTION("023", "DB EXCEPTION", "数据库异常"),
    DB_SQL_ERROR("024", "SQL ERROR", "SQL语句错误"),
    DB_SQL_TRANSACTION_COMITED("025", "DB RANSACTION COMITED", "已经提交过事务，分布式环境可能会存在脏读写，拒绝"),
    DB_MYDQL_LOST("030", "MYSQL LOST", "MYSQL 连接丢失"),
    DB_MYDQL_INIT_ERROR("031", "MYSQL INIT ERROR", "MYSQL 初始化失败"),
    DB_MYDQL_TIMEOUT("032", "MYSQL TIMIOUT", "MYSQL访问超时"),
    DB_MYDQL_EXCEPTION("033", "MYSQL EXCEPTION", "MYSQL异常"),
    DB_MYDQL_SQL_ERROR("034", "MYSQL SQL EEROR", "MYSQL SQL语句错误"),
    CORE_USER_ACCOUNT_ID_IS_NULL("040", "USER ACCOUNT ID IS NULL", "用户账户ID不能为空"),
    CORE_USER_INFO_ID_IS_NULL("041", "USER INFO ID IS NULL", "用户信息ID不能为空"),
    CORE_USERID_EXISTES("042", "USER ID EXISTES", "用户id已经存在"),
    CORE_USER_NO_FATHER_USER_ID("043", "CHILD ID MUST HAVE FATHER ID", "子账号必须指定主账号"),
    CORE_USER_ID_MUST_BE_MAIN("044", "USER ID MUST BE MAIN", "必须是主账号才能操作"),
    CORE_USER_NOT_EXISTS("045", "USER ACCOUNT NOT EXISTS", "用户账户不存在"),
    CORE_ROLE_NOT_EXISTS("046", "ROLE NOT EXISTS", "角色不存在"),
    CORE_ROLE_GROUP_NOT_EXISTS("047", "ROLE GROUP NOT EXISTS", "角色组不存在"),
    CORE_USER_ROLE_GROUP_IS_NULL("048", "USER NO ROLE GROUP", "未指定用户角色组"),
    CORE_USER_ROLE_IS_NULL("049", "USER NO ROLE", "未指定用户角色"),
    CORE_USER_ROLE_NO_RIGHT("050", "NO RIGHT ", "没有权限"),
    ORDER_NOT_CONFIRMED("051", "ORDER IS NOT CONFIRMED", "订单尚未确认"),
    ORDER_IS_CONFIRMED("052", "ORDER IS CONFIRMED", "订单已经确认，无法更新"),
    ORDER_IS_PAIED("053", "ORDER IS PAIED", "订单已支付"),
    ORDER_IS_NOT_PAY("054", "ORDER NOT PAY", "订单尚未支付"),
    ORDER_IS_CANCEL("055", "ORDER IS CANCEL", "订单已经取消，无法操作"),
    ORDER_IS_FINISH("056", "ORDER IS FINISH", "订单已经完成，无法操作"),
    ORDER_PAYMENT_IS_BIND_ALIPAYACCOUNT("057", "PAYMENT ALREADY BIND ALIPAY", "支付单已经绑定了提现账号"),
    ORDER_KIND_NOT_ALLOW("058", "ORDER KIND ERROR ", "订单类型信息不合法"),
    ORDER_NOT_EXISTS("059", "ORDER NOT EXISTS", "订单不存在"),
    ORDER_ID_NOT_MATCHED("060", "ORDER ID NOT MATCHED ", "订单id不匹配"),
    MONEY_IS_NOT_ENOUGH("061", "MONEY IS NOT ENOUGH", "余额不足以支付该业务"),
    GOODS_SYSTEM_NOT_EXISTS("070", "GOODSSYSTEM NOT EXISTS", "未找到对应的产品体系"),
    GOODS_SYSTEM_PAYACCOUNT_IS_NOT_CONFIG("071", "PAYACCOUNT IS NOT CONFIG ", "支付帐套未配置"),
    GOODS_SYSTEM_PRODUCT_MODEL_NOT_EXISTS("072", "PRODUCT MODEL IS NOT EXISTS ", "产品型号不存在"),
    COMMON_ADDRESS_NOT_REGION("080", "ADDRESS INFO NOT REGION", "地址不是区县界别的信息"),
    ASSEMBLY_RED_ACCOUNT_NOT_EXISTS("081", "RED ACCOUNT NOT EXISTS", "[组件服务]红包账户不存在"),
    ASSEMBLY_RED_RULE_NOT_EXISTS("082", "RED RULE NOT EXISTS", "[组件服务]红包规则不存在"),
    ASSEMBLY_RED_TYPE_NOT_EXISTS("083", "RED TYPE NOT EXISTS", "[组件服务]红包类型不存在"),
    ASSEMBLY_RED_MONEY_ERROR("084", "RED MONEY ERROR", "[组件服务]金额不符合规则"),
    ASSEMBLY_RED_MONEY_IS_NOT_ENOUGH("085", "RED MONEY NOT ENOUGH", "[组件服务]红包账户余额不足以支付"),
    ASSEMBLY_RED_ACCOUNT_NOT_BIND_ALIPAY("086", "RED ACCOUNT NOT BIND ALIPAY", "用户尚未绑定支付宝提现账号"),
    ASSEMBLY_RED_NOT_ESISTS("087", "RED NOT EXISTS", "[组件服务]红包不存在"),
    ORDER_PAYMENT_APPLYED("090", "ORDER PAYMENT APPLY STATUS IS TRUE", "支付单已被审核,无法重复审核"),
    ORDER_PAYMENT_APPLYED_NOT_PASS_NEED_REASON("091", "ORDER PAYMENT APPLY NOT PASS NEED REASON", "审核不通过需要输入具体的原因."),
    ORDER_PAYMENT_APPLYED_NOT_PASS_REASON_TOO_LONG("092", "ORDER PAYMENT APPLY NOT PASS  REASON TOO LONG", "审核不通过原因超过最大长度"),
    ORDER_PAYMENT_NOT_ALREADY_CHECK_OUT("093", "ORDER PAYMENT NOT ALREADY CHECK OUT", "请稍等.系统正在为您生成审核单"),
    WORK_ORDER_ID_EXISTES("100", "WORK ORDER ID IS EXISTES", "工单号已存在"),
    WORK_ORDER_ID_NOT_EXISTES("101", "WORK ORDER ID NOT EXISTES", "工单号不存在"),
    WORK_ORDER_ID_IS_EMPTY("102", "WORK ORDER ID NOT EMPTY", "工单号不得为空"),
    WORK_ORDER_IS_FINISH("103", "WORK ORDER IS FINISH", "工单已完成.不进行操作"),
    WATERDEVICE_NOT_EXISTES("110", "WATERDEVICE NOT EXISTES", "设备不存在"),
    WATERDEVICE_IS_EXISTES("111", "WATERDEVICE IS EXISTES", "设备已存在"),
    BATCHCODE_EXSISTS_ERROR("112", "BATCHCODE EXSISTS ERROR", "batchCode已存在"),
    SNCODE_EXSISTS_ERROR("113", "SNCODE EXSISTS ERROR", "sncode已存在"),
    SIMCARD_EXSISTS_ERROR("114", "SIMCARD_EXSISTS ERROR", "simcard已存在"),
    WATERDEVICE_MD5_VALUE_IS_EXISTES("115", "WATERDEVICE MD5 VALUE IS EXISTES", "设备数据未做改变,无需update"),
    PARAM_ID_NOT_EXISTES("121", "PARAM ID NOT EXISTES IN SYSTEM", "系统里不存在该id"),
    ORDER_ID_NOT_EXISTES("122", "ORDER ID NOT EXISTES IN SYSTEM", "订单id不存在"),
    ORDER_ID_ALREADY_PAY("123", "ORDER ID ALREADY PAY", "订单已支付"),
    ORDER_ID_CONFIRM_ERROR("124", "ORDER ID CONFIRM ERROR", "订单确认失败"),
    ORDER_PAYMENT_CREATE_ERROR("125", "ORDER PAYMENT CREATE ERROR", "支付单创建失败"),
    ORDER_CENTER_BATCH_NUM_LESS_THAN_ZERO("126", "ORDER CENTER BATCH ORDER LESS THAN ZERO", "净水订单单产品数量小于0"),
    ORDER_CENTER_ORDER_PAY_ROLE_IS_USER("127", "ORDER CENTER PAY ROLE IS USER", "净水订单支付仅限于用户支付"),
    ORDER_CENTER_ORDER_PAY_ROLE_IS_DIS("128", "ORDER CENTER PAY ROLE IS DIS", "净水订单支付仅限于经销商支付"),
    ORDER_CENTER_OTHER_PAY_APPLY_NOT_UNCHECK("129", "ORDER CENTER OTHER PAY APPLY NOT UNCHECK", "其他支付审核单允许审核"),
    WATERDEVICE_CONFIG_COLOR_IS_EXISTS("130", "WATER DEVICE COLOR CONFIG IS EXISTS", "该颜色配置已存在,请执行修改规则/更新 操作"),
    JSON_OPERATION_ANALYSIS_FAILURE("140", "json data analysis failure", "json数据解析失败"),
    ALIPAY_ERROR_BIND_ACCOUNT_IS_EXISTS("150", "BIND ACCOUNT IS EXISTS", "此支付宝账号已绑定"),
    INVOICE_ERROR_REPETITION("160", "MAKE OUT AN INVOICE", "请不要重复开票"),
    ENGINEER_NOT_FOUND("170", "ENGINEER NOT FOUND", "此工程师不存在"),
    OTHER_PAY_TYPE_UNKNOW("180", "UNKNOW OTHER PAY TYPE", "未知的其他支付类型"),
    COMMON_ADDRESS_IS_NULL("190", " ADDRESS IS NULL", "地址不得为空"),
    COMMON_OTHER_PAY_TYPE_NEED_IMG("191", "OTHER PAY TYPE NEED IMG", "请至少上传一张图片"),
    COMMON_UPLOAD_IMG_ERROR("193", "IMG ERROR", "请上传jpg,png格式的图片"),
    COMMON_UPLOAD_IMG_ANALYSIS_ERROR("194", "IMG ANALYSIS ERROR", "解析图片异常"),
    COMMON_CHARGE_TYPE_ERROR("195", "CHARGE TYPE ERROR", "计费方式错误"),
    COMMON_CHARGE_NOT_EXISTS_ERROR("196", "CHARGE TYPE NOT EXISTS ERROR", "计费方式不存在"),
    UTIL_ERROR_ABOUT_DATE_FORMAT("301", "UTIL ERROR ABOUT DATE FORMAT", "时间格式异常,转换错误!"),
    BAIDE_WORKORDER_INSTALL_OTHER_PAY_APPLY_ERROR("401", "WORK ORDER INSTALL OTHER PAY APPLY ERROR", "售后其他支付申请异常"),
    NONE("999", "NONE", "未定义");

    String code;
    String text;
    String textZh;

    private ServiceStatusCode(String code, String text, String textZh) {
        this.code = code;
        this.text = text;
        this.textZh = textZh;
    }

    public String getCode() {
        return this.code;
    }

    public String getText() {
        return this.text;
    }

    public String getTextZh() {
        return this.textZh;
    }

    public static ServiceStatusCode getByCode(String code) {
        ServiceStatusCode[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ServiceStatusCode en = var1[var3];
            if (en.getCode().equals(code)) {
                return en;
            }
        }

        return SER_UNKNOW;
    }

    public String toString() {
        return this.getCode() + " : " + this.getTextZh() + " " + this.getText();
    }
}
