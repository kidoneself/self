package com.yimao.cloud.hra.service.impl;


import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.hra.mapper.MiniSicknessMapper;
import com.yimao.cloud.hra.mapper.MiniSymptomMapper;
import com.yimao.cloud.hra.po.MiniSickness;
import com.yimao.cloud.hra.po.MiniSymptom;
import com.yimao.cloud.hra.service.SicknessService;
import com.yimao.cloud.pojo.dto.hra.MiniSicknessDTO;
import com.yimao.cloud.pojo.dto.hra.SicknessResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description: 疾病结果 实现类
 * @author: yu chunlei
 * @create: 2018-05-23 16:23:27
 **/
@Service
@Slf4j
public class SicknessServiceImpl implements SicknessService {

    @Resource
    private MiniSicknessMapper miniSicknessMapper;
    @Resource
    private MiniSymptomMapper miniSymptomMapper;

    @Override
    public MiniSicknessDTO getSicknessById(Integer sicknessId) {
        log.debug("====进入getSicknessById()方法=====");
        MiniSickness sickness = miniSicknessMapper.selectByPrimaryKey(sicknessId);
        if (sickness != null) {
            MiniSicknessDTO dto = new MiniSicknessDTO();
            sickness.convert(dto);
            return dto;
        }
        return null;
    }

    @Override
    public SicknessResultDTO getSicknessBySymptomId(Integer symptomId, String[] symptomIds) {
        log.debug("=====传入参数:symptomId=" + symptomId + "=======");
        SicknessResultDTO resultDto = new SicknessResultDTO();
        List<MiniSicknessDTO> dtoList = new ArrayList<>();

        MiniSymptom symptom = miniSymptomMapper.selectByPrimaryKey(symptomId);
        String jibing = symptom.getJibingIds();
        String[] jibings = jibing.split(",");
        List<String> list01 = Arrays.asList(jibings);
        List arrList = new ArrayList(list01);

        if (symptomIds.length > 1) {
            List<List<String>> list02 = new ArrayList<>();
            //选择多个情况
            for (String symId : symptomIds) {
                log.debug("*****遍历获取传入的symId=" + symId + "********");
                MiniSymptom symp = miniSymptomMapper.selectByPrimaryKey(Integer.valueOf(symId));
                String jbing = symp.getJibingIds();
                String[] jbings = jbing.split(",");
                List<String> list = Arrays.asList(jbings);
                List aList = new ArrayList(list);
                list02.add(aList);
            }

            list02.add(arrList);
            Set<String> intersection = getIntersection(list02);
            getSickness(dtoList, intersection);
            resultDto.setSickDtoList(dtoList);
            return resultDto;
        } else {
            //选择一个的情况

            List<List<String>> list = new ArrayList<>();
            MiniSymptom miniSymptom = miniSymptomMapper.selectByPrimaryKey(Integer.valueOf(symptomIds[0]));
            if(Objects.isNull(miniSymptom)){
                throw new YimaoException("miniSymptom为空");
            }
            String str = miniSymptom.getJibingIds();
            String[] ids = str.split(",");
            List<String> asList = Arrays.asList(ids);
            List bList = new ArrayList(asList);
            list.add(bList);
            list.add(arrList);
            Set<String> intersection = getIntersection(list);
            getSickness(dtoList, intersection);
            resultDto.setSickDtoList(dtoList);
            return resultDto;
        }
    }

    private void getSickness(List<MiniSicknessDTO> dtoList, Set<String> intersection) {
        for (String id : intersection) {
            MiniSickness sickness = miniSicknessMapper.selectByPrimaryKey(Integer.valueOf(id));
            MiniSicknessDTO sicknessDto = new MiniSicknessDTO();
            sicknessDto.setId(sickness.getId());
            sicknessDto.setSickName(sickness.getSickName());
            dtoList.add(sicknessDto);
        }
    }


    private Set<String> getIntersection(List<List<String>> list) {
        Set<String> set = new HashSet<String>();
        int size = list.size();
        if (size > 1) {
            //取集合中的交集
            for (int i = 0; i < size; i++) {
                int j = i + 1;
                if (j < size) {
                    list.get(0).retainAll(list.get(j));
                    if (i == size - 2) {
                        List<String> resultList = list.get(0);
                        for (String name : resultList) {
                            set.add(name);
                        }
                    }
                }
            }
        } else {
            //只有一个集合则不取交集
            for (List<String> list2 : list) {
                for (String name : list2) {
                    set.add(name);
                }
            }
        }
        return set;
    }
}
