package com.yimao.cloud.hra.service.impl;

import com.yimao.cloud.base.enums.ClassifyEnum;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.CopyUtil;
import com.yimao.cloud.hra.mapper.MiniSecondClassifyMapper;
import com.yimao.cloud.hra.mapper.MiniSymptomMapper;
import com.yimao.cloud.hra.po.MiniSecondClassify;
import com.yimao.cloud.hra.po.MiniSymptom;
import com.yimao.cloud.hra.service.SecondClassifyService;
import com.yimao.cloud.hra.service.SymptomService;
import com.yimao.cloud.pojo.dto.hra.MiniSecondClassifyDTO;
import com.yimao.cloud.pojo.dto.hra.MiniSymptomDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 所有二级症状类型 接口的实现类
 * @author: yu chunlei
 * @create: 2018-04-27 17:44:05
 **/
@Service
@Slf4j
public class SecondClassifyServiceImpl implements SecondClassifyService {

    @Resource
    private MiniSecondClassifyMapper miniSecondClassifyMapper;
    @Resource
    private SymptomService symptomService;

    @Override
    public MiniSecondClassifyDTO getAllClassify(Integer pageNum, Integer pageSize, Integer pid) {
        MiniSecondClassify secondClassify = null;
        PageVO<MiniSymptom> pageVO = null;

        if (pid == ClassifyEnum.CLASSIFY_ZERO.value) {
            log.debug("=====pid=0:进入=====");
            secondClassify = miniSecondClassifyMapper.selectByPid(pid);
            pageVO = symptomService.findAllList(pageNum, pageSize);
        } else if (pid == ClassifyEnum.CLASSIFY_ONE.value) {
            secondClassify = miniSecondClassifyMapper.selectByPid(pid);
            pageVO = symptomService.findSymptomList(pageNum, pageSize, pid);
        } else if (pid == ClassifyEnum.CLASSIFY_TWO.value) {
            secondClassify = miniSecondClassifyMapper.selectByPid(pid);
            pageVO = symptomService.findSymptomList(pageNum, pageSize, pid);
        } else if (pid == ClassifyEnum.CLASSIFY_THREE.value) {
            secondClassify = miniSecondClassifyMapper.selectByPid(pid);
            pageVO = symptomService.findSymptomList(pageNum, pageSize, pid);
        } else if (pid == ClassifyEnum.CLASSIFY_FOUR.value) {
            secondClassify = miniSecondClassifyMapper.selectByPid(pid);
            pageVO = symptomService.findSymptomList(pageNum, pageSize, pid);
        } else if (pid == ClassifyEnum.CLASSIFY_FIVE.value) {
            secondClassify = miniSecondClassifyMapper.selectByPid(pid);
            pageVO = symptomService.findSymptomList(pageNum, pageSize, pid);
        } else if (pid == ClassifyEnum.CLASSIFY_SIX.value) {
            secondClassify = miniSecondClassifyMapper.selectByPid(pid);
            pageVO = symptomService.findSymptomList(pageNum, pageSize, pid);
        } else if (pid == ClassifyEnum.CLASSIFY_SEVEN.value) {
            secondClassify = miniSecondClassifyMapper.selectByPid(pid);
            pageVO = symptomService.findSymptomList(pageNum, pageSize, pid);
        } else if (pid == ClassifyEnum.CLASSIFY_EIGHT.value) {
            secondClassify = miniSecondClassifyMapper.selectByPid(pid);
            pageVO = symptomService.findSymptomList(pageNum, pageSize, pid);
        } else {
            log.debug("#####获取pid数据有误#####");
        }

        if (secondClassify == null) {
            log.debug("#####获取类型为全部为空#####");
            return null;
        }

        MiniSecondClassifyDTO dto = new MiniSecondClassifyDTO();
        dto.setId(secondClassify.getId());
        dto.setPid(secondClassify.getPid());
        dto.setClassifyId(secondClassify.getClassifyId());
        dto.setSecondName(secondClassify.getSecondName());
        dto.setCreateTime(secondClassify.getCreateTime());
        List<MiniSymptom> result = pageVO.getResult();
        List<MiniSymptomDTO> list = new ArrayList<>();
        getResult(result, list);
        dto.setSymptomList(list);

        return dto;
    }

    static void getResult(List<MiniSymptom> result, List<MiniSymptomDTO> list) {
        if (CollectionUtil.isNotEmpty(result)) {
            MiniSymptomDTO dto1;
            for (MiniSymptom po : result) {
                dto1 = new MiniSymptomDTO();
                po.convert(dto1);
                list.add(dto1);
            }
        }
    }


    @Override
    public List<MiniSecondClassifyDTO> findAllSymptom() {
        List<MiniSecondClassifyDTO> list;
        List<MiniSecondClassify> secondClassifyList = miniSecondClassifyMapper.findAllSecond();
        if (CollectionUtil.isNotEmpty(secondClassifyList)) {
            list = CopyUtil.copyList(secondClassifyList, MiniSecondClassify.class, MiniSecondClassifyDTO.class);
            return list;
        }
        return null;
    }

    @Override
    public List<MiniSecondClassify> findAllHealthSecond() {
        List<MiniSecondClassify> secondClassifyList = miniSecondClassifyMapper.findAllHealthSecond();
        if (CollectionUtil.isNotEmpty(secondClassifyList)) {
            return secondClassifyList;
        }
        return null;
    }


}
