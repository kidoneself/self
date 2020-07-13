package com.yimao.cloud.system.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.system.service.ExportRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 描述：导出记录
 *
 * @Author Zhang Bo
 * @Date 2019/11/25
 */
@Component
@Slf4j
public class ExportRecordProcessor {

    @Resource
    private ExportRecordService exportRecordService;

    /**
     * 导出记录
     */
    @RabbitListener(queues = RabbitConstant.EXPORT_RECORD)
    @RabbitHandler
    public void processor(ExportRecordDTO recordDTO) {
        try {
            exportRecordService.update(recordDTO);
        } catch (Exception e) {
        }
    }
}
