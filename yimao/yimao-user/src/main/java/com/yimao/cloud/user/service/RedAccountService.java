package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.Distributor;
import com.yimao.cloud.user.po.DistributorOrder;
import com.yimao.cloud.user.po.UserDistributorApply;

import java.util.List;
import java.util.Map;

public interface RedAccountService {
    /**
     * @description   根据新id获取红包账户
     * @author Liu Yi
     * @date 2019/9/9 13:41
     * @param
     * @return com.yimao.cloud.pojo.dto.user.RedAccountDTO
     */
    RedAccountDTO getRedAccountById(Integer id);

    /**
     * @description   根据老id获取红包账户
     * @author Liu Yi
     * @date 2019/9/9 13:41
     * @param
     * @return com.yimao.cloud.pojo.dto.user.RedAccountDTO
     */
    RedAccountDTO getRedAccountByOldId(String oldId);

    /**
     * @description   根据账户id获取红包账户
     * @author Liu Yi
     * @date 2019/9/9 13:41
     * @param accountId 账户id
     * @parama accountType 账户类型
     * @return com.yimao.cloud.pojo.dto.user.RedAccountDTO
     */
    RedAccountDTO getRedAccountByAccountId(Integer accountId,Integer accountType);
}
