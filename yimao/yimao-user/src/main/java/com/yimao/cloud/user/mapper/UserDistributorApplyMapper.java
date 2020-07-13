package com.yimao.cloud.user.mapper;

import com.yimao.cloud.user.po.UserDistributorApply;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description   申请注册经销商
 * @author Liu Yi
 * @date 2019/8/30 15:59
 * @param
 * @return
 */
public interface UserDistributorApplyMapper extends Mapper<UserDistributorApply> {

    /**
     * 根据经销商订单id查询经销商申请信息(创建合同调用)
     * @param orderId
     * @return
     */
    UserDistributorApply getByOrderId(@Param("orderId") Long orderId);
}
