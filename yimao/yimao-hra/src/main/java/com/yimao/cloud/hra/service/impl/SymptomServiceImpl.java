package com.yimao.cloud.hra.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.PropertiesUtils;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.hra.mapper.MiniSecondClassifyMapper;
import com.yimao.cloud.hra.mapper.MiniSickenResultMapper;
import com.yimao.cloud.hra.mapper.MiniSicknessMapper;
import com.yimao.cloud.hra.mapper.MiniSubsymptomMapper;
import com.yimao.cloud.hra.mapper.MiniSymptomMapper;
import com.yimao.cloud.hra.po.MiniSecondClassify;
import com.yimao.cloud.hra.po.MiniSickenResult;
import com.yimao.cloud.hra.po.MiniSickness;
import com.yimao.cloud.hra.po.MiniSubsymptom;
import com.yimao.cloud.hra.po.MiniSymptom;
import com.yimao.cloud.hra.service.SymptomService;
import com.yimao.cloud.pojo.dto.hra.*;
import com.yimao.cloud.pojo.dto.hra.MiniSickenResultDTO;
import com.yimao.cloud.pojo.dto.hra.MiniSubsymptomDTO;
import com.yimao.cloud.pojo.dto.hra.MiniSymptomDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 身体部位下所有症状 实现类
 * @author: yu chunlei
 * @create: 2018-04-28 11:44:01
 **/
@Service
@Slf4j
public class SymptomServiceImpl implements SymptomService {

    @Resource
    private MiniSymptomMapper symptomMapper;
    @Resource
    private MiniSubsymptomMapper subsymptomMapper;
    @Resource
    private MiniSickenResultMapper sickenResultMapper;
    @Resource
    private MiniSicknessMapper sicknessMapper;
    @Resource
    private MiniSecondClassifyMapper secondClassifyMapper;

    @Override
    public List<MiniSymptom> findBodySymptom(Map<String, String> map) {
        String secondId = map.get("secondId");
        log.info("#####二级分类ID：#####" + secondId);
        if (secondId == null) {
            log.info("获取二级分类ID为空！");
            return null;
        }
        String pageNum = map.get("pageNum");
        String pageSize = map.get("pageSize");
        PageHelper.startPage(Integer.parseInt(pageNum), Integer.parseInt(pageSize));
        Page<MiniSymptom> symptomPage = (Page<MiniSymptom>) symptomMapper.findBodySymptom(map);
        //List<HealthySymptom> symptomList = symptomMapper.findBodySymptom(map);
        return symptomPage.getResult();
    }

    //封装DTO(子症状和患病结果)
    @Override
    public SubSymptomResultDTO getSubsymptomAndResult(Map<String, String> map) {
        log.debug("######进入子症状和患病结果 方法######");
        String symptomId = map.get("symptomId");
        if (symptomId == null) {
            log.debug("症状ID为空！");
            return null;
        }
        log.debug("#####症状ID：#####" + symptomId);

        SubSymptomResultDTO dto = new SubSymptomResultDTO();
        MiniSymptom symptom = symptomMapper.selectByPrimaryKey(Integer.valueOf(symptomId));
        if (symptom != null) {
            dto.setSymptomName(symptom.getSymptomName());
            dto.setSymptomIntro(symptom.getSymptomIntro());
            dto.setSymptomDetail(symptom.getSymptomDetail());
        }

        log.debug("====查询子症状列表开始======");
        log.debug("########symptomId#######" + Long.valueOf(symptomId));
        List<MiniSubsymptom> subsymptomList = subsymptomMapper.findListBySymId(Long.valueOf(symptomId));
        List<SubSymptomDTO> subSymptomDTOList = PropertiesUtils.copyList(SubSymptomDTO.class, subsymptomList);
        if (subSymptomDTOList != null && subSymptomDTOList.size() > 0) {
            for (SubSymptomDTO subSymptomDto : subSymptomDTOList) {
                Integer dtoSymptomId = subSymptomDto.getSymptomId();
                List<MiniSickenResult> sickenResultList = sickenResultMapper.findListBySymptomId(dtoSymptomId);
                List<MiniSickenResultDTO> list = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(sickenResultList)) {
                    MiniSickenResultDTO dto1;
                    for (MiniSickenResult po : sickenResultList) {
                        dto1 = new MiniSickenResultDTO();
                        po.convert(dto1);
                        list.add(dto1);
                    }
                }
                subSymptomDto.setSickenResultList(list);
            }
        }

