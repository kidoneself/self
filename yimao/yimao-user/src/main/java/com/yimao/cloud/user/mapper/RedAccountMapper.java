package com.yimao.cloud.user.mapper;

import com.yimao.cloud.pojo.dto.user.RedAccountDTO;
import com.yimao.cloud.user.po.RedAccount;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description   红包账户
 * @author Liu Yi
 * @date 2019/9/9 13:31
 */
public interface RedAccountMapper extends Mapper<RedAccount> {

    List<RedAccountDTO> getRedAccountByAccountId(@Param("accountId") Integer accountId, @Param("accountType") Integer accountType);
}
