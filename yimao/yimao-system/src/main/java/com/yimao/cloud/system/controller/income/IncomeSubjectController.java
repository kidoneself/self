package com.yimao.cloud.system.controller.income;

import com.yimao.cloud.pojo.dto.order.IncomeSubjectDTO;
import com.yimao.cloud.system.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述：收益主体
 *
 * @author hhf
 * @date 2019/4/23
 */
@RestController
@Slf4j
@Api(tags = "IncomeSubjectController")
public class IncomeSubjectController {

    @Resource
    private OrderFeign orderFeign;

    @GetMapping(value = "/income/subject")
    @ApiOperation(value = "收益主体")
    public List<IncomeSubjectDTO> getIncomeSubject() {
        return orderFeign.getIncomeSubject();
    }
}
