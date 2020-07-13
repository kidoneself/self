package com.yimao.cloud.base.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class UUIDUtil {

    private static final char[] digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] num_digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    //兑换码显示规则：小写英文字母+数字
    private static final char[] JD_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /**
     * 生成16位随机UUID
     *
     * @return
     */
    public static String uuid() {
        Random localRandom = new Random();
        char[] arrayOfChar = new char[16];
        for (int i = 0; i < arrayOfChar.length; i++) {
            arrayOfChar[i] = digits[localRandom.nextInt(digits.length)];
        }
        return new String(arrayOfChar);
    }

    public static String numuuid(int length) {
        Random random = new Random();
        char[] arrayOfChar = new char[length];
        for (int i = 0; i < arrayOfChar.length; i++) {
            arrayOfChar[i] = num_digits[random.nextInt(num_digits.length)];
        }
        return new String(arrayOfChar);
    }

    public static String longuuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String longuuid32() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    public static String uuid16HashCode() {
        int machineId = 9;//最大支持1-9个集群机器部署
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//有可能是负数
            hashCodeV = -hashCodeV;
        }
        System.out.println(hashCodeV);
        // %011d
        // 0 代表前面补充0
        // 11 代表长度为12位
        // d 代表参数为正数型
        return machineId + String.format("%015d", hashCodeV);
    }

    /**
     * 主订单号生成规则<br/>
     * 订单号编码共15 位，采用纯数字，第1~8位采用8位日期，第9~15位采用随机数。
     * ===     1    ===    智能净水    ===
     * ===     2    ===    健康食品    ===
     * ===     3    ===    生物理疗    ===
     * ===     4    ===    健康睡眠    ===
     * ===     5    ===    健康评估    ===
     * ===     20   ===    商户订单号  ===
     * ===     21   ===    推送内容    ===
     * ===     22   ===    短信内容    ===
     * ==================================
     *
     * @return 订单号
     */
    public static long buildMainOrderId() {
        //当前日期年月日
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        return Long.parseLong(dateStr + numuuid(7));
    }

    /**
     * 订单号生成规则<br/>
     * 订单号编码共16 位，采用纯数字，第1 位代表产品类型编码，第2~9
     * 位采用8位日期，第10~16位采用随机数。
     *
     * @param orderType ==================================
     *                  ===     1    ===    智能净水    ===
     *                  ===     2    ===    健康食品    ===
     *                  ===     3    ===    生物理疗    ===
     *                  ===     4    ===    健康睡眠    ===
     *                  ===     5    ===    健康评估    ===
     *                  ===     20   ===    商户订单号  ===
     *                  ===     21   ===    推送内容    ===
     *                  ===     22   ===    短信内容    ===
     *                  ==================================
     * @return 订单号
     */
    public static long buildOrderId(int orderType) {
        //当前日期年月日
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        return Long.parseLong(orderType + dateStr + numuuid(7));
    }

    /**
     * 生成续费单号【22位数字】
     */
    public static String buildRenewWorkOrderId() {
        //当前日期年月日
        String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
        return dateStr + numuuid(5);
    }

    /**
     * 生成经销商订单单号
     *
     * @return
     */
    public static long buildRegisterWorkOrderId() {
        //当前日期年月日
        String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
        return Long.parseLong(dateStr + numuuid(2));
    }

    /**
     * 生成工单号
     *
     * @return
     */
    public static final String getWorkOrderNoToStr() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return simpleDateFormat.format(new Date()) + (new Random().nextInt(9000) + 1000);
    }

    /**
     * 生成物资申请订单id
     *
     * @return
     */
    public static final String getStationCompanyGoodsApplyOrderId() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return "WZSQ" + simpleDateFormat.format(new Date()) + (new Random().nextInt(9000) + 1000);
    }

    /**
     * 生成物资申请订单id
     *
     * @return
     */
    public static final String getMoveWaterDeviceOrderId() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return "YJ" + simpleDateFormat.format(new Date()) + (new Random().nextInt(9000) + 1000);
    }

    /**
     * 评估券号生成规则<br/>
     * 评估券编码共16 位，采用字母+数字，第1 位代表评估卡型号编码，采用1 个字母，
     * 不区分大小写，第2~16位采用随机数。
     *
     * @param cardType ==================================
     *                 ===     Y    ===    用户购买卡   ===
     *                 ===     F    ===    服务站用卡   ===
     *                 ===     M    ===    免费体验卡   ===
     *                 ==================================
     * @return 评估号
     */
    public static String buildHraTicketNo(String cardType) {
        return cardType + numuuid(15);
    }

    /**
     * 6位用户编号，第一位不为0
     *
     * @return 用户编号
     */
    public static String buildUserId() {
        return uuid();
    }


    /**
     * 兑换码
     * 从[0~9][a~z]里面随机不重复6位
     *
     * @param num num位的随机数
     * @return 随机码
     */
    public static String buildExchange(int num) {
        String randomCode = "";
        for (int j = 0; j < num; j++) {
            char c = JD_DIGITS[(int) (Math.random() * JD_DIGITS.length)];
            randomCode = randomCode + c;
        }
        return randomCode;
    }

    /**
     * 兑换码 批次号
     *
     * @param channelName 渠道(小写)
     * @param num         最后随机数长度
     * @return
     */
    public static String buildBatchNumber(String channelName, Integer num) {
        if (StringUtil.isEmpty(channelName)) {
            channelName = "翼猫(YM)";
        }
        StringBuffer buffer = new StringBuffer();
        return buffer.append(DateUtil.formatCurrentTime("yyyyMMdd")).append(channelName.substring(channelName.lastIndexOf("(") + 1, channelName.lastIndexOf(")")).toLowerCase()).append(UUIDUtil.numuuid(num)).toString();
    }


    /**
     * 评估卡号生成规则2.0<br/>
     * 评估卡编码共16 位，采用字母+数字，第1位代表评估卡型号编码，采用1个字母，
     * 不区分大小写，第2~9位采用8位日期，第10~16位采用随机数。
     *
     * @param cardType ==================================
     *                 ===     Y    ===    用户购买卡   ===
     *                 ===     F    ===    服务站用卡   ===
     *                 ===     M    ===    免费体验卡   ===
     *                 ==================================
     * @return 评估号
     */
    public static String buildHraCardNo(String cardType) {
        //当前日期年月日
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        return cardType + dateStr + numuuid(7);
    }


    /**
     * 生成退机工单号
     *
     * @return
     */
    public static final String getWorkOrderBackNoToStr() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return simpleDateFormat.format(new Date()) + (new Random().nextInt(9000) + 1000);
    }
    
    /**
     * 生成维修工单号
     *
     * @return
     */
    public static final String getWorkRepairOrderNoToStr() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return "WX" +simpleDateFormat.format(new Date()) + (new Random().nextInt(9000) + 1000);
    }
}
