package com.yimao.cloud.hra.controller;

import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.hra.service.HraTicketService;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import com.yimao.cloud.pojo.dto.hra.ReserveDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * HRA体检预约前端控制器
 *
 * @author liuhao@yimaokeji.com
 * @date 2019/04/03.
 */
@RestController
@Slf4j
@Api(tags = "HraReserveController")
public class HraReserveController {

    @Resource
    private HraTicketService hraTicketService;
    @Resource
    private UserCache userCache;

    /**
     * 预约评估
     *
     * @author liuhao@yimaokeji.com
     * @date 2019-04-03
     */
    @PostMapping(value = {"/reserve"})
    @ApiOperation(value = "预约评估", notes = "预约评估")
    @ApiImplicitParam(name = "reserveDTO", value = "预约信息", required = true, dataType = "ReserveDTO", paramType = "body")
    public ResponseEntity create(@RequestBody ReserveDTO reserveDTO) {
        JWTInfo jwtInfo = userCache.getJWTInfo();
        //输入值范围：【50,300】可输入小数，不可超过 6 位字符
        if (reserveDTO.getHeight() == null || Double.parseDouble(reserveDTO.getHeight()) < 50 || Double.parseDouble(reserveDTO.getHeight()) > 300) {
            log.error("======传入reserveDto.getHeight()为：" + reserveDTO.getHeight() + "======");
            throw new BadRequestException("身高不能为空且身高范围要在50~300之间！");
        }
        //输入值范围：【0,500】 可输入小数，不可超过6 位字符
        if (reserveDTO.getWeight() == null || Double.parseDouble(reserveDTO.getWeight()) < 0 || Double.parseDouble(reserveDTO.getWeight()) > 500) {
            log.error("======传入reserveDto.getWeight()为：" + reserveDTO.getWeight() + "======");
            throw new BadRequestException("体重不能为空且体重范围要在0~500之间！");
        }
        HraTicketDTO hraTicket = hraTicketService.createReserveTicket(reserveDTO, jwtInfo.getId());
        if (hraTicket == null) {
            throw new NotFoundException("此评估卡不可用!");
        }
        return ResponseEntity.ok(hraTicket);
    }


    /**
     * 预约列表
     *
     * @return page
     * @author liuhao@yimaokeji.com
     */
    @GetMapping(value = {"/reserve/{pageNum}/{pageSize}"})
    @ApiOperation(value = "预约列表", notes = "预约列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码数", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity reserveList(@PathVariable(name = "pageNum") Integer pageNum,
                                      @PathVariable(name = "pageSize") Integer pageSize) {
        JWTInfo jwtInfo = userCache.getJWTInfo();
        PageVO<HraTicketDTO> page = hraTicketService.getHraTicketByUser(pageNum, pageSize, jwtInfo.getId());
        return ResponseEntity.ok(page);
    }


    /**
     * 取消预约
     *
     * @param ticketId 体检卡
     * @return
     */
    @PutMapping(value = {"/reserve/cancel/{ticketId}"})
    @ApiOperation(value = "取消预约", notes = "取消预约")
    @ApiImplicitParam(name = "ticketId", value = "体检劵ID", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity cancelReserve(@PathVariable(name = "ticketId") Integer ticketId) {
        JWTInfo jwtInfo = userCache.getJWTInfo();
        Integer userId = jwtInfo.getId();
        hraTicketService.cancelReserve(ticketId, userId);
        return ResponseEntity.noContent().build();
    }

}
