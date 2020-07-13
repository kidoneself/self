package com.yimao.cloud.order.utils;

import com.yimao.cloud.base.enums.ProductBuyPermission;
import com.yimao.cloud.base.utils.StringUtil;

/**
 * 描述：校验用户产品购买权限
 *
 * @Author Zhang Bo
 * @Date 2019/3/27
 */
public class ProductBuyPermissionUtil {

    /**
     * 校验用户产品购买权限
     *
     * @param buyPermission 产品购买权限字段值
     * @param userType      用户类型
     */
    public static boolean check(String buyPermission, Integer userType) {
        if (StringUtil.isNotBlank(buyPermission)) {
            switch (userType) {
                case 1:
                    return buyPermission.contains(ProductBuyPermission.D_350.code);
                case 2:
                    return buyPermission.contains(ProductBuyPermission.D_650.code);
                case 3:
                    return buyPermission.contains(ProductBuyPermission.U_3.code);
                case 4:
                    return buyPermission.contains(ProductBuyPermission.U_4.code);
                case 5:
                    return buyPermission.contains(ProductBuyPermission.D_1000.code);
                case 6:
                    return buyPermission.contains(ProductBuyPermission.D_950.code);
                case 7:
                    return buyPermission.contains(ProductBuyPermission.U_7.code);
                case 8:
                    return buyPermission.contains(ProductBuyPermission.D_0.code);
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
}
