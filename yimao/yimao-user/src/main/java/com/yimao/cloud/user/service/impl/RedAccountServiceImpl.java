package com.yimao.cloud.user.service.impl;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.user.mapper.*;
import com.yimao.cloud.user.po.*;
import com.yimao.cloud.user.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class RedAccountServiceImpl implements RedAccountService {
    @Resource
    private RedAccountMapper redAccountMapper;
    @Resource
    private DistributorMapper distributorMapper;

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.RedAccountDTO
     * @description 根据新id获取红包账户
     * @author Liu Yi
     * @date 2019/9/9 13:41
     */
    @Override
    public RedAccountDTO getRedAccountById(Integer id) {
        RedAccountDTO dto = new RedAccountDTO();
        RedAccount redAccount = redAccountMapper.selectByPrimaryKey(id);
        if (redAccount != null) {
            redAccount.convert(dto);
            return dto;
        }
        return null;
    }

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.RedAccountDTO
     * @description 根据老id获取红包账户
     * @author Liu Yi
     * @date 2019/9/9 13:41
     */
    @Override
    public RedAccountDTO getRedAccountByOldId(String oldId) {
        RedAccountDTO dto = new RedAccountDTO();
        Example example = new Example(RedAccount.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("oldId", oldId);
        RedAccount redAccount = redAccountMapper.selectOneByExample(example);
        if (redAccount != null) {
            redAccount.convert(dto);
            return dto;
        }
        return null;
    }

    /**
     * @param accountType 1-安装工 2-经销商(各种身份)
     * @return com.yimao.cloud.pojo.dto.user.RedAccountDTO
     * @description 根据账户id获取红包账户
     * @author Liu Yi
     * @date 2019/9/9 13:41
     */
    @Override
    public RedAccountDTO getRedAccountByAccountId(Integer accountId, Integer accountType) {
        if (accountId == null) {
            throw new BadRequestException("账号id不能为空！");
        }
        if (accountType == null) {
            throw new BadRequestException("账号类型不能为空！");
        }
        if (accountType == 2) {
            Example example = new Example(Distributor.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId", accountId);
            Distributor distributor = distributorMapper.selectOneByExample(example);
            if (Objects.isNull(distributor)) {
               return null;
            }
            accountId = distributor.getId();
        }

        List<RedAccountDTO> list = redAccountMapper.getRedAccountByAccountId(accountId, accountType);
        if (list == null || list.size() <= 0) {
            return null;
        }

        return list.get(0);
    }

}
