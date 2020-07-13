package com.yimao.cloud.hra.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.CopyUtil;
import com.yimao.cloud.base.utils.PropertiesUtils;
import com.yimao.cloud.hra.constant.HraConstant;
import com.yimao.cloud.hra.mapper.MiniChoiceMapper;
import com.yimao.cloud.hra.mapper.MiniEvaluatingMapper;
import com.yimao.cloud.hra.mapper.MiniOptionMapper;
import com.yimao.cloud.hra.mapper.MiniSecondClassifyMapper;
import com.yimao.cloud.hra.po.MiniChoice;
import com.yimao.cloud.hra.po.MiniEvaluating;
import com.yimao.cloud.hra.po.MiniOption;
import com.yimao.cloud.hra.po.MiniSecondClassify;
import com.yimao.cloud.hra.service.HealthOwnerService;
import com.yimao.cloud.pojo.dto.hra.ChoiceOptionDTO;
import com.yimao.cloud.pojo.dto.hra.EvaluatingDTO;
import com.yimao.cloud.pojo.dto.hra.EvaluatingImageDTO;
import com.yimao.cloud.pojo.dto.hra.MiniEvaluatingDTO;
import com.yimao.cloud.pojo.dto.hra.MiniOptionDTO;
import com.yimao.cloud.pojo.dto.hra.MiniSecondClassifyDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 健康评测实现类
 * @author: yu chunlei
 * @create: 2018-05-04 10:29:45
 **/
@Service
@Slf4j
public class HealthOwnerServiceImpl implements HealthOwnerService {

    @Resource
    private MiniEvaluatingMapper miniEvaluatingMapper;
    @Resource
    private MiniSecondClassifyMapper miniSecondClassifyMapper;
    @Resource
    private MiniChoiceMapper miniChoiceMapper;
    @Resource
    private MiniOptionMapper miniOptionMapper;


    @Override
    public EvaluatingDTO getHealthCatory(Integer pageNum, Integer pageSize, Integer pid) {
        log.debug("==============进入getgetHealthCatory()方法=============");
        EvaluatingDTO dto = new EvaluatingDTO();
        if (HraConstant.EVALUATING_LIST.contains(pid + "")) {
            MiniSecondClassify secondClassify = miniSecondClassifyMapper.selectHealthByPid(pid);
            Integer secId = secondClassify.getId();
            PageVO<MiniEvaluatingDTO> pageVO = findEvaluatingList(pageNum, pageSize, secId);
            dto.setClassifyId(secondClassify.getClassifyId());
            dto.setPid(secondClassify.getPid());
            dto.setSecondName(secondClassify.getSecondName());
            dto.setEvaluatingList(pageVO.getResult());
            return dto;
        }
        log.debug("获取pid不正确");
        throw new BadRequestException("获取pid不正确");
    }


    @Override
    public List<EvaluatingDTO> findAllHealthList(Integer pageNum, Integer pageSize) {
        log.debug("==============进入findAllHealthList()方法=============");
        //查询健康自测的 所有分类
        List<MiniSecondClassify> secondClassifyList = miniSecondClassifyMapper.findAllHealthSecond();
        List<MiniSecondClassifyDTO> secondClassifyListDTO = CopyUtil.copyList(secondClassifyList, MiniSecondClassify.class, MiniSecondClassifyDTO.class);
        List<EvaluatingDTO> evaList = new ArrayList<>();
        //判断
        if (CollectionUtil.isNotEmpty(secondClassifyList)) {
            for (MiniSecondClassifyDTO classify : secondClassifyListDTO) {
                EvaluatingDTO dto = new EvaluatingDTO();
                dto.setId(classify.getId());
                dto.setPid(classify.getPid());
                dto.setSecondName(classify.getSecondName());
                dto.setClassifyId(classify.getClassifyId());
                if (classify.getPid() == 9) {
                    PageVO<MiniEvaluatingDTO> evaluatingPage = findEvaluatingList(pageNum, pageSize, classify.getId());
                    List<MiniEvaluatingDTO> list = evaluatingPage.getResult();
                    dto.setEvaluatingList(list);
                }
                evaList.add(dto);
            }
            return evaList;
        }
        throw new NotFoundException("查询健康自测的 所有分类为空");
    }

    @Override
    public List<ChoiceOptionDTO> findChoiceList(Integer evaluateId) {
        log.debug("==============进入findChoiceList()方法=============");
        log.debug("获取测评evaluateId=" + evaluateId);
        List<MiniChoice> choiceList = miniChoiceMapper.findChoiceListByEvaId(evaluateId);
        log.debug("========choiceList.size()长度为：" + choiceList.size() + "=========");
        if (CollectionUtil.isEmpty(choiceList)) {
            log.debug("######获取题干列表为空######");
            throw new NotFoundException("获取题干列表为空");
        }

        List<ChoiceOptionDTO> listDto = PropertiesUtils.copyList(ChoiceOptionDTO.class, choiceList);
        log.debug("******查询成功：" + listDto.toString() + "******");
        for (ChoiceOptionDTO choiceOptionDto : listDto) {
            //获取选项
            Integer choiceId = choiceOptionDto.getId();
            List<MiniOption> optionList = miniOptionMapper.selectOption(choiceId);
            List<MiniOptionDTO> optionDTOList = CopyUtil.copyList(optionList, MiniOption.class, MiniOptionDTO.class);
            choiceOptionDto.setOptionList(optionDTOList);
        }

        return listDto;
    }


    public PageVO<MiniEvaluatingDTO> findEvaluatingList(Integer pageNum, Integer pageSize, Integer secId) {
        PageHelper.startPage(pageNum, pageSize);
        Page<MiniEvaluating> evaluatingPage = (Page<MiniEvaluating>) miniEvaluatingMapper.findEvaluatingList(secId);
        PageVO<MiniEvaluatingDTO> pageVO = new PageVO<>(pageNum, evaluatingPage, MiniEvaluating.class, MiniEvaluatingDTO.class);
        return pageVO;
    }

    @Override
    public EvaluatingImageDTO getEvaluateInfo(Map<String, String> map) {
        log.debug("==============进入getEvaluatInfo()方法=============");
        EvaluatingImageDTO evaluating = miniEvaluatingMapper.findImageById(map);
        return evaluating;
    }

}
