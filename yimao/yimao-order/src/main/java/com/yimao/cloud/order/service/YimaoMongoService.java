package com.yimao.cloud.order.service;

import com.yimao.cloud.base.enums.ProductModeEnum;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.HttpUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.feign.ProductFeign;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class YimaoMongoService {

    @Resource
    private DomainProperties domainProperties;

    //获取产品型号
    @Value(value = "${config.yimao.url.deviceModels}")
    private String deviceModelsUrl;
    //调用翼猫接口的授权码
    @Value(value = "${config.yimao.accessKey}")
    private String accessKey;
    //创建健康产品订单
    @Value(value = "${config.yimao.url.healthyProductOrder}")
    private String healthyProductOrderUrl;
    //水机工单计费方式列表
    @Value(value = "${config.yimao.url.listByDeviceScope}")
    private String listByDeviceScopeUrl;
    //创建水机工单
    @Value(value = "${config.yimao.url.waterOrder}")
    private String waterOrderUrl;

    @Resource
    private MailSender mailSender;
    @Resource
    private ProductFeign productFeign;

    /**
     * 调用接口向翼猫系统创建健康产品订单
     *
     * @param order
     * @param userInfo
     * @return
     */
    public JSONObject outSaveOrder(OrderSubDTO order, UserDTO userInfo, String tradeNo) {
        try {
            if (StringUtil.isNotEmpty(order.getRefer())) {
                log.info("此订单已创建工单");
                return null;
            }
            Long orderId = order.getId();
            log.info("===================================================");
            log.info("开始调用翼猫系统下单（健康产品）：" + orderId);
            log.info("===================================================");

            ProductDTO productDTO = productFeign.getProductById(order.getProductId());
            if (Objects.isNull(productDTO)) {
                log.error("健康产品下单失败，订单号：" + orderId + "。获取订单产品失败");
                return null;
            }

            // nickname 对应第二级   name对应第三级
            ProductCategoryDTO threeCategory = productFeign.getProductCategory(productDTO.getCategoryId());
            if (Objects.isNull(threeCategory)) {
                log.error("健康产品下单失败，订单号：" + orderId + "。获取订单产品三级类目失败");
                return null;
            }
            ProductCategoryDTO twoCategory = productFeign.getProductCategory(threeCategory.getPid());
            if (Objects.isNull(twoCategory)) {
                log.error("健康产品下单失败，订单号：" + orderId + "。获取订单产品二级类目失败");
                return null;
            }

            String deviceScope = twoCategory.getName().trim();

            if (StringUtil.isEmpty(deviceScope)) {
                log.error("健康产品下单失败，订单号：" + orderId + "。下单产品类型错误");
                return null;
            }

            String productId = "";
            //获取产品型号
            String url = deviceModelsUrl + "?accessKey=" + accessKey + "&deviceScope=" + deviceScope;
            String resp = this.callYimaoInterface("GET", url, null);
            if (StringUtil.isEmpty(resp)) {
                log.error("获取产品型号失败");
                return null;
            }
            JSONObject json = new JSONObject(resp);
            if (!json.getBoolean("success")) {
                log.error(json.getString("errorMsg"));
                return null;
            }
            JSONArray jsonArray = json.getJSONArray("list");
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject deviceModel = jsonArray.getJSONObject(i);
                log.error("订单型号：" + order.getProductType() + "==型号名称==：" + twoCategory.getName());
                if (Objects.equals(order.getProductType(), ProductModeEnum.REALTHING.value) && threeCategory.getName().toUpperCase().contains(deviceModel.getString("name").toUpperCase())) {
                    productId = deviceModel.getString("id");
                }
            }
            if (StringUtil.isEmpty(productId)) {
                log.error("健康产品下单失败，订单号：" + orderId + "。调用翼猫系统接口获取产品型号ID失败");
                return null;
            }

            Map<String, String> params = new HashMap<>(8);
            params.put("accessKey", accessKey);
            params.put("distributorId", userInfo.getOldDistributorId());
            params.put("productId", productId);
            params.put("count", String.valueOf(order.getCount()));
            params.put("userProvince", order.getAddresseeProvince());
            params.put("userCity", order.getAddresseeCity());
            params.put("userRegion", order.getAddresseeRegion());
            params.put("address", order.getAddresseeStreet());
            params.put("remark", "来自翼猫健康e家微商城的工单");
            params.put("sex", String.valueOf(order.getAddresseeSex()));
            params.put("name", order.getAddresseeName());
            params.put("phone", order.getAddresseePhone());
            params.put("tradeNo", tradeNo);//微信或者支付宝交易单号
            params.put("payType", String.valueOf(order.getPayType()));//支付类型：1-微信，2-支付宝，3-其他
            params.put("payTime", DateUtil.transferDateToString(order.getPayTime(), "yyyy-MM-dd HH:mm:ss"));//支付时间
            params.put("addTime", DateUtil.transferDateToString(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));//下单时间

            //向翼猫系统创建健康产品订单
            resp = this.callYimaoInterface("POST", healthyProductOrderUrl, params);
            if (StringUtil.isEmpty(resp)) {
                log.error("向翼猫系统创建健康产品订单失败");
                return null;
            }
            return new JSONObject(resp);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("调用翼猫系统【创建健康产品订单】接口失败！");
            log.error("YimaoMongoService.outSaveOrder : ", e);
            log.error(e.getMessage(), e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String subject = "健康产品下单失败提醒" + domainProperties.getWechat();
            String content = "向翼猫系统中下健康产品工单时出错。orderId=" + order.getId() + "\n" + sw.toString();
            mailSender.send(null, subject, content);
            return null;
        }
    }


    /**
     * 调用接口向翼猫系统创建水机工单
     *
     * @param order
     * @param userInfo
     * @return
     */
    public JSONObject outSaveWorkorder(OrderSubDTO order, UserDTO userInfo, String tradeNo) {
        try {
            if (StringUtil.isNotEmpty(order.getRefer())) {
                return null;
            }
            Long orderId = order.getId();
            log.info("===================================================");
            log.info("开始调用翼猫系统下单（水机）：" + orderId);
            log.info("===================================================");

            ProductDTO productDTO = productFeign.getProductById(order.getProductId());
            if (Objects.isNull(productDTO)) {
                log.error("净水产品下单失败，订单号：" + orderId + "。获取订单产品失败");
                return null;
            }
            // nickname 对应第二级   name对应第三级
            ProductCategoryDTO threeCategory = productFeign.getProductCategory(productDTO.getCategoryId());
            if (Objects.isNull(threeCategory)) {
                log.error("净水产品下单失败，订单号：" + orderId + "。获取订单产品三级类目失败");
                return null;
            }
            ProductCategoryDTO twoCategory = productFeign.getProductCategory(threeCategory.getPid());
            if (Objects.isNull(twoCategory)) {
                log.error("净水产品下单失败，订单号：" + orderId + "。获取订单产品二级类目失败");
                return null;
            }

            String deviceScope = twoCategory.getName().trim();
            String costId = this.getCostId(order.getCostName(), deviceScope);
            if (StringUtil.isEmpty(costId)) {
                log.error("水机下单失败，订单号：" + orderId + "。调用翼猫系统接口获取计费方式ID失败");
                throw new YimaoException("水机下单失败，订单号：" + orderId + "。调用翼猫系统接口获取计费方式ID失败");
            }

            String productId = "";
            //获取产品型号
            String url = deviceModelsUrl + "?accessKey=" + accessKey + "&deviceScope=" + deviceScope;
            String resp = this.callYimaoInterface("GET", url, null);
            if (StringUtil.isEmpty(resp)) {
                log.error("获取产品型号失败");
                return null;
            }
            JSONObject json = new JSONObject(resp);
            if (!json.getBoolean("success")) {
                log.error(json.getString("errorMsg"));
                return null;
            }
            JSONArray jsonArray = json.getJSONArray("list");
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject deviceModel = jsonArray.getJSONObject(i);
                if (threeCategory.getName().toUpperCase().contains(deviceModel.getString("name").toUpperCase())) {
                    productId = deviceModel.getString("id");
                }
            }
            if (StringUtil.isEmpty(productId)) {
                log.error("水机下单失败，订单号：" + orderId + "。调用翼猫系统接口获取产品型号ID失败");
                throw new YimaoException("水机下单失败，订单号：" + orderId + "。调用翼猫系统接口获取产品型号ID失败");
            }

            Map<String, String> params = new HashMap<>(8);
            params.put("accessKey", accessKey);
            params.put("distributorId", userInfo.getOldDistributorId());
            params.put("costId", costId);
            params.put("productId", productId);
            params.put("count", String.valueOf(order.getCount()));
            params.put("userProvince", order.getAddresseeProvince());
            params.put("userCity", order.getAddresseeCity());
            params.put("userRegion", order.getAddresseeRegion());
            params.put("address", order.getAddresseeStreet());
            params.put("time", DateUtil.transferDateToString(order.getServiceTime(), "yyyy-MM-dd HH:mm"));
            params.put("remark", "来自翼猫健康e家微商城的工单");
            params.put("sex", String.valueOf(order.getAddresseeSex()));
            params.put("name", order.getAddresseeName());
            params.put("phone", order.getAddresseePhone());
            params.put("tradeNo", tradeNo);//微信或者支付宝交易单号
            params.put("payType", String.valueOf(order.getPayType()));//支付类型：1-微信，2-支付宝，3-其他
            params.put("payTime", DateUtil.transferDateToString(order.getPayTime(), "yyyy-MM-dd HH:mm:ss"));//支付时间
            params.put("addTime", DateUtil.transferDateToString(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));//下单时间

            log.error("执行创建工单:" + order.getMongoEngineerId() + "====" + order.getEngineerId());
            if (Objects.nonNull(order.getEngineerId()) && Objects.nonNull(order.getMongoEngineerId())) {
                params.put("customerId", order.getMongoEngineerId());
                params.put("allotType", "1");//1;选择安装工  2：系统自动派单
            }
            //向翼猫系统创建水机工单
            resp = this.callYimaoInterface("POST", waterOrderUrl, params);
            if (StringUtil.isEmpty(resp)) {
                log.error("向翼猫系统创建水机工单失败");
                return null;
            }
            return new JSONObject(resp);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("水机下单失败。调用翼猫系统【创建水机工单】接口失败！");
            log.error(e.getMessage(), e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String subject = "水机下单失败提醒" + domainProperties.getWechat();
            String content = "向翼猫系统中下水机工单时出错。orderId=" + order.getId() + "\n" + sw.toString();
            mailSender.send(null, subject, content);
            return null;
        }
    }


    /**
     * 调用翼猫系统通用方法
     *
     * @param url    接口地址
     * @param params 传递参数
     * @return JSON对象
     * @throws Exception 异常
     */
    private String callYimaoInterface(String methodType, String url, Map<String, String> params) {
        try {
            String resp = null;
            if (Objects.equals(methodType, "GET")) {
                resp = HttpUtil.executeGet(url);
            } else if (Objects.equals(methodType, "POST")) {
                resp = HttpUtil.postData(url, params);
            }
            if (StringUtil.isEmpty(resp)) {
                log.error("调用翼猫系统接口" + url + "，参数：" + params + "失败！");
                throw new YimaoException("调用翼猫系统接口" + url + "，参数：" + params + "失败！");
            }

            if (params != null) {
                log.error("====================lh==========================================");
                log.error("调用翼猫系统接口" + url + "，参数：" + params);
                log.error("调用翼猫系统接口" + resp);
                log.error("====================lh==========================================");
            }

            return resp;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 获取计费方式
     */
    private String getCostId(String costName, String deviceScope) {
        try {
            String costId = "";
            //获取产品范围列表
            String url = listByDeviceScopeUrl + "?accessKey=" + accessKey + "&deviceScope=" + deviceScope;
            String resp = this.callYimaoInterface("GET", url, null);
            if (StringUtil.isEmpty(resp)) {
                log.error("获取产品范围列表失败");
                return null;
            }
            JSONObject json = new JSONObject(resp);
            if (!json.getBoolean("success")) {
                log.error(json.getString("errorMsg"));
                return null;
            }
            JSONArray jsonArray = json.getJSONArray("list");
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject cost = jsonArray.getJSONObject(i);
                if (costName.contains(cost.getString("name"))) {
                    costId = cost.getString("id");
                }
            }
            return costId;
        } catch (Exception e) {
            log.error("YimaoMongoService.getCostId : ", e);
            throw new YimaoException(e.getMessage(), e);
        }
    }

}
