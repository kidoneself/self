package com.yimao.cloud.hra.controller;

import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NoLoginException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.hra.service.HraCardService;
import com.yimao.cloud.pojo.dto.hra.HraCardDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceOnlineDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zhang Bo
 * @date 2017/12/4.
 */
@RestController
@Slf4j
@Api(tags = "HraCardController")
public class HraCardController {

    @Resource
    private HraCardService hraCardService;
    @Resource
    private UserCache userCache;

    /**
     * 根据用户ID查询体检卡信息
     * (评估卡包含多种评估劵)
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/ticket/card/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据用户ID查询体检卡信息", notes = "根据用户ID查询体检卡信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity list(@PathVariable(name = "pageNum") Integer pageNum,
                               @PathVariable(name = "pageSize") Integer pageSize) {
        JWTInfo jwtInfo = userCache.getJWTInfo();
        if (Objects.isNull(jwtInfo)) {
            throw new NoLoginException();
        }
        PageVO<HraCardDTO> page = hraCardService.getHraCardByUser(pageNum, pageSize, jwtInfo.getId());
        return ResponseEntity.ok(page);
    }


    /**
     * 查询评估卡
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = {"/card/listCard"})
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码数", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", required = true, dataType = "Long", paramType = "query")
    })
    public ResponseEntity listCard(@RequestParam(name = "pageNum") Integer pageNum, @RequestParam(name = "pageSize") Integer pageSize) {
        JWTInfo jwtInfo = userCache.getJWTInfo();
        PageVO<HraCardDTO> page = hraCardService.listCardByUserId(pageNum, pageSize, jwtInfo.getId(), null);
        return ResponseEntity.ok(page);
    }


    /**
     * 获取评估券(小程序)
     *
     * @param state
     * @return
     */
    @GetMapping(value = {"/card/getCouponsList"})
    @ApiOperation(value = "获取评估券(小程序)", notes = "获取评估券(小程序)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "状态标识", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码数", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", required = true, dataType = "Long", paramType = "query")
    })
    public ResponseEntity getCouponsList(@RequestParam(name = "state") String state,
                                         @RequestParam(name = "pageNum") Integer pageNum,
                                         @RequestParam(name = "pageSize") Integer pageSize) {
        log.info("*****************获取评估券开始*****************");
        JWTInfo jwtInfo = userCache.getJWTInfo();
        Map<String, Object> map = hraCardService.listFreeCounpons(state, jwtInfo.getId(), pageSize, pageNum);
        return ResponseEntity.ok(map);
    }


    /**
     * 批量赠送评估券(小程序)
     *
     * @param cardId  //     * @param userId  被赠送用户
     * @param request
     * @return
     */
    @RequestMapping(value = {"/card/batchCoupons"}, method = {RequestMethod.POST})
    @ApiOperation(value = "批量赠送评估券(小程序)", notes = "批量赠送评估券(小程序)")
    @ApiImplicitParam(name = "cardId", value = "评估券ID", required = true, dataType = "String", paramType = "query")
    public ResponseEntity batchCoupons(@RequestParam(name = "cardId") String cardId, HttpServletRequest request) {
        log.debug("****************小程序批量赠送评估券开始****************");
        if (StringUtil.isEmpty(cardId)) {
            throw new BadRequestException("传入cardId为空");
        }
        return null;
    }


    /**
     * 删除评估卡
     *
     * @param cardId
     * @param request
     * @return
     */
    @DeleteMapping(value = {"/card/{cardId}"})
    @ApiOperation(value = "删除评估卡", notes = "删除评估卡")
    @ApiImplicitParam(name = "cardId", value = "评估卡ID", required = true, dataType = "String", paramType = "query")
    public ResponseEntity deleteCard(@PathVariable String cardId, HttpServletRequest request) {
        int count = hraCardService.deleteCard(cardId);
        if (count == -1) {
            throw new NotFoundException("存在未使用的卡，不能删除！");
        }
        if (count < 1) {
            throw new NotFoundException("卡号不存在或存在未使用的卡，不能删除！");
        }
        return ResponseEntity.ok(count);
    }


    /**
     * @Description: HRA设备管理-设备上线
     * @author ycl
     * @param: hraCardDTO
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/18 10:05
     */
    @PostMapping(value = {"/device"})
    @ApiOperation(value = "HRA设备管理-设备上线", notes = "HRA设备管理-设备上线")
    @ApiImplicitParam(name = "dto", value = "设备信息", required = true, dataType = "HraDeviceOnlineDTO", paramType = "body")
    public ResponseEntity saveStationOnline(@RequestBody HraDeviceOnlineDTO hraDeviceOnlineDTO) {
//        AdminDTO admin = userCache.getCurrentAdmin();
//        hraDeviceDTO.setCreator(admin.getUserName());
//        hraDeviceDTO.setUpdater(admin.getUserName());

        //去掉设备ID中间的"-"字符，同意设备ID格式
        this.formatDeviceId(hraDeviceOnlineDTO);
        hraCardService.saveHraDevice(hraDeviceOnlineDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * 去掉设备ID中间的"-"字符，同意设备ID格式
     *
     * @param haDeviceOnlineDTO
     */
    private void formatDeviceId(HraDeviceOnlineDTO haDeviceOnlineDTO) {
        haDeviceOnlineDTO.setDeviceId(haDeviceOnlineDTO.getDeviceId().replace("-", "").toUpperCase());
    }


    /**
     * @param orderId
     * @param userId
     * @param pageNum
     * @param pageSize
     * @Description: 根据订单号查询评估卡列表
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/4/19 10:21
     */
    @RequestMapping(value = "/order/hraCard/{pageNum}/{pageSize}", method = RequestMethod.GET)
    @ApiOperation(value = "根据订单号查询评估卡列表", notes = "根据订单号查询评估卡列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单ID", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path")
    })
    public ResponseEntity listCardByUserId(@RequestParam Long orderId,
                                           @RequestParam Integer userId,
                                           @PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<HraCardDTO> pageVO = hraCardService.listCardByUserId(pageNum, pageSize, userId, orderId);
        return ResponseEntity.ok(pageVO);
    }


    /**
     * @Author ycl
     * @Description 根据订单号查询体检卡
     * @Date 20:45 2019/6/24
     * @Param
     **/
    @RequestMapping(value = "/hraCard/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据订单号查询评估卡列表", notes = "根据订单号查询评估卡列表")
    public HraTicketDTO getHraTicketByOrderId(@PathVariable(value = "id") Long id) {
        return hraCardService.getHraTicketByOrderId(id);
    }


    /**
     * 根据订单号查询评估卷 [APP]
     *
     * @param orderId 订单号
     * @return obj
     */
    @RequestMapping(value = "/order/card/detail", method = RequestMethod.GET)
    @ApiOperation(value = "订单详情:查看评估卷", notes = "根据订单号查询评估卷")
    @ApiImplicitParam(name = "orderId", value = "订单ID", dataType = "Long", required = true, paramType = "query")
    public List<HraTicketDTO> cardDetailByOrderId(@RequestParam Long orderId) {
        return hraCardService.cardDetailByOrderId(orderId);
    }
}
