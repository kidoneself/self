package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.UserFeign;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NoNeedSignException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.user.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Liu Yi
 * @description 经销商订单
 * @date 2019/8/22 11:30
 */
@RestController
@Slf4j
@Api(tags = "DistributorOrderController")
public class DistributorOrderController {

    @Resource
    private UserFeign userFeign;

    /**
     * 升级经销商订单
     *
     * @param dto
     * @author Liu Yi
     * @date 2019/1/9
     */
    @PostMapping(value = "user/distributor/order/upgrade", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "升级经销商订单")
    @ApiImplicitParam(name = "dto", value = "经销商DTO", required = true, dataType = "DistributorOrderDTO", paramType = "body")
    public Object upgradeOrder(@RequestBody DistributorOrderDTO dto) {
        Map<String, Object> map = userFeign.upgradeOrder(dto);
        return ResponseEntity.ok(map);
    }

    /**
     * 续费经销商订单
     *
     * @param dto
     * @author Liu Yi
     * @date 2019/1/9
     */
    @PostMapping(value = "user/distributor/order/renew", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "续费经销商订单")
    @ApiImplicitParam(name = "dto", value = "经销商DTO", required = true, dataType = "DistributorOrderDTO", paramType = "body")
    public Object renewOrder(@RequestBody DistributorOrderDTO dto) {
        Map<String, Object> map = userFeign.renewOrder(dto);
        return ResponseEntity.ok(map);
    }

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     * @description 获取签署合同信息
     * @author Liu Yi
     * @date 2019/8/22 10:32
     */
    @GetMapping(value = "/distributor/app/contract/{orderId}/preview")
    @ApiOperation(value = "获取签署合同信息")
    @ApiImplicitParam(name = "orderId", value = "订单id", required = true, dataType = "Long", paramType = "path")
    public Object previewContract(@PathVariable(value = "orderId") Long orderId) {
        DistributorProtocolDTO dto = userFeign.previewContract(orderId);
        return ResponseEntity.ok(dto);
    }

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     * @description 经销商app签署合同
     * @author Liu Yi
     * @date 2019/8/22 10:32
     */
    @PatchMapping(value = "/distributor/app/contract/{orderId}/sign")
    @ApiOperation(value = "经销商app签署合同")
    @ApiImplicitParam(name = "orderId", value = "订单id", required = true, dataType = "Long", paramType = "path")
    public Object signContract(@PathVariable(value = "orderId") Long orderId) {
        DistributorOrderDTO dto = userFeign.signContract(orderId);
        return ResponseEntity.ok(dto);
    }

