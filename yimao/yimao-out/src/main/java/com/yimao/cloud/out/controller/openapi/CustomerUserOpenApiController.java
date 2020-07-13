package com.yimao.cloud.out.controller.openapi;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.enums.InvoiceApplyStatus;
import com.yimao.cloud.base.enums.InvoiceHeadEnum;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.out.enums.OpenApiStatusCode;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.utils.ApiResult;
import com.yimao.cloud.out.utils.OpenApiResult;
import com.yimao.cloud.out.utils.ResultBean;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/openapi/customeruser")
public class CustomerUserOpenApiController {
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserFeign userFeign;

    /**
     * 根据手机号查询用户信息
     *
     * @param phone 水机用户手机号
     */
    @GetMapping(value = "/getInfoByPhone")
    public Map<String, Object> getInfobyphone(HttpServletRequest request, @RequestParam String phone) {
        WaterDeviceUserDTO user = userFeign.getWaterDeviceUserByPhone(phone);
        if (user != null) {
            JSONObject json = new JSONObject();
            json.put("userId", user.getOldId());
            json.put("name", user.getRealName());
            json.put("phone", user.getPhone());
            json.put("email", user.getEmail());
            json.put("sex", user.getSex());
            json.put("age", user.getAge());
            json.put("job", user.getJob());
            json.put("idCard", user.getIdCard());
            json.put("province", user.getProvince());
            json.put("city", user.getCity());
            json.put("region", user.getRegion());
            json.put("address", user.getAddress());
            return OpenApiResult.result(request, json);
        } else {
            return OpenApiResult.error(request, OpenApiStatusCode.DATA_NOT_FOUND);
        }
    }

    /**
     * @param
     * @return
     * @description 原售后请求微服务-开发票
     * @author Liu Yi
     * @date 2019/9/5 13:35
     */

    @PostMapping(value = "/invoice/order")
    @ApiOperation(value = "开发票", notes = "开发票")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "invoiceType", value = "发票类型售后是 2-个人；1-公司 ", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "发票抬头", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "售后是0-普票；1-专票 需要做转换", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "email", value = "申请人名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "bankName", value = "银行", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "bankAccount", value = "开户号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "taxCode", value = "税号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "address", value = "地址", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "联系方式", dataType = "String", paramType = "query")
    })
    public Map<String, Object> orderInvoice(HttpServletRequest request,
                                            @RequestParam String userId,
                                            @RequestParam String orderId,
                                            @RequestParam Integer invoiceType,
                                            @RequestParam String name,
                                            @RequestParam(required = false, defaultValue = "0") Integer type,
                                            @RequestParam(required = false, defaultValue = "") String email,
                                            @RequestParam(required = false, defaultValue = "") String bankName,
                                            @RequestParam(required = false, defaultValue = "") String bankAccount,
                                            @RequestParam(required = false, defaultValue = "") String taxCode,
                                            @RequestParam(required = false, defaultValue = "") String address,
                                            @RequestParam(required = false, defaultValue = "") String phone) {
        Map returnMap;
        if (StringUtil.isBlank(orderId)) {
            returnMap = ApiResult.error(request, "订单号不能为空！");
            return returnMap;
        }

        //Log.info("开发票接口参数打印：" + JsonUtil.BeanToJson(parameterMap), new Object[0]);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(orderId);
        if (workOrder == null) {
            returnMap = ApiResult.error(request, "该订单不存在！");
            return returnMap;
        }
        Long subOrderId = workOrder.getSubOrderId();
        ResultBean<OrderInvoiceDTO> orderInvoice = null;
        try {
            OrderInvoiceDTO invoice = new OrderInvoiceDTO();
            //根据老id查询用户
            UserDTO user = userFeign.getUserById(Integer.valueOf(userId));
            if (user == null) {
                returnMap = ApiResult.error(request, "用户不存在！");
                return returnMap;
            }
            if (type == 1) {//发票类型1、普票；2、专票 售后是0-普票；1-专票 需要做转换
                invoice.setInvoiceType(2);
            } else {
                invoice.setInvoiceType(1);
            }

            if (invoiceType == 1) {//售后：invoiceType 2-个人；1-公司
                invoice.setOldUserId(userId);
                invoice.setUserId(user.getId());
                invoice.setOrderId(subOrderId);
                invoice.setCompanyName(name);

                invoice.setBankAccount(bankAccount);
                invoice.setBankName(bankName);
                invoice.setDutyNo(taxCode);
                invoice.setApplyAddress(address);
                invoice.setApplyPhone(phone);
                invoice.setApplyTime(new Date());
                invoice.setApplyEmail(email);
                invoice.setInvoiceHead(InvoiceHeadEnum.COMPANY.value);//1、公司发票

                invoice.setCompanyAddress(address);
                invoice.setCompanyPhone(phone);
                invoice.setBankName(bankName);
                invoice.setBankAccount(bankAccount);
            } else if (invoiceType == 2) {
                //invoice.setId(IdGenerator.digitId());
                invoice.setUserId(user.getId());
                //invoice.setOldUserId(user.getOldId());
                invoice.setOrderId(subOrderId);
                //invoice.setBusinessOrderId(code);
                invoice.setDutyNo(taxCode);
                invoice.setApplyUser(name);
                invoice.setApplyEmail(email);
                invoice.setInvoiceType(2);
                invoice.setInvoiceHead(InvoiceHeadEnum.ONE.value);//2、个人发票
//                entity.setTaxCode(invoiceTax);
                invoice.setApplyAddress(address);
                invoice.setApplyPhone(phone);
            }

            invoice.setAmount(workOrder.getFee());

            workOrder.setInvoice(true);

            invoice.setSourceType(2);//来源：1-安装工app;2-健康e家公众号
            invoice.setAmount(workOrder.getFee());
            invoice.setApplyTime(new Date());
            workOrder.setBillTime(new Date());
            invoice.setApplyStatus(InvoiceApplyStatus.APPLIED.value);
            orderFeign.saveInvoice(invoice);
            orderFeign.updateWorkOrder(workOrder);
        } catch (Exception e) {
            returnMap = ApiResult.error(request, orderInvoice.getStatusText());
            return returnMap;
        }

        returnMap = ApiResult.success(request);
        return returnMap;
    }
}