        dto.setSymptomDtoList(subSymptomDTOList);
        //处理患病结果数据
        List<MiniSickenResult> sickenResultList = sickenResultMapper.findResultList(map);
        List<SickenResultDTO> resultDtoList = PropertiesUtils.copyList(SickenResultDTO.class, sickenResultList);
        if (resultDtoList != null && resultDtoList.size() > 0) {
            for (SickenResultDTO resultDto : resultDtoList) {
                Integer resultId = resultDto.getId();
                List<MiniSubsymptom> subsymptoms = subsymptomMapper.findListByResultId(resultId);
                List<MiniSubsymptomDTO> list = new ArrayList<>();
                SickenResultServiceImpl.getSubsymptomList(subsymptoms, list);
                resultDto.setSubsymptomList(list);
            }
        }
        dto.setResultDtoList(resultDtoList);
        return dto;
    }


    @Override
    public PageVO<MiniSymptom> findSymptomList(Integer pageNum, Integer pageSize, Integer pid) {
        Map<String, String> map = new HashMap<>();
        map.put("secondId", pid + "");
        PageHelper.startPage(pageNum, pageSize);
        Page<MiniSymptom> symptomPage = (Page<MiniSymptom>) symptomMapper.findBodySymptom(map);
        return new PageVO<>(pageNum, symptomPage);
    }


    @Override
    public PageVO<MiniSymptom> findAllList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<MiniSymptom> symptomPage = (Page<MiniSymptom>) symptomMapper.selectAll();
        //Page<HealthySymptom> symptomPage = (Page<HealthySymptom>) symptomMapper.findAllSymptom();
        log.info("********symptomPage.getTotal()=" + symptomPage.getTotal() + "********");
        return new PageVO<>(pageNum, symptomPage);
    }


    @Override
    public PageVO<MiniSymptomDTO> findBodyPartSymptoms(Integer pageNum, Integer pageSize, Integer secondId) {
        Page<MiniSymptom> bodySymptom = null;

        if (secondId == 0) {
            PageHelper.startPage(pageNum, pageSize);
            bodySymptom = (Page<MiniSymptom>) symptomMapper.selectAll();
        } else {
            Map<String, String> map = new HashMap<String, String>();
            map.put("secondId", secondId + "");
            PageHelper.startPage(pageNum, pageSize);
            bodySymptom = (Page<MiniSymptom>) symptomMapper.findBodySymptom(map);
            log.debug("********bodySymptom.getTotal()=" + bodySymptom.getTotal() + "********");
        }
        PageVO<MiniSymptomDTO> pageVO = new PageVO<>(pageNum, bodySymptom, MiniSymptom.class, MiniSymptomDTO.class);
        return pageVO;
    }


    @Override
    public SymptomInfoDTO getZhengZhuangInfo(Integer symptomId) {
        log.info("******传入参数,symptomId = " + symptomId);
        SymptomInfoDTO infoDto = new SymptomInfoDTO();
        MiniSymptom symptom = symptomMapper.findSymptomName(symptomId);
        infoDto.setSymptomId(symptom.getId());
        infoDto.setSymptomName(symptom.getSymptomName());

        if (StringUtil.isNotEmpty(symptom.getZhengzhuangIds())) {
            String zhengzhuangIds = symptom.getZhengzhuangIds();
            //症状ID数组
            String[] zhengzhuangIdsSpit = zhengzhuangIds.split(",");
            List<MiniSymptomDTO> symptomDtoList = new ArrayList<>();
            for (String zhengzhuangId : zhengzhuangIdsSpit) {
                MiniSymptom stom = symptomMapper.findZhengZhuangName(Integer.valueOf(zhengzhuangId));
                MiniSymptomDTO symptomDto = new MiniSymptomDTO();
                if (stom != null) {
                    symptomDto.setId(stom.getId());
                    symptomDto.setSymptomName(stom.getSymptomName());
                    symptomDtoList.add(symptomDto);
                }
            }
            infoDto.setSymptomDtoList(symptomDtoList);
        }

        if (StringUtil.isNotEmpty(symptom.getJibingIds())) {
            String jibingIds = symptom.getJibingIds();
            //疾病ID数组
            String[] jibingIdsSpit = jibingIds.split(",");
            //疾病处理
            List<MiniSicknessDTO> sicknessDtoList = new ArrayList<>();
            for (String jibingId : jibingIdsSpit) {
                MiniSickness sickness = sicknessMapper.selectSickness(Integer.valueOf(jibingId));
                MiniSicknessDTO sicknessDto = new MiniSicknessDTO();
                sicknessDto.setId(sickness.getId());
                sicknessDto.setSickName(sickness.getSickName());
                sicknessDtoList.add(sicknessDto);
            }
            infoDto.setSicknessDtoList(sicknessDtoList);
        }

        return infoDto;
    }


    @Override
    public SymptomDetailDTO getZhengZhuangDetail(Integer symptomId) {
        log.info("******进入getZhengZhuangDetail()方法,传入参数:symptomId = " + symptomId + "*******");
        SymptomDetailDTO detailDto = new SymptomDetailDTO();
        MiniSymptom symptom = symptomMapper.findSymptomDetail(symptomId);
        if (symptom != null) {
            detailDto.setId(symptom.getId());
            detailDto.setSymptomName(symptom.getSymptomName());
            detailDto.setSymptomIntro(symptom.getSymptomIntro());
            detailDto.setSymptomDetail(symptom.getSymptomDetail());
            return detailDto;
        }
        return null;
    }


    @Override
    public List<ClassfySymptomDTO> getClassNameAndHotSymptom() {
        log.info("########进入getClassNameAndHotSymptom()########");
        List<ClassfySymptomDTO> dtoList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("classifyId", "2");
        List<MiniSecondClassify> secondClassifyList = secondClassifyMapper.selectClassifyByMap(map);

        if (CollectionUtil.isEmpty(secondClassifyList)) {
            log.info("######获取身体部位列表为空#######");
            return null;
        }

        for (MiniSecondClassify secondClassify : secondClassifyList) {

            ClassfySymptomDTO classfySymptomDto = new ClassfySymptomDTO();
            classfySymptomDto.setClassfyId(secondClassify.getPid());
            classfySymptomDto.setClassfyName(secondClassify.getSecondName());
            Integer classifyId = secondClassify.getPid();
            log.info("######二级分类id==" + classifyId + "#######");
            map.put("classifyId", classifyId + "");
            List<MiniSymptom> symptomList = symptomMapper.findSymptomListBySecondId(classifyId);
            List<MiniSymptomDTO> list = new ArrayList<>();
            SecondClassifyServiceImpl.getResult(symptomList, list);
            classfySymptomDto.setSymptomList(list);
            dtoList.add(classfySymptomDto);
        }
        return dtoList;
    }

}