    /**
     * @param orderType        订单类型1-升级 2-续费
     * @param distributorLevel 升级或者续费级别
     * @return java.lang.String
     * @description 获取经销商升级或者续费提示信息
     * @author Liu Yi
     * @date 2019/8/23 13:31
     */
    @GetMapping(value = "/distributor/app/order/remindMessage")
    @ApiOperation(value = "获取经销商升级或者续费提示信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderType", value = "订单类型：1-升级、2-续费", defaultValue = "1", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorLevel", value = "经销商升级等级", dataType = "Long", paramType = "query")
    })
    public Object getDistributorOrderRemindMessage(@RequestParam(value = "orderType") Integer orderType,
                                                   @RequestParam(value = "distributorLevel", required = false) Integer distributorLevel) {
        Map<String, Object> map = new HashMap<>();
        String message = userFeign.getDistributorOrderRemindMessage(orderType, distributorLevel);
        map.put("message", message);
        return ResponseEntity.ok(map);
    }

    /**
     * @param distributorId 经销商id
     * @return java.util.List<com.yimao.cloud.user.po.DistributorOrder>
     * @description 根据创建人（经销商）查询未完成订单
     * @author Liu Yi
     * @date 14:53 2019/8/20
     **/
    @GetMapping(value = "/distributor/app/order/unfinished")
    @ApiOperation(value = "根据创建人（经销商）查询未完成订单")
    @ApiImplicitParam(name = "distributorId", value = "创建者", required = true, dataType = "Long", paramType = "query")
    public Object unfinishedOrderListByCreator(@RequestParam(value = "distributorId") Integer distributorId) {
        List<DistributorOrderDTO> list = userFeign.unfinishedOrderListByCreator(distributorId);
        return ResponseEntity.ok(list);
    }

    /**
     * 上传营业执照
     *
     * @return
     */
    @RequestMapping(value = "/distributor/businessLicenseImage", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "上传营业执照", notes = "上传营业执照")
    public ResponseEntity uploadBusinessLicenseImage(@RequestPart("image") MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        String imageUrl = userFeign.uploadBusinessLicenseImage(file);
        map.put("imageUrl", imageUrl);

        return ResponseEntity.ok(map);
    }

    /**
     * 重新提交企业审核
     *
     * @param dto
     * @author Liu Yi
     * @date 2019/1/9
     */
    @PostMapping(value = "user/distributor/order/companyApply/renewCommit", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "重新提交企业审核")
    @ApiImplicitParam(name = "dto", value = "企业", required = true, dataType = "UserCompanyApplyDTO", paramType = "body")
    public Object renewCommitCompanyApply(@RequestBody UserCompanyApplyDTO dto) {
        userFeign.renewCommitCompanyApply(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 经销商订单提交支付凭证
     *
     * @param dto
     * @author Liu Yi
     * @date 2019/1/9
     */
    @PutMapping(value = "user/distributor/order/{id}/credential")
    @ApiOperation(value = "经销商订单提交支付凭证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "payType", value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "payCredential", value = "支付凭证", required = true, dataType = "String", paramType = "query")
    })
    public Object submitCredential(@PathVariable(value = "id") Long id,
                                   @RequestParam(value = "payType") Integer payType,
                                   @RequestParam(value = "payCredential") String payCredential) {
        userFeign.submitCredential(id, payType, payCredential);
        return ResponseEntity.noContent().build();
    }

    /**
     * 合同创建
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "distributor/protocol/create")
    @ApiOperation(value = "创建合同 ")
    @ApiImplicitParam(name = "dto", value = "合同信息", required = true, dataType = "DistributorProtocolDTO", paramType = "body")
    public Object createProtocol(@RequestBody DistributorProtocolDTO dto) {
        userFeign.createProtocol(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查看合同页
     *
     * @param distributorOrderId
     * @return
     */
    @GetMapping(value = "distributor/protocol/view/{distributorOrderId}")
    @ApiOperation(value = "查看合同页 ", notes = "查看合同页")
    @ApiImplicitParam(name = "distributorOrderId", value = "distributorOrderId", required = true, dataType = "Long", paramType = "path")
    public Object previewDistributorProtocol(@PathVariable(value = "distributorOrderId") Long distributorOrderId) {
        Map<String, String> resultMap = new HashMap<>();
        String url = userFeign.previewDistributorProtocol(distributorOrderId);
        resultMap.put("url", url);
        return ResponseEntity.ok(resultMap);
    }

    /**
     * 合同签署页面
     *
     * @param distributorOrderId
     * @return
     */
    @GetMapping(value = "distributor/protocol/sign/{distributorOrderId}")
    @ApiOperation(value = "合同签署页面 ", notes = "合同签署页面")
    @ApiImplicitParam(name = "distributorOrderId", value = "distributorOrderId", required = true, dataType = "Long", paramType = "path")
    public Object signDistributorProtocol(@PathVariable(value = "distributorOrderId") Long distributorOrderId) {
        Map<String, String> resultMap = new HashMap<>();
        String url = userFeign.signDistributorProtocol(distributorOrderId);
        resultMap.put("url", url);
        return ResponseEntity.ok(resultMap);
    }

    /**
     * 云签回调，修改合同签署状态
     *
     * @param info
     */
    @PostMapping("distributor/protocol/backCall")
    @ApiOperation(value = "云签回调，修改合同签署状态 ")
    public void backCall(@RequestParam("info") String info) {
        userFeign.backCall(info);
    }

    /**
     * 根据经销商的ID查询合同信息（用户，总部都已签署的合同）
     *
     * @param distributorId
     * @return
     */
    @GetMapping("distributor/protocol/listShow")
    @ApiOperation(value = "根据经销商的ID查询合同信息（用户，总部都已签署的合同） ")
    @ApiImplicitParam(name = "distributorId", value = "经销商id", required = true, dataType = "Long", paramType = "query")
    public Object queryDistributorProtocolByDistIdAndSignStatus(@RequestParam("distributorId") Integer distributorId) {
        List<DistributorProtocolDTO> distributorProtocolDTOList = userFeign.queryDistributorProtocolByDistIdAndSignStatus(distributorId);
        return ResponseEntity.ok(distributorProtocolDTOList);
    }

    /**
     * 经销商订单详情
     *
     * @param id
     */
    @GetMapping(value = "distributor/order/{id}/basic")
    @ApiOperation(value = "经销商订单详情")
    @ApiImplicitParam(name = "id", value = "订单号", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity findDistributorById(@PathVariable(value = "id") Long id) {
        DistributorOrderDTO distributorOrderDTO = userFeign.findBasisDistributorOrderById(id);
        return ResponseEntity.ok(distributorOrderDTO);
    }

    /**
     * 查询订单所需价格
     *
     * @param origLevel
     * @param destLevel
     * @param distributorId
     * @author Liu Yi
     * @date 2019/1/9
     */
    @GetMapping(value = "user/distributor/order/price")
    @ApiOperation(value = "查询升级所需价格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "distributorId", value = "经销商id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "origLevel", value = "原经销商等级", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "destLevel", value = "升级经销商等级", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型:0-注册、1-升级、2-续费", required = true, dataType = "Long", paramType = "query")
    })
    public Object getPrice(@RequestParam(value = "distributorId") Integer distributorId,
                           @RequestParam(value = "origLevel") Integer origLevel,
                           @RequestParam(value = "destLevel", required = false) Integer destLevel,
                           @RequestParam(value = "orderType") Integer orderType) {
        BigDecimal price = userFeign.getOrderPrice(distributorId, origLevel, destLevel, orderType);
        if (price == null) {
            return ResponseEntity.ok(0);
        }

        return ResponseEntity.ok(price);
    }

    @GetMapping("distributor/protocol/checkUserSignStatus")
    @ApiOperation(value = "校验用户签署状态")
    @ApiImplicitParam(name = "orderId", value = "经销商订单id", required = true, dataType = "Long", paramType = "query")
    public Object checkUserSignStatus(@RequestParam("orderId") Long orderId) {
        Map<String, String> resultMap = userFeign.checkUserSignStatus(orderId);
        return ResponseEntity.ok(resultMap);
    }
}
