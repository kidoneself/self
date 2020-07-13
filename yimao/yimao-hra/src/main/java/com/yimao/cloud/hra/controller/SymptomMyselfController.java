package com.yimao.cloud.hra.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.hra.service.SecondClassifyService;
import com.yimao.cloud.hra.service.SickenResultService;
import com.yimao.cloud.hra.service.SicknessService;
import com.yimao.cloud.hra.service.SubsymptomService;
import com.yimao.cloud.hra.service.SymptomService;
import com.yimao.cloud.pojo.dto.hra.ClassfySymptomDTO;
import com.yimao.cloud.pojo.dto.hra.MiniSecondClassifyDTO;
import com.yimao.cloud.pojo.dto.hra.MiniSicknessDTO;
import com.yimao.cloud.pojo.dto.hra.MiniSubsymptomDTO;
import com.yimao.cloud.pojo.dto.hra.MiniSymptomDTO;
import com.yimao.cloud.pojo.dto.hra.SickenResultDTO;
import com.yimao.cloud.pojo.dto.hra.SicknessResultDTO;
import com.yimao.cloud.pojo.dto.hra.SubSymptomDTO;
import com.yimao.cloud.pojo.dto.hra.SubSymptomResultDTO;
import com.yimao.cloud.pojo.dto.hra.SymptomAndSubsymDTO;
import com.yimao.cloud.pojo.dto.hra.SymptomDetailDTO;
import com.yimao.cloud.pojo.dto.hra.SymptomInfoDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 二级分类
 * @author: yu chunlei
 * @create: 2018-04-27 17:35:41
 **/
@RestController
@Slf4j
@Api(tags = "SymptomMyselfController")
public class SymptomMyselfController {

    @Resource
    private SecondClassifyService secondClassifyService;
    @Resource
    private SymptomService symptomService;
    @Resource
    private SubsymptomService subsymptomService;
    @Resource
    private SickenResultService sickenResultService;
    @Resource
    private SicknessService sicknessService;

