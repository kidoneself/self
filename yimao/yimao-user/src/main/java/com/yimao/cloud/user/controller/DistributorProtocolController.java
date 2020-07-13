package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.YunSignProperties;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignApi;
import com.yimao.cloud.pojo.dto.user.DistributorProtocolDTO;
import com.yimao.cloud.user.po.DistributorProtocol;
import com.yimao.cloud.user.service.DistributorProtocolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * @author Lizhqiang
 * @date 2019/1/2
 */
@RestController
@Api(tags = "DistributorProtocolController")
@Slf4j
public class DistributorProtocolController {

    @Resource
    private YunSignApi yunSignApi;

    @Resource
    private DistributorProtocolService distributorProtocolService;

    @Resource
    private YunSignProperties yunSignProperties;


    @GetMapping("distributor/protocol/checkUserSignStatus")
    @ApiOperation(value = "校验用户签署状态")
    @ApiImplicitParam(name = "orderId", value = "经销商订单id", required = true, dataType = "Long", paramType = "query")
    public Object checkUserSignStatus(@RequestParam("orderId") Long orderId) {
        Map<String, String> resultMap = distributorProtocolService.checkUserSignStatus(orderId);
        return ResponseEntity.ok(resultMap);
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
        List<DistributorProtocolDTO> distributorProtocolDTOList = distributorProtocolService.queryDistributorProtocolByDistIdAndSignStatus(distributorId);
        return ResponseEntity.ok(distributorProtocolDTOList);
    }

    /**
     * 云签回调，修改合同签署状态
     *
     * @param info
     */
    @PostMapping("distributor/protocol/backCall")
    @ApiOperation(value = "云签回调，修改合同签署状态 ")
    public Object backCall(@RequestParam("info") String info) {
        try {
            info = URLDecoder.decode(info, "UTF-8");
            log.info("云签 回调 ： " + info);
            JSONObject json = new JSONObject(info);
            String orderId = json.getString("orderId");

            String userId = json.getString("userId");
            String signer = json.getString("signer");
            String status = json.getString("status");
            String updateTime = json.getString("updateTime");
            String syncOrderId = json.getString("syncOrderId");
            String appSecretKey = yunSignProperties.getServiceKey();
            String md5 = orderId + "&" + signer + "&" + status + "&" + updateTime + "&" + userId + "&" + syncOrderId + "&" + appSecretKey;
            log.info(md5);
            String callbackCheck = json.getString("callbackCheck");
            String check = StringUtil.encodeMD5(md5).toLowerCase();
            log.info("call back check = " + callbackCheck + "   ==  " + check);
            if (check.equals(callbackCheck)) {
                log.info("回调合法");
                //处理回调
                distributorProtocolService.backCallYunSignOrderUpdate(userId, orderId, signer, updateTime, syncOrderId, status, appSecretKey);
            }
        } catch (UnsupportedEncodingException e) {
            throw new YimaoException("信息编码出现异常");
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 合同创建
     *
     * @param dto
     * @return
     * @author Liu Long Jie
     * @date 2019-9-22
     */
    @PostMapping(value = "distributor/protocol/create")
    @ApiOperation(value = "创建合同 ")
    @ApiImplicitParam(name = "dto", value = "合同信息（前端只传orderId）", required = true, dataType = "DistributorProtocolDTO", paramType = "body")
    public Object create(@RequestBody DistributorProtocolDTO dto) {
        long startTime = System.currentTimeMillis();
        DistributorProtocolDTO query = distributorProtocolService.getDistributorProtocolByOrderId(dto.getOrderId());
        //判断合同是否已创建
        if (!yunSignApi.queryContract(dto.getOrderId() + "").isSuccess() && query == null) {
            //合同没有创建，创建合同
            DistributorProtocol distributorProtocol = new DistributorProtocol(dto);
            distributorProtocolService.create(distributorProtocol);
        } else {
            log.info("订单对应合同已存在，无需再次创建");
            throw new BadRequestException("订单对应合同已存在，无需再次创建");
        }
        long endTime = System.currentTimeMillis();
        log.info("创建合同接口调用总时长" + (endTime - startTime) + "毫秒");
        return ResponseEntity.noContent().build();
    }

    /**
     * 查看合同页
     *
     * @param distributorOrderId
     * @return
     */
    @GetMapping(value = "/distributor/protocol/view/{distributorOrderId}")
    @ApiOperation(value = "查看合同页 ", notes = "查看合同页")
    @ApiImplicitParam(name = "distributorOrderId", value = "distributorOrderId", required = true, dataType = "Long", paramType = "path")
    public Object previewDistributorProtocol(@PathVariable(value = "distributorOrderId") Long distributorOrderId) {
        String url = distributorProtocolService.previewDistributorProtocol(distributorOrderId);
        return ResponseEntity.ok(url);
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
        String url = distributorProtocolService.signDistributorProtocol(distributorOrderId);
        return ResponseEntity.ok(url);
    }


    /**
     * 获取单个合同详情(暂时无用)
     *
     * @param distributorOrderId
     * @return
     */
    @GetMapping(value = "distributor/protocol/{distributorOrderId}")
    @ApiOperation(value = "合同记录 ", notes = "合同记录")
    @ApiImplicitParam(name = "distributorOrderId", value = "distributorOrderId", required = true, dataType = "Long", paramType = "path")
    public Object insert(@PathVariable(value = "distributorOrderId") Long distributorOrderId) {
        DistributorProtocolDTO distributorProtocolById = distributorProtocolService.getDistributorProtocolByOrderId(distributorOrderId);
        return ResponseEntity.ok(distributorProtocolById);
    }

    /**
     * 服务站复查合同
     *
     * @param id
     * @return
     */
    @PatchMapping(value = "distributor/protocol/renew/{id}")
    @ApiOperation(value = "服务站复查合同")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long", paramType = "path")
    public Object renew(@PathVariable(required = true) Integer id) {
        distributorProtocolService.renew(id);
        return ResponseEntity.noContent().build();
    }

}
