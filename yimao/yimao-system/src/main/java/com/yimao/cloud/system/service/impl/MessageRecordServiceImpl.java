package com.yimao.cloud.system.service.impl;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.system.MessageRecordDTO;
import com.yimao.cloud.system.mapper.MessageRecordMapper;
import com.yimao.cloud.system.po.MessageRecord;
import com.yimao.cloud.system.service.MessageRecordService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/27
 */
@Service
public class MessageRecordServiceImpl implements MessageRecordService {

    @Resource
    private MessageRecordMapper messageRecordMapper;

    @Override
    public List<MessageRecordDTO> messageRecordListByOrderId(Long orderId, Integer type) {
        // 查询数据库,根据是否有数据判断信息是否已经发送成功
        Example example = new Example(MessageRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        if (Objects.nonNull(type)) {
            criteria.andEqualTo("type", type);
        }
        List<MessageRecord> messageRecords = messageRecordMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(messageRecords)) {
            List<MessageRecordDTO> recordList = new ArrayList<>();
            MessageRecordDTO recordDTO;
            for (MessageRecord dto : messageRecords) {
                recordDTO = new MessageRecordDTO();
                dto.convert(recordDTO);
                recordList.add(recordDTO);
            }
            return recordList;
        }
        return null;
    }

    @Override
    public void messageRecordSave(MessageRecordDTO recordDTO) {
        MessageRecord record = new MessageRecord(recordDTO);
        messageRecordMapper.insert(record);
    }
}