    /**
     * 获取全部症状类型
     *
     * @return
     */
    @GetMapping(value = {"/miniprogram/second/all"})
    @ApiOperation(value = "获取症状自查症状类型(全部)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码数", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pid", value = "父类ID", required = true, dataType = "Long", paramType = "query")
    })
    public Object getTotalSymptom(
            @RequestParam(name = "pageNum") Integer pageNum,
            @RequestParam(name = "pageSize") Integer pageSize,
            @RequestParam(name = "pid") Integer pid) {
        log.debug("========SecondClassifyController/getTotalSymptom()==========");
        MiniSecondClassifyDTO classifyDto = secondClassifyService.getAllClassify(pageNum, pageSize, pid);
        if (classifyDto != null) {
            return ResponseEntity.ok(classifyDto);
        }
        throw new NotFoundException("获取所有二级子症状为空！");
    }


    /**
     * 获取身体某一部位下的症状
     *
     * @param pageNum
     * @param pageSize
     * @param secondId
     * @return
     */
    @GetMapping(value = {"/miniprogram/second/part"})
    @ApiOperation(value = "获取身体部位下的症状")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码数", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "secondId", value = "二级ID", required = true, dataType = "Long", paramType = "query")
    })
    public Object getBodyPartSymptoms(
            @RequestParam(name = "pageNum") Integer pageNum,
            @RequestParam(name = "pageSize") Integer pageSize,
            @RequestParam(name = "secondId") Integer secondId) {
        log.debug("=====进入获取身体部位下的症状,getBodyPartSymptoms()======");
        PageVO<MiniSymptomDTO> bodyPartSymptoms = symptomService.findBodyPartSymptoms(pageNum, pageSize, secondId);
        if (CollectionUtil.isNotEmpty(bodyPartSymptoms.getResult())) {
            return ResponseEntity.ok(bodyPartSymptoms);
        }
        throw new NotFoundException("获取身体部位下的症状为空！");
    }


    /**
     * 获取症状信息
     *
     * @param symptomId
     * @return
     */
    @GetMapping(value = {"/miniprogram/second/info/{symptomId}"})
    @ApiOperation(value = "获取症状信息", notes = "获取症状信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symptomId", value = "症状ID", required = true, dataType = "Long", paramType = "path")
    })
    public Object getZhengZhuangInfo(@PathVariable(name = "symptomId") Integer symptomId) {
        log.debug("=====进入获取症状信息方法,getZhengZhuangInfo()======");
        if (symptomId == null) {
            throw new BadRequestException("symptomId为空!");
        }

        SymptomInfoDTO infoDto = symptomService.getZhengZhuangInfo(symptomId);
        if (infoDto != null) {
            return ResponseEntity.ok(infoDto);
        }
        throw new NotFoundException("获取症状信息为空！");
    }


    /**
     * 获取症状详情接口
     *
     * @param symptomId
     * @return
     */
    @GetMapping(value = {"/miniprogram/second/detail/{symptomId}"})
    @ApiOperation(value = "获取症状详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symptomId", value = "症状ID", required = true, dataType = "Long", paramType = "path")
    })
    public SymptomDetailDTO getZhengZhuangDetail(@PathVariable(name = "symptomId") Integer symptomId) {
        log.debug("=====进入获取症状详情方法,getZhengZhuangDetail()======");
        if (symptomId == null) {
            throw new BadRequestException("传入参数symptomId为空");
        }
        SymptomDetailDTO detailDto = symptomService.getZhengZhuangDetail(symptomId);
        if (detailDto != null) {
            return detailDto;
        }
        throw new NotFoundException("获取症状信息为空！");
    }


    /**
     * 获取疾病结果详情
     *
     * @param sicknessId
     * @return
     */
    @GetMapping(value = {"/miniprogram/second/sicknessDetail/{sicknessId}"})
    @ApiOperation(value = "获取疾病结果详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sicknessId", value = "疾病ID", required = true, dataType = "Long", paramType = "path")
    })
    public MiniSicknessDTO getSicknessDetail(@PathVariable(name = "sicknessId") Integer sicknessId) {
        log.debug("=====进入获取疾病结果详情方法,getSicknessDetail()======");
        if (sicknessId == null) {
            throw new BadRequestException("传入参数:sicknessId为空!");
        }

        MiniSicknessDTO sickness = sicknessService.getSicknessById(sicknessId);
        if (sickness != null) {
            return sickness;
        }
        throw new NotFoundException("获取疾病结果详情为空！");
    }


    /**
     * 根据选择症状得到患病结果
     *
     * @param symptomIds
     * @return
     */
    @GetMapping(value = {"/miniprogram/second/sickness"})
    @ApiOperation(value = "根据选择症状得到患病结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symptomId", value = "症状ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "symptomIds", value = "症状ID数组", required = true, allowMultiple = true, dataType = "Long", paramType = "query")
    })
    public SicknessResultDTO getSicknessBySymptomId(
            @RequestParam(name = "symptomId") Integer symptomId,
            @RequestParam(name = "symptomIds") String[] symptomIds) {
        log.debug("=====进入根据选择症状得到患病结果方法,getSicknessBySymptomId()=====");
        if (symptomId == null) {
            throw new BadRequestException("传入参数:symptomId为空!");
        }

        if (symptomIds == null || symptomIds.length == 0) {
            throw new BadRequestException("传入参数symptomIds为空!");
        }

        SicknessResultDTO resultDto = sicknessService.getSicknessBySymptomId(symptomId, symptomIds);
        if (resultDto != null) {
            return resultDto;
        }
        throw new NotFoundException("得到患病结果为空!");

    }


    /**
     * 获取症状自查 所有症状类型
     *
     * @return
     */
    @GetMapping(value = {"/miniprogram/second/symptom"})
    @ApiOperation(value = "获取症状自查 所有症状类型")
    public List<MiniSecondClassifyDTO> getAllSymptom() {
        log.debug("========SecondClassifyController/getAllSymptom()==========");
        List<MiniSecondClassifyDTO> secondClassifyList = secondClassifyService.findAllSymptom();
        if (CollectionUtil.isNotEmpty(secondClassifyList)) {
            return secondClassifyList;
        }
        log.debug("#####SecondClassifyController获取所有症状类型为空#####");
        return null;
    }


    /**
     * 每种症状对应的：子症状和患病结果
     * 参数：symptomId 症状ID
     *
     * @return
     */
    @GetMapping(value = {"/miniprogram/second/result/{symptomId}"})
    @ApiOperation(value = "每种症状对应的：子症状和患病结果")
    @ApiImplicitParam(name = "symptomId", value = "症状ID", required = true, dataType = "Long", paramType = "path")
    public SubSymptomResultDTO getSubsymptomAndResult(@PathVariable(name = "symptomId") Integer symptomId) {
        log.debug("===进入每种症状对应的：子症状和患病结果 方法=====");
        if (symptomId == null) {
            throw new BadRequestException("症状ID为空！");
        }

        Map<String, String> map = new HashMap<String, String>(8);
        map.put("symptomId", symptomId + "");

        SubSymptomResultDTO symptomResultDto = symptomService.getSubsymptomAndResult(map);
        if (symptomResultDto != null) {
            return symptomResultDto;
        }
        log.debug("######子症状和患病结果为空！######");
        throw new NotFoundException("该部位下症状为空");
    }


    /**
     * 获取患病结果详细信息
     *
     * @param resultId
     * @return
     */
    @GetMapping(value = {"/miniprogram/second/resultDetail/{resultId}"})
    @ApiOperation(value = "获取患病结果详细信息")
    @ApiImplicitParam(name = "resultId", value = "患病结果ID", required = true, dataType = "Long", paramType = "path")
    public SickenResultDTO getResultDetail(@PathVariable(name = "resultId") Integer resultId) {
        log.debug("=====进入获取结果详情getResultDetail()=======");
        if (resultId == null) {
            throw new BadRequestException("传入resultId参数为空!");
        }

        log.debug("=====获取患病结果resultId为" + resultId + "=======");
        SickenResultDTO resultDto = sickenResultService.getResultDetail(resultId);
        if (resultDto != null) {
            return resultDto;
        }
        log.debug("######子获取结果详情为空！######");
        throw new NotFoundException("患病结果详情为空！");

    }


    /**
     * 获取热门搜索列表接口
     * flag:参数 默认传 1
     *
     * @return
     */
    @GetMapping(value = {"/miniprogram/second/getHotSearch"})
    @ApiOperation(value = "获取热门搜索列表接口")
    @ApiImplicitParam(name = "flag", value = "参数 默认传 1", required = true, dataType = "Long", paramType = "query")
    public SymptomAndSubsymDTO getHotSearch(@RequestParam(name = "flag") Integer flag) {
        log.debug("=====进入获取热门搜索列表getHotSearch()=====");
        if (flag != null && flag == 1) {
            SymptomAndSubsymDTO symptomAndSubsymDto = subsymptomService.findHotList(flag);
            return symptomAndSubsymDto;
        }
        throw new BadRequestException("获取参数标识不正确!");
    }


    /**
     * 根据子症状得到患病结果
     */
    @GetMapping(value = {"/miniprogram/result/{subsymId}"})
    @ApiOperation(value = "根据子症状得到患病结果")
    @ApiImplicitParam(name = "subsymId", value = "子症状ID", required = true, dataType = "Long", paramType = "path")
    public Object findResultBySubsymId(@PathVariable(name = "subsymId") Integer subsymId) {
        log.debug("====进入根据子症状得到患病结果findResultBySubsymId()方法=======");
        if (subsymId == null) {
            throw new BadRequestException("传入参数有误!");
        }

        SubSymptomDTO subDto = subsymptomService.findResultBySubsymId(subsymId);
        if (subDto != null) {
            return subDto;
        }
        throw new NotFoundException("获取subDto为空!");
    }


    /**
     * 搜索结果
     *
     * @return
     */
    @GetMapping(value = {"/miniprogram/second/search"})
    @ApiOperation(value = "搜索结果")
    @ApiImplicitParam(name = "keyWords", value = "关键词", required = true, dataType = "String", paramType = "query")
    public MiniSubsymptomDTO getSearchResult(@RequestParam(name = "keyWords") String keyWords) {
        log.debug("====进入搜索结果getSearchResult()方法=======");
        //搜索子症状
        MiniSubsymptomDTO subsymptom = subsymptomService.getSearchResult(keyWords);
        if (subsymptom != null) {
            return subsymptom;
        }
        throw new NotFoundException("获取subsymptom为空!");
    }

    /**
     * 获取分类名称以及包含热门症状
     *
     * @return
     */
    @GetMapping(value = {"/miniprogram/second/hotSymptom"})
    @ApiOperation(value = "获取分类名称以及包含热门症状")
    public List<ClassfySymptomDTO> getClassNameAndHotSymptom() {
        log.debug("====进入获取分类名称以及包含热门症状getClassNameAndHotSymptom()方法=======");
        List<ClassfySymptomDTO> classfySymptomDTOList = symptomService.getClassNameAndHotSymptom();
        if (CollectionUtil.isEmpty(classfySymptomDTOList)) {
            log.debug("=====获取分类名称以及包含热门症状为空======");
            throw new NotFoundException("返回数据为空");
        }

        return classfySymptomDTOList;

    }

}
