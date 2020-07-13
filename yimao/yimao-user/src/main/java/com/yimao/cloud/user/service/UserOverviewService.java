package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.system.BusinessProfileDTO;
import com.yimao.cloud.pojo.dto.user.UserOverviewDTO;

public interface UserOverviewService {

    /**
     * 用户概况
     *
     * @return UserOverviewDTO
     * @author hhf
     * @date 2019/3/19
     */
    UserOverviewDTO overview();

    /**
     * 经营概况（用户相关）
     *
     * @param
     * @return BusinessProfileDTO
     * @author hhf
     * @date 2019/3/27
     */
    BusinessProfileDTO overviewBusiness();
}
