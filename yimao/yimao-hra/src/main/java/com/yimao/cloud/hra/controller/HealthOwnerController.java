package com.yimao.cloud.hra.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.JsonUtil;
import com.yimao.cloud.hra.service.AnswersRecordService;
import com.yimao.cloud.hra.service.AnswersService;
import com.yimao.cloud.hra.service.HealthOwnerService;
import com.yimao.cloud.pojo.dto.hra.AnswersDTO;
import com.yimao.cloud.pojo.dto.hra.ChoiceOptionDTO;
import com.yimao.cloud.pojo.dto.hra.EvaluatingDTO;
import com.yimao.cloud.pojo.dto.hra.EvaluatingImageDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description: 健康自测 控制类
 * @author: yu chunlei
 * @create: 2018-05-02 18:00:00
 **/
@RestController
@Slf4j
@Api(tags = "HealthOwnerController")
public class HealthOwnerController {

    @Resource
    private HealthOwnerService healthOwnerService;
    @Resource
    private AnswersRecordService answersRecordService;
    @Resource
    private AnswersService answersService;

    /**
     * 获取健康自测分类
     *
     * @return
     */
    @GetMapping(value = {"/miniprogram/health/{pageNum}/{pageSize}"})
    @ApiOperation(value = "获取健康自测分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码数", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object getCategoryName(@PathVariable(name = "pageNum") Integer pageNum, @PathVariable(name = "pageSize") Integer pageSize) {
        log.debug("========HealthOwnerController/getCategoryName()==========");
        if (pageNum == 0 || pageNum == null) {
            throw new BadRequestException("pageNum参数为空");
        }

        if (pageSize == 0 || pageSize == null) {
            throw new BadRequestException("pageSize参数为空");
        }

        List<EvaluatingDTO> dtoList = healthOwnerService.findAllHealthList(pageNum, pageSize);
        if (CollectionUtil.isNotEmpty(dtoList)) {
            return ResponseEntity.ok(dtoList);
        }
        throw new NotFoundException("获取健康测评为空！");
    }


    /**
     * 获取健康分类下的列表
     *
     * @param pageNum
     * @param pageSize
     * @param pid
     * @return
     */
    @GetMapping(value = {"/miniprogram/health"})
    @ApiOperation(value = "获取健康分类下的列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码数", defaultValue = "1", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", defaultValue = "10", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pid", value = "分类ID", required = true, dataType = "Long", paramType = "query")
    })
    public Object getHealthCatory(
            @RequestParam(name = "pageNum") Integer pageNum,
            @RequestParam(name = "pageSize") Integer pageSize,
            @RequestParam(name = "pid") Integer pid) {
        log.debug("========HealthOwnerController/getHealthCatory()==========");
        log.debug("####pageNum=" + pageNum + "#####pageSize=" + pageSize + "#####pid=" + pid);
        EvaluatingDTO evaluatingDto = healthOwnerService.getHealthCatory(pageNum, pageSize, pid);
        if (Objects.nonNull(evaluatingDto)) {
            return ResponseEntity.ok(evaluatingDto);
        }
        throw new NotFoundException("获取健康测评为空！");
    }


    /**
     * 获取测评下对应的选择题
     *
     * @return
     */
    @GetMapping(value = {"/health/{evaluateId}"})
    @ApiOperation(value = "获取测评下对应的选择题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "evaluateId", value = "测评ID", required = true, dataType = "Long", paramType = "path")
    })
    public Object getChoiceList(@PathVariable(name = "evaluateId") Integer evaluateId) {
        log.debug("======进入getChoiceList()=======");
        if (evaluateId == null) {
            throw new BadRequestException("参数evaluateId为空");
        }

        List<ChoiceOptionDTO> dtoList = healthOwnerService.findChoiceList(evaluateId);
        if (CollectionUtil.isNotEmpty(dtoList)) {
            return ResponseEntity.ok(dtoList);
        }
        throw new NotFoundException("获取选择题为空！");
    }

    /**
     * 根据用户的选择 返给用户相应的答案
     * evaluateId:测评ID
     * options:答案标识数组
     *
     * @return
     */
    @GetMapping(value = {"/miniprogram/health/answers"})
    @ApiOperation(value = "根据用户的选择 返给用户相应的答案")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "evaluateId", value = "测评ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "options", value = "选项", required = true, dataType = "String", paramType = "query")
    })
    public Object queryAnswers(
            @RequestParam(name = "evaluateId") Integer evaluateId,
            @RequestParam(name = "options") String options) {
        log.debug("option:" + JsonUtil.objectToJson(options));
        if (evaluateId == null) {
            throw new BadRequestException("evaluateId为空!");
        }

        if (Objects.isNull(options)) {
            throw new BadRequestException("options为空!");
        }

        String regex = "\"";
        String s = options.replaceAll(regex, "");
        String replaceAll = s.replace("[", "").replace("]","");
        List<String> list = Arrays.asList(replaceAll.split(","));
        List<Integer> integerList = new ArrayList<>();
        for (String i : list){
            //System.out.println("i" + i);
            integerList.add(Integer.parseInt(i));
        }
        AnswersDTO dto = answersService.getOptionAnswers(evaluateId, integerList);
        if (dto != null) {
            log.debug("dto=" + JsonUtil.objectToJson(dto));
            return ResponseEntity.ok(dto);
        }
        throw new NotFoundException("获取选项答案失败!");
    }


    /**
     * 选项保存 操作
     * 测评id,题干id,选项id
     *
     * @return
     */
    @PostMapping(value = {"/miniprogram/health/answers"})
    @ApiOperation(value = "选项保存 操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "evaluateId", value = "测评ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "choiceId", value = "题干id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "optionId", value = "选项id", required = true, dataType = "Long", paramType = "query")
    })
    public Object saveAnswers(
            @RequestParam(name = "evaluateId") Integer evaluateId,
            @RequestParam(name = "choiceId") Integer choiceId,
            @RequestParam(name = "optionId") Integer optionId) {
        log.debug("======进入保存选项saveAnswers()=======");
        if (evaluateId == null) {
            throw new BadRequestException("evaluateId为空!");
        }

        if (choiceId == null) {
            throw new BadRequestException("choiceId为空!");
        }

        if (optionId == null) {
            throw new BadRequestException("optionId为空!");
        }
        return ResponseEntity.ok(answersRecordService.saveOption(evaluateId, choiceId, optionId));
    }


    /**
     * 获取每个选题信息
     *
     * @param evaluateId 题目ID
     * @return
     */
    @GetMapping(value = {"/miniprogram/health/{evaluateId}"})
    @ApiOperation(value = "获取每个选题信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "evaluateId", value = "测评ID", required = true, dataType = "Long", paramType = "path")
    })
    public Object getEvaluateInfo(@PathVariable(name = "evaluateId") Integer evaluateId) {
        if (evaluateId == null) {
            throw new BadRequestException("题目ID");
        }

        Map<String, String> map = new HashMap<>(16);
        map.put("evaluateId", evaluateId + "");
        EvaluatingImageDTO evaluating = healthOwnerService.getEvaluateInfo(map);
        if (Objects.isNull(evaluating)) {
            throw new NotFoundException("获取每个选题信息为空!");
        }
        return ResponseEntity.ok(evaluating);
    }


}
