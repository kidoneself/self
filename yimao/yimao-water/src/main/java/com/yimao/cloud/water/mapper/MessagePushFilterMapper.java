package com.yimao.cloud.water.mapper;

import com.yimao.cloud.water.po.MessagePushFilter;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * 描述：消息推送频次过滤配置
 *
 * @Author LiuYi
 * @Date 2019/11/20
 */

public interface MessagePushFilterMapper extends Mapper<MessagePushFilter> {
    MessagePushFilter selectFilter(@Param("province") String province,
                                   @Param("city") String city,
                                   @Param("region") String region,
                                   @Param("deviceModel") String deviceModel,
                                   @Param("faultType") Integer faultType);
}
