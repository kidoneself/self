package com.yimao.cloud.order.controller;

import com.yimao.cloud.order.service.MoveWaterDeviceOrderService;
import com.yimao.cloud.pojo.dto.order.MoveWaterDeviceOrderDTO;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.query.order.MoveWaterDeviceOrderQuery;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 移机工单
 *
 * @author Liu Long Jie
 * @date 2020-6-29
 */
@RestController
@Slf4j
@Api(tags = "MoveWaterDeviceOrderController")
public class MoveWaterDeviceOrderController {

    @Resource
    private MoveWaterDeviceOrderService moveWaterDeviceOrderService;

    /**
     * 新安装工app - 移机工单 - 待处理移机工单列表
     */
    @GetMapping(value = "/move/water/device/order/waitDispose/list")
    public Object getWaitDisposeList(@RequestParam(value = "engineerId") Integer engineerId,
                                     @RequestParam(value = "sort", required = false) Boolean sort,
                                     @RequestParam(value = "serviceType") Integer serviceType,
                                     @RequestParam(value = "longitude") Double longitude,
                                     @RequestParam(value = "latitude") Double latitude) {

        return ResponseEntity.ok(moveWaterDeviceOrderService.getWaitDisposeList(engineerId, sort, serviceType, longitude, latitude));
    }

    /**
     * 新安装工app - 移机工单 - 处理中移机工单列表
     */
    @GetMapping(value = "/move/water/device/order/dispose/list")
    public Object getDisposeList(@RequestParam(value = "engineerId") Integer engineerId,
                                 @RequestParam(value = "sort", required = false) Boolean sort,
                                 @RequestParam(value = "longitude") Double longitude,
                                 @RequestParam(value = "latitude") Double latitude) {

        return ResponseEntity.ok(moveWaterDeviceOrderService.getDisposeList(engineerId, sort, longitude, latitude));
    }

    /**
     * 新安装工app - 移机工单 - 挂单列表
     */
    @GetMapping(value = "/move/water/device/order/pending/list")
    public Object getPendingList(@RequestParam(value = "engineerId") Integer engineerId,
                                 @RequestParam(value = "sort", required = false) Boolean sort,
                                 @RequestParam(value = "longitude") Double longitude,
                                 @RequestParam(value = "latitude") Double latitude) {

        return ResponseEntity.ok(moveWaterDeviceOrderService.getPendingList(engineerId, sort, longitude, latitude));
    }

    /**
     * 新安装工app - 移机工单 - 已完成移机工单列表
     */
    @GetMapping(value = "/move/water/device/order/complete/list/{pageNum}/{pageSize}")
    public Object getCompleteList(@PathVariable(value = "pageNum") Integer pageNum,
                                  @PathVariable(value = "pageSize") Integer pageSize,
                                  @RequestParam(value = "engineerId") Integer engineerId,
                                  @RequestParam(value = "sort", required = false) Boolean sort) {

        return ResponseEntity.ok(moveWaterDeviceOrderService.getCompleteList(engineerId, sort, pageNum, pageSize));
    }

    /**
     * 新安装工app - 移机工单 - 待处理 - 拆机服务开始
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/dismantle/{id}")
    public Object dismantle(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(moveWaterDeviceOrderService.dismantle(id));
    }

    /**
     * 新安装工app - 移机工单 - 处理中 - 继续拆机服务
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/continueDismantle/{id}")
    public Object continueDismantle(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(moveWaterDeviceOrderService.continueDismantle(id));
    }

    /**
     * 新安装工app - 移机工单 - 处理中 - 点击“待移入地处理”回显拆机安装工信息
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/waitDismantle/{id}")
    public Object waitDismantle(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(moveWaterDeviceOrderService.waitDismantle(id));
    }

    /**
     * 完成拆机
     *
     * @param id         移机工单id
     * @param engineerId 拆机安装工id
     */
    @PatchMapping(value = "/move/water/device/order/completeDismantle/{id}")
    public Object completeDismantle(@PathVariable(value = "id") String id,
                                    @RequestParam(value = "engineerId") Integer engineerId) {

        return ResponseEntity.ok(moveWaterDeviceOrderService.completeDismantle(id, engineerId));
    }

