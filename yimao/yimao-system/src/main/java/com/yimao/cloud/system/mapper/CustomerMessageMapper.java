package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.CustomerMessageDTO;
import com.yimao.cloud.system.po.CustomerMessage;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author liuhao@yimaokeji.com
 * 2018052018/5/17
 */
public interface CustomerMessageMapper extends Mapper<CustomerMessage> {

    /**
     * 留言消息
     *
     * @param province     省
     * @param city         市
     * @param region       区
     * @param customerName 名称
     * @param mobile       手机号
     * @param joinType     加盟类型
     * @param beginTime    开始时间
     * @param endTime      结束时间
     * @return
     */
    Page<CustomerMessageDTO> listCustomerMessage(@Param("province") String province,
                                                 @Param("city") String city,
                                                 @Param("region") String region,
                                                 @Param("customerName") String customerName,
                                                 @Param("mobile") String mobile,
                                                 @Param("joinType") Integer joinType,
                                                 @Param("beginTime") String beginTime,
                                                 @Param("endTime") String endTime,
                                                 @Param("terminal") Integer terminal);
}
