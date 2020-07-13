package com.yimao.cloud.system.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.system.mapper.RequestPathRecordMapper;
import com.yimao.cloud.system.po.RequestPathRecord;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 请求接口路径日志
 *
 * @author Zhang Bo
 * @date 2019/10/22
 */
@Component
public class RequestPathRecordProcessor {

    @Resource
    private RequestPathRecordMapper requestPathRecordMapper;

    /**
     * 在网关层获取到path直接进入到此队列
     *
     * @param path 请求接口地址
     */
    @RabbitListener(queues = RabbitConstant.REQUEST_PATH_RECORD)
    @RabbitHandler
    public void processor(String path) {
        try {
            if (StringUtil.isNotEmpty(path)) {
                RequestPathRecord record = new RequestPathRecord();
                record.setPath(path);
                int count = requestPathRecordMapper.selectCount(record);
                if (count < 1) {
                    requestPathRecordMapper.insert(record);
                }
            }
        } catch (Exception e) {
        }
    }

}
