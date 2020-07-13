package com.yimao.cloud.user.service;

import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/6/29.
 */
public interface UserChangeService {
    /**
     * 保存用户信息变更记录
     * @param userId 用户e家号
     * @param type 变化类型(事件)
     * @param phone 手机号
     * @param time 变更事件
     * @param remark 备注
     * @param source 来源
     */
    void save(Integer userId, Integer type,Integer userType, String phone, Date time, String remark,Integer source);

}
