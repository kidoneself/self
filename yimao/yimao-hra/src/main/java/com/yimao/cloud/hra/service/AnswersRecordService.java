package com.yimao.cloud.hra.service;

import com.yimao.cloud.pojo.dto.hra.MiniAnswersRecordDTO;

/**
 * @author ycl
 * @Create: 2019/4/22 14:21
*/
public interface AnswersRecordService {

    /**
     * @Description: 选择题保存
     * @author ycl
     * @param evaluateId
     * @param choiceId
     * @param optionId
     * @Return: com.yimao.cloud.pojo.dto.hra.HealthyAnswersRecordDTO
     * @Create: 2019/4/22 14:21
    */
    MiniAnswersRecordDTO saveOption(Integer evaluateId, Integer choiceId, Integer optionId);

}
