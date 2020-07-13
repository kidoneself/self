package com.yimao.cloud.user.service.impl;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.user.UserIncomeAccountDTO;
import com.yimao.cloud.user.mapper.UserIncomeAccountMapper;
import com.yimao.cloud.user.po.UserIncomeAccount;
import com.yimao.cloud.user.service.UserIncomeAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/4/12
 */
@Slf4j
@Service
public class UserIncomeAccountServiceImpl implements UserIncomeAccountService {

    @Resource
    private UserIncomeAccountMapper accountMapper;

    @Override
    public UserIncomeAccountDTO getIncomeAccount() {
        List<UserIncomeAccount> list = accountMapper.selectAll();
        UserIncomeAccountDTO dto = new UserIncomeAccountDTO();
        if (CollectionUtil.isNotEmpty(list)) {
            list.get(0).convert(dto);
            return dto;
        }
        return null;
    }
}
