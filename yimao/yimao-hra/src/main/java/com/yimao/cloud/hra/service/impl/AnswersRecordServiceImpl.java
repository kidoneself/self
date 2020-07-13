package com.yimao.cloud.hra.service.impl;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.hra.mapper.MiniAnswersRecordMapper;
import com.yimao.cloud.hra.po.MiniAnswersRecord;
import com.yimao.cloud.hra.service.AnswersRecordService;
import com.yimao.cloud.pojo.dto.hra.MiniAnswersRecordDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @description: 选项保存 实现类
 * @author: yu chunlei
 * @create: 2018-05-05 10:55:54
 **/
@Service
@Slf4j
public class AnswersRecordServiceImpl implements AnswersRecordService {

    @Resource
    private MiniAnswersRecordMapper miniAnswersRecordMapper;

    @Override
    public MiniAnswersRecordDTO saveOption(Integer evaluateId, Integer choiceId, Integer optionId) {
        log.debug("==============进入saveOption()方法=============");
        MiniAnswersRecord record = new MiniAnswersRecord();
        record.setEvaluateId(evaluateId);
        record.setChoiceId(choiceId);
        record.setOptionId(optionId);
        record.setCreateTime(new Date());
        //用户ID
        int i = miniAnswersRecordMapper.insert(record);
        if (i > 0) {
            log.debug("==========选项保存成功!===========");
            MiniAnswersRecordDTO recordDTO = new MiniAnswersRecordDTO();
            record.convert(recordDTO);
            return recordDTO;
        }
        throw new NotFoundException("选项保存失败");
    }
}
