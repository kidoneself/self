package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import com.yimao.cloud.pojo.dto.hra.ReserveDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.wechat.feign.HraFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 体检卡预约
 *
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/4/3
 */
@RestController
@Slf4j
@Api(tags = "HraReserveController")
public class HraReserveController {

    @Resource
    private HraFeign hraFeign;


    @PostMapping(value = {"/reserve"})
    @ApiOperation(value = "预约评估", notes = "预约评估")
    @ApiImplicitParam(name = "reserveDTO", value = "页码", required = true, dataType = "ReserveDTO", paramType = "body")
    public ResponseEntity<HraTicketDTO> reserveCreate(@RequestBody ReserveDTO reserveDTO) {
        HraTicketDTO hraTicketDTO = hraFeign.reserveCreate(reserveDTO);
        return ResponseEntity.ok(hraTicketDTO);
    }

    @GetMapping(value = {"/reserve/{pageNum}/{pageSize}"})
    @ApiOperation(value = "我的预约列表", notes = "我的预约列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页码", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<HraTicketDTO>> reserveList(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return ResponseEntity.ok(hraFeign.reserveList(pageNum, pageSize));
    }


    @PutMapping(value = {"/reserve/cancel/{id}"})
    @ApiOperation(value = "取消预约", notes = "取消预约")
    @ApiImplicitParam(name = "id", value = "体检劵ID", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    public ResponseEntity cancelReserve(@PathVariable("id") Integer id) {
        hraFeign.cancelReserve(id);
        return ResponseEntity.noContent().build();
    }
}
