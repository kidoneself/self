package com.yimao.cloud.hra.mapper;

import com.yimao.cloud.hra.po.MiniAnswers;

import java.util.List;

public interface MiniAnswersMapper {

    List<MiniAnswers> findAnswersList(Integer evaluateId);
}
