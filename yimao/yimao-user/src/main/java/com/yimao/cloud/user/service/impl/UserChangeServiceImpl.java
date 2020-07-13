package com.yimao.cloud.user.service.impl;

import com.yimao.cloud.pojo.dto.user.UserChangeRecordDTO;
import com.yimao.cloud.user.mapper.UserChangeMapper;
import com.yimao.cloud.user.po.UserChangeRecord;
import com.yimao.cloud.user.service.UserChangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/6/29.
 */
@Service
@Slf4j
public class UserChangeServiceImpl implements UserChangeService {

    @Resource
    private UserChangeMapper userChangeMapper;

    /**
     * 保存用户变化记录
     *
     * @param userId 用户e家号
     * @param type   变化类型
     * @param time   变化时间
     * @param remark 说明
     */
    @Override
    public void save(Integer userId, Integer type, Integer userType, String phone, Date time, String remark, Integer source) {
        UserChangeRecord record = new UserChangeRecord();
        record.setOrigUserId(userId);
        record.setOrigUserType(userType);
        record.setOrigPhone(phone);
        //变化类型（事件）1-创建账号 2-升级分享 3-升级分销用户 4-绑定手机 5-绑定经销商 6-升级 7-首次关注公众号 8-首次登陆小程序 9-取消关注公众号 10-转让
        record.setType(type);
        record.setTime(time);
        record.setRemark(remark);
        record.setSource(source);//来源
        userChangeMapper.insertSelective(record);
    }

}
