package com.yimao.cloud.hra.service.impl;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.PropertiesUtils;
import com.yimao.cloud.hra.mapper.MiniSickenResultMapper;
import com.yimao.cloud.hra.mapper.MiniSubsymptomMapper;
import com.yimao.cloud.hra.po.MiniSickenResult;
import com.yimao.cloud.hra.po.MiniSubsymptom;
import com.yimao.cloud.hra.service.SickenResultService;
import com.yimao.cloud.pojo.dto.hra.MiniSubsymptomDTO;
import com.yimao.cloud.pojo.dto.hra.SickenResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 患病结果详细信息 实现类
 * @author: yu chunlei
 * @create: 2018-05-07 10:46:09
 **/
@Service
@Slf4j
public class SickenResultServiceImpl implements SickenResultService {

    @Resource
    private MiniSickenResultMapper miniSickenResultMapper;
    @Resource
    private MiniSubsymptomMapper miniSubsymptomMapper;

    @Override
    public SickenResultDTO getResultDetail(Integer resultId) {
        log.info("=====获取患病结果resultId=" + resultId + "=====");
        MiniSickenResult sickenResult = miniSickenResultMapper.selectByPrimaryKey(resultId);
        if (sickenResult != null) {
            SickenResultDTO resultDto = PropertiesUtils.copy(SickenResultDTO.class, sickenResult);
            List<MiniSubsymptom> subsymptomList = miniSubsymptomMapper.findListByResultId(resultId);
            List<MiniSubsymptomDTO> list = new ArrayList<>();
            getSubsymptomList(subsymptomList, list);
            resultDto.setSubsymptomList(list);
            return resultDto;
        }
        log.debug("=====结果为空======");
        return null;
    }

    static void getSubsymptomList(List<MiniSubsymptom> subsymptomList, List<MiniSubsymptomDTO> list) {
        if (CollectionUtil.isNotEmpty(subsymptomList)) {
            MiniSubsymptomDTO dto;
            for (MiniSubsymptom po : subsymptomList) {
                dto = new MiniSubsymptomDTO();
                po.convert(dto);
                list.add(dto);
            }
        }
    }
}
