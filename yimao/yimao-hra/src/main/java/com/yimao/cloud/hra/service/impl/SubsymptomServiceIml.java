package com.yimao.cloud.hra.service.impl;

import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.hra.mapper.MiniSickenResultMapper;
import com.yimao.cloud.hra.mapper.MiniSubsymptomMapper;
import com.yimao.cloud.hra.mapper.MiniSymptomMapper;
import com.yimao.cloud.hra.po.MiniSickenResult;
import com.yimao.cloud.hra.po.MiniSubsymptom;
import com.yimao.cloud.hra.service.SubsymptomService;
import com.yimao.cloud.pojo.dto.hra.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 子症状 实现类
 * @author: yu chunlei
 * @create: 2018-04-28 14:53:27
 **/
@Service
@Slf4j
public class SubsymptomServiceIml implements SubsymptomService {

    @Resource
    private MiniSubsymptomMapper miniSubsymptomMapper;
    @Resource
    private MiniSickenResultMapper miniSickenResultMapper;
    @Resource
    private MiniSymptomMapper miniSymptomMapper;

    @Override
    public List<MiniSubsymptom> findSubsymptomList(Map<String, String> map) {
        log.info("=====SubsymptomApiIml/findSubsymptomList()======");
        String symptomId = map.get("symptomId");
        if (StringUtil.isBlank(symptomId)) {
            log.info("#####症状ID没有值#####" );
            return null;
        }
        List<MiniSubsymptom> subsymptomList = miniSubsymptomMapper.findSubsymptomList(map);
        if (subsymptomList != null && subsymptomList.size() > 0) {
            return subsymptomList;
        }
        log.info("获取该症状下的子症状为空！");
        return null;
    }




    /*public List<HealthySubsymptom> findHotList(Integer flag) {
        try{
            List<HealthySubsymptom> subsymptomList = healthySubsymptomMapper.findHotList(flag);
            if(subsymptomList != null && subsymptomList.size() > 0){
                return subsymptomList;
            }
            log.info("获取热门搜索列表为空！");
            return null;
        }catch(Exception e){
            e.printStackTrace();
            log.error("热门搜索异常：" + e.getMessage(),e);
            throw new ServiceException(e.getMessage(), e);
        }

    }*/

    @Override
    public SymptomAndSubsymDTO findHotList(Integer flag) {
        SymptomAndSubsymDTO dto = new SymptomAndSubsymDTO();
        List<MiniSubsymptomDTO> subsymptomList = miniSubsymptomMapper.findHotList(flag);
        List<MiniSymptomDTO> symptomList = miniSymptomMapper.findSymptomByFlag(flag);
        if (subsymptomList != null && subsymptomList.size() > 0) {
            dto.setSubsymptomList(subsymptomList);
        }

        if (symptomList != null && symptomList.size() > 0) {
            dto.setSymptomList(symptomList);
        }

        return dto;
    }


    @Override
    public SubSymptomDTO findResultBySubsymId(Integer subsymId) {
        log.info("=====进入findResultBySubsymId()方法,subsymId=" + subsymId + "======");
        SubSymptomDTO symptomDto = new SubSymptomDTO();
        List<String> list = new ArrayList<>();
        Integer resultId = null;
        MiniSubsymptom subsymptom = miniSubsymptomMapper.selectByPrimaryKey(subsymId);
        if (subsymptom != null) {
            resultId = subsymptom.getResultId();
            MiniSickenResult result = miniSickenResultMapper.selectByPrimaryKey(resultId);
            MiniSickenResultDTO healthySickenResultDTO = new MiniSickenResultDTO();
            result.convert(healthySickenResultDTO);
            symptomDto.setResult(healthySickenResultDTO);
        } else {
            log.info("====未查询到subsymptom====");
        }

        List<MiniSubsymptom> subsymptomList = miniSubsymptomMapper.findListByResultId(resultId);
        if (subsymptomList != null && subsymptomList.size() > 0) {
            for (MiniSubsymptom subsymptom1 : subsymptomList) {
                list.add(subsymptom1.getSubsymptomName());
            }
            symptomDto.setSubList(list);
        } else {
            log.info("*****subsymptomList为空******");
        }
        return symptomDto;
    }


    @Override
    public MiniSubsymptomDTO getSearchResult(String keyWords) {
        log.info("=====进入getSearchResult()方法=====");
        log.info("=====传入的关键词:" + keyWords + "======");
        Map<String, String> map = new HashMap<String, String>();
        map.put("keyWords", keyWords);
        MiniSubsymptom subsymptom = miniSubsymptomMapper.getSearchResult(map);
        if (subsymptom != null) {
            MiniSubsymptomDTO dto = new MiniSubsymptomDTO();
            subsymptom.convert(dto);
            return dto;
        }
        return null;
    }
}
