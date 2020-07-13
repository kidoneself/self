package com.yimao.cloud.order.controller;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.order.mapper.IncomeSubjectMapper;
import com.yimao.cloud.order.po.IncomeSubject;
import com.yimao.cloud.pojo.dto.order.IncomeSubjectDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述：收益主体
 *
 * @author Liu Yi
 * @date 2018/12/17
 */
@RestController
@Api(tags = "IncomeSubjectController")
public class IncomeSubjectController {

    @Resource
    private IncomeSubjectMapper incomeSubjectMapper;

    @GetMapping(value = "/income/subject")
    @ApiOperation(value = "收益主体")
    public List<IncomeSubjectDTO> getIncomeSubject() {
        List<IncomeSubject> list = incomeSubjectMapper.selectAll();
        return CollectionUtil.batchConvert(list, IncomeSubject.class, IncomeSubjectDTO.class);
    }

}
