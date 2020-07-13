package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.MessageRecordDTO;

import java.util.List;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/27
 */
public interface MessageRecordService {
    List<MessageRecordDTO> messageRecordListByOrderId(Long orderId, Integer type);

    void messageRecordSave(MessageRecordDTO recordDTO);
}