    /**
     * 新安装工app - 移机工单 - 待处理 - 移入安装服务开始
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/install/{id}")
    public Object install(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(moveWaterDeviceOrderService.install(id));
    }

    /**
     * 新安装工app - 移机工单 - 处理中 - 继续移入安装服务
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/continueInstall/{id}")
    public Object continueInstall(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(moveWaterDeviceOrderService.continueInstall(id));
    }

    /**
     * 点击“等待拆机完成”回显装机安装工信息
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/waitInstall/{id}")
    public Object waitInstall(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(moveWaterDeviceOrderService.waitInstall(id));
    }

    /**
     * 完成移机工单
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/complete/{id}")
    public Object complete(@PathVariable(value = "id") String id) {
        moveWaterDeviceOrderService.complete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 新安装工app 移机工单完成列表获取工单详情
     *
     * @param id 移机工单id
     */
    @GetMapping(value = "/move/water/device/order/app/details/{id}")
    public Object appGetMoveWaterDeviceDetails(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(moveWaterDeviceOrderService.appGetMoveWaterDeviceDetails(id));
    }

    /**
     * 新安装工app 移机工单挂单操作
     *
     * @param id         移机工单id
     * @param type       1-拆机挂单 2-移入挂单
     * @param cause      改约原因
     * @param startTime  改约后服务时间（开始）
     * @param endTime    改约后服务时间（结束）
     * @param engineerId 操作安装工id
     */
    @GetMapping(value = "/move/water/device/order/hangUp/{id}")
    public Object hangUp(@PathVariable(value = "id") String id,
                         @RequestParam(value = "type") Integer type,
                         @RequestParam(value = "cause") String cause,
                         @RequestParam(value = "startTime") Date startTime,
                         @RequestParam(value = "endTime") Date endTime,
                         @RequestParam(value = "engineerId") Integer engineerId) {
        moveWaterDeviceOrderService.hangUp(id, type, cause, startTime, endTime, engineerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 业务管理后台 - 创建移机工单
     *
     * @param dto
     */
    @PostMapping(value = "/move/water/device/order/save")
    public Object save(@RequestBody MoveWaterDeviceOrderDTO dto) {
        moveWaterDeviceOrderService.save(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 移机模块工单统计
     *
     * @return
     */
    @GetMapping(value = "/move/water/device/statistics/count")
    public Map<String, Integer> getMoveWaterDeviceCount(@RequestParam(value = "engineerId") Integer engineerId) {
        return moveWaterDeviceOrderService.getMoveWaterDeviceCount(engineerId);
    }


    /**
     * 移机模块总工单数量
     *
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/move/model/water/device/total")
    public Integer getMoveModelTotalCount(@RequestParam(value = "engineerId") Integer engineerId) {
        return moveWaterDeviceOrderService.getMoveModelTotalCount(engineerId);
    }

    /**
     * 站务系统 - 移机工单 - 更换安装工
     *
     * @param id          移机工单号
     * @param engineerId  替换原服务人员的安装工id
     * @param engineerIds 操作人管理的售后服务站门店下的安装工
     * @param type        1-更换拆机服务人员；2-更换装机服务人员
     * @param source      来源端
     * @param operator    操作人
     */
    @PostMapping(value = "/move/water/device/order/changeEngineer/{id}")
    public Object changeEngineer(@PathVariable(value = "id") String id,
                                 @RequestParam(value = "engineerId") Integer engineerId,
                                 @RequestParam(value = "engineerIds") List<Integer> engineerIds,
                                 @RequestParam(value = "type") Integer type,
                                 @RequestParam(value = "source") Integer source,
                                 @RequestParam(value = "operator") String operator) {
        moveWaterDeviceOrderService.changeEngineer(id, engineerId, engineerIds, type, source,operator);
        return ResponseEntity.noContent().build();
    }


    /**
     * 服务统计-工单服务-移机统计
     *
     * @param completeTime
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/move/water/device/list")
    public List<RenewDTO> getMoveWaterDeviceList(@RequestParam(value = "completeTime") String completeTime,
                                                 @RequestParam(value = "engineerId") Integer engineerId,
                                                 @RequestParam(value = "timeType") Integer timeType) {
        return moveWaterDeviceOrderService.getMoveWaterDeviceList(completeTime, engineerId, timeType);
    }


    /**
     * 移机工单分页展示
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @PostMapping(value = "/move/water/device/order/{pageNum}/{pageSize}")
    public Object page(@PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize,
                       @RequestBody MoveWaterDeviceOrderQuery query) {

        return ResponseEntity.ok(moveWaterDeviceOrderService.moveWaterOrderPage(pageNum, pageSize, query));
    }


    @GetMapping(value = "/move/water/device/order/{id}")
    public Object getMoveWaterDeviceOrderDetailsById(@PathVariable(value = "id") String id) {
        return moveWaterDeviceOrderService.getMoveWaterDeviceOrderById(id);
    }

}
