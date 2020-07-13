package com.yimao.cloud.hra.controller;

import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.HraTicketStatusEnum;
import com.yimao.cloud.base.enums.HraType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.hra.service.HraReportService;
import com.yimao.cloud.hra.service.HraTicketService;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import com.yimao.cloud.pojo.dto.hra.ReportDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@Slf4j
@Api(tags = "HraReportController")
public class HraReportController {

    @Resource
    private UserCache userCache;
    @Resource
    private HraTicketService hraTicketService;
    @Resource
    private HraReportService hraReportService;

    /**
     * 评估报告列表
     *
     * @param pageNum  页码
     * @param pageSize 页数
     * @return page
     * @author liuhao@yimaokeji.com
     */
    @GetMapping(value = {"/report/{pageNum}/{pageSize}"})
    @ApiOperation(value = "评估报告列表", notes = "评估报告列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object listReportRecord(@PathVariable(value = "pageNum") Integer pageNum,
                                   @PathVariable(value = "pageSize") Integer pageSize) {
        JWTInfo jwtInfo = userCache.getJWTInfo();
        Integer userId = jwtInfo.getId();
        try {
            //根据用户id，获取已完成的体检卡，自动添加报告
            hraReportService.saveReportRecord(userId);
        } catch (Exception e) {
            log.error("自动添加体检报告失败，e家号为：" + userId, e);
        }
        PageVO<ReportDTO> basePage = hraReportService.listReportRecord(pageNum, pageSize, userId);
        return ResponseEntity.ok(basePage);
    }


    /**
     * 添加评估报告
     *
     * @param phone    手机号
     * @param ticketNo 体检劵
     * @return int
     * @author liuhao@yimaokeji.com
     */
    @PostMapping(value = {"/report"})
    @ApiOperation(value = "添加评估报告", notes = "添加评估报告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true, defaultValue = "18210922956", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ticketNo", value = "评估卡号", required = true, defaultValue = "Y357299499875550", dataType = "String", paramType = "query")
    })
    public ResponseEntity addReportRecord(@RequestParam("phone") String phone, @RequestParam("ticketNo") String ticketNo) {
        JWTInfo jwtInfo = userCache.getJWTInfo();
        Integer userId = jwtInfo.getId();
        if (StringUtil.isEmpty(ticketNo)) {
            throw new BadRequestException("体检卡号不能为空！");
        }
        if (StringUtil.isEmpty(phone)) {
            throw new BadRequestException("手机号不能为空！");
        }

        ticketNo = ticketNo.trim();
        phone = phone.trim();
        Boolean flag = !ticketNo.toUpperCase().startsWith(HraType.M.value) && !ticketNo.toUpperCase().startsWith(HraType.Y.value) || ticketNo.length() != 16;
        if (flag) {
            log.error("体检卡号格式错误");
            throw new BadRequestException("体检卡号格式错误！");
        }

        HraTicketDTO hraTicket = hraTicketService.findTicketByUserIdAndTicketNo(ticketNo, userId);
        if (hraTicket == null) {
            throw new BadRequestException("体检卡号输入错误！");
        }

        log.debug("hraTicket.getTicketStatus()=" + hraTicket.getTicketStatus());
        if (hraTicket.getTicketStatus() == null || HraTicketStatusEnum.find(hraTicket.getTicketStatus()) == null || hraTicket.getDeviceId() == null) {
            throw new BadRequestException("此体检卡还未使用！");
        }
        //如果体检了，但是没有客户id 【可能出现】
        if (hraTicket.getHraCustomer() == null) {
            log.error("您的体检报告在体检的时候没有上传,客户信息不存在");
            throw new BadRequestException("非常抱歉,您的体检报告没有上传！");
        }
        if (!Objects.equals(phone, hraTicket.getHraCustomer().getPhone())) {
            log.error("手机号不一致！");
            throw new BadRequestException("手机号输入错误！");
        }
        int count = hraReportService.addReportRecord(phone, ticketNo, userId);
        if (count == -1) {
            log.error("非常抱歉,您的体检报告在体检的时候没有上传！");
            throw new BadRequestException("非常抱歉,您的体检报告在体检的时候没有上传！");
        }
        if (count == -2) {
            log.error("你已添加过此评估报告！");
            throw new BadRequestException("你已添加过此评估报告");
        }
        return ResponseEntity.noContent().build();
    }


    /**
     * 删除体检报告记录
     *
     * @param id 体检报告记录id
     * @return int
     * @author liuhao@yimaokeji.com
     */
    @DeleteMapping(value = "/report/{id}")
    @ApiOperation(value = "删除体检报告记录", notes = "删除体检报告记录")
    @ApiImplicitParam(name = "id", value = "体检报告ID", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity deleteRecord(@PathVariable("id") Integer id) {
        hraReportService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
