package com.yimao.cloud.hra.service;


import com.yimao.cloud.pojo.dto.hra.AnswersDTO;

import java.util.List;

/**
 * @author ycl
 */
public interface AnswersService {

    /**
     * @param evaluateId
     * @param integerList
     * @Description: 根据用户的选择 返给用户相应的答案
     * @author ycl
     * @Return: com.yimao.cloud.pojo.dto.hra.AnswersDTO
     * @Create: 2019/4/22 14:14
     */
    AnswersDTO getOptionAnswers(Integer evaluateId, List<Integer> integerList);
}
