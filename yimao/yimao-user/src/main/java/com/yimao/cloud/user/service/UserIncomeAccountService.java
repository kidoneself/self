package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.UserIncomeAccountDTO;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/4/12
 */
public interface UserIncomeAccountService {
    /**
     * 收益账户
     * @return dto
     */
    UserIncomeAccountDTO getIncomeAccount();
}
