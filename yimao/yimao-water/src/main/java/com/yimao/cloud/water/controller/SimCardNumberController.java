package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.water.SimCardNumberDTO;
import com.yimao.cloud.pojo.query.water.SimCardNumberQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.SimCardNumberVO;
import com.yimao.cloud.water.po.SimCardNumber;
import com.yimao.cloud.water.service.SimCardNumberService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：SIM号码段
 *
 * @Author Zhang Bo
 * @Date 2019/4/25
 */
@RestController
public class SimCardNumberController {

    @Resource
    private SimCardNumberService simCardNumberService;

    /**
     * 创建SIM号码段
     *
     * @param dto SIM号码段
     */
    @PostMapping(value = "/simcard/number")
    public void save(@RequestBody SimCardNumberDTO dto) {
        SimCardNumber simCardNumber = new SimCardNumber(dto);
        simCardNumberService.save(simCardNumber);
    }

    /**
     * 修改SIM号码段
     *
     * @param dto SIM号码段
     */
    @PutMapping(value = "/simcard/number")
    public void update(@RequestBody SimCardNumberDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            SimCardNumber simCardNumber = new SimCardNumber(dto);
            simCardNumberService.update(simCardNumber);
        }
    }

    /**
     * 查询SIM号码段（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/simcard/number/{pageNum}/{pageSize}")
    public PageVO<SimCardNumberVO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody SimCardNumberQuery query) {
        return simCardNumberService.page(pageNum, pageSize, query);
    }

}
