package com.yimao.cloud.system.service.impl;

import com.yimao.cloud.pojo.dto.system.MessageContentDTO;
import com.yimao.cloud.system.mapper.MessageContentMapper;
import com.yimao.cloud.system.po.MessageContent;
import com.yimao.cloud.system.service.MessageContentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/27
 */
@Service
public class MessageContentServiceImpl implements MessageContentService {

   @Resource
   private MessageContentMapper messageContentMapper;

    @Override
    public void messageContentSave(MessageContentDTO recordDTO) {
        MessageContent record = new MessageContent(recordDTO);
        messageContentMapper.insert(record);
    }
}
