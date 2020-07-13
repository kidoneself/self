package com.yimao.cloud.hra.service.impl;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.JsonUtil;
import com.yimao.cloud.hra.constant.HraConstant;
import com.yimao.cloud.hra.mapper.MiniAnswersMapper;
import com.yimao.cloud.hra.mapper.MiniEvaluatingMapper;
import com.yimao.cloud.hra.po.MiniAnswers;
import com.yimao.cloud.hra.po.MiniEvaluating;
import com.yimao.cloud.hra.service.AnswersService;
import com.yimao.cloud.pojo.dto.hra.AnswersDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @description: 获取答案 实现类
 * @author: yu chunlei
 * @create: 2018-05-05 14:05:20
 **/
@Service
@Slf4j
public class AnswersServiceImpl implements AnswersService {

    @Resource
    private MiniAnswersMapper miniAnswersMapper;
    @Resource
    private MiniEvaluatingMapper miniEvaluatingMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
    public AnswersDTO getOptionAnswers(Integer evaluateId, List<Integer> integerList) {
        int score01 = 0;
        int score02 = 0;
        int score03 = 0;
        int score04 = 0;
        int score05 = 0;
        int score06 = 0;
        int total = 0;

        /*if(CollectionUtil.isNotEmpty(options)){
            for(Integer num : options){
                if (num.equals(HraConstant.OPTION_A)) {
                    score01 = 10;
                    total += score01;
                }
                if (num.equals(HraConstant.OPTION_B)) {
                    score02 = 9;
                    total += score02;
                }
                if (num.equals(HraConstant.OPTION_C)) {
                    score03 = 8;
                    total += score03;
                }
                if (num.equals(HraConstant.OPTION_D)) {
                    score04 = 7;
                    total += score04;
                }
                if (num.equals(HraConstant.OPTION_E)) {
                    score05 = 6;
                    total += score05;
                }
                if (num.equals(HraConstant.OPTION_F)) {
                    score06 = 5;
                    total += score06;
                }
            }
        }*/
        if (CollectionUtil.isNotEmpty(integerList)) {
            for (Integer num : integerList) {
                if (num == HraConstant.OPTION_A) {
                    score01 = 10;
                    total += score01;
                }
                if (num == HraConstant.OPTION_B) {
                    score02 = 9;
                    total += score02;
                }
                if (num == HraConstant.OPTION_C) {
                    score03 = 8;
                    total += score03;
                }
                if (num == HraConstant.OPTION_D) {
                    score04 = 7;
                    total += score04;
                }
                if (num == HraConstant.OPTION_E) {
                    score05 = 6;
                    total += score05;
                }
                if (num == HraConstant.OPTION_F) {
                    score06 = 5;
                    total += score06;
                }
            }

        }

        //分数处理
        String resultScore = getResultScore(total, integerList);
        //根据评测ID 查询答案列表
        AnswersDTO dto = new AnswersDTO();
        dto.setReturnScore(Integer.valueOf(resultScore));
        List<MiniAnswers> answersList = miniAnswersMapper.findAnswersList(evaluateId);
        if (CollectionUtil.isEmpty(answersList)) {
            throw new NotFoundException("答案列表为空");
        }

        for (MiniAnswers answers : answersList) {
            if (total >= answers.getScore1() && total <= answers.getScore2()) {
                dto.setTitle(answers.getAnswerTitle());
                dto.setContent(answers.getContent());
                dto.setResultScore(answers.getResultScore());
                Double percentage = getPercentage(answers.getResultScore().intValue());
                dto.setPercentage(percentage.toString());
            }
        }
        //参加人数
        MiniEvaluating evaluating = miniEvaluatingMapper.selectByPrimaryKey(evaluateId);
        Integer joinNumber = evaluating.getJoinNumber();
        joinNumber += 3;
        Map<String, String> map = new HashMap<>(16);
        map.put("evaluateId", evaluateId + "");
        map.put("joinNumber", joinNumber + "");
        int i = miniEvaluatingMapper.updateJoinNumber(map);
        if (i == 0) {
            throw new NotFoundException("获取选项答案失败");
        }
        log.debug("dto" + JsonUtil.objectToJson(dto));
        return dto;

    }


    public static Double getPercentage(Integer totle) {
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        Random r = new Random();
        double nextDouble = r.nextDouble();

        log.debug("随机数：" + dcmFmt.format((nextDouble * 3) + 1));

        double doubleValue = totle.doubleValue();
        Double aDouble = doubleValue;
        String str = dcmFmt.format((nextDouble * 3) + 1);
        Double dou = Double.valueOf(str);
        Double dd = aDouble - dou;
        DecimalFormat dcm = new DecimalFormat("0.0");
        log.debug("dcm.format(dd)==" + dcm.format(dd));
        double v = Double.parseDouble(dcm.format(dd));
        return v;
    }


    public static String getResultScore(Integer score, List<Integer> integerList) {
        int num = (integerList.size()) * 10;
        int score1 = score;
        double d = (double) score1 / num;
        double i = d * 100;
        DecimalFormat dcmFmt = new DecimalFormat("0");
        log.info("i====" + dcmFmt.format(i));
        return dcmFmt.format(i);
    }


}
