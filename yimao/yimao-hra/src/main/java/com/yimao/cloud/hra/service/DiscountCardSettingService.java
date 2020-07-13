package com.yimao.cloud.hra.service;

import com.yimao.cloud.hra.po.DiscountCardSetting;

/**
 * @author Zhang Bo
 * @date 2018/1/24.
 */
public interface DiscountCardSettingService {
    DiscountCardSetting getGiveCount(Integer userType, String company);
}
