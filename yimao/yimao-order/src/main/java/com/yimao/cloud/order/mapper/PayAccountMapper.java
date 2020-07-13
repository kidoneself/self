package com.yimao.cloud.order.mapper;

import com.yimao.cloud.order.po.PayAccount;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface PayAccountMapper extends Mapper<PayAccount> {

    PayAccount selectPayAccount(@Param("companyId") Integer companyId, @Param("platform") Integer platform,
                                @Param("clientType") Integer clientType, @Param("receiveType") Integer receiveType);

    PayAccount selectPayAccountByAppid(@Param("appid") String appid, @Param("platform") Integer platform);
}
