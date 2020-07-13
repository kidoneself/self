package com.yimao.cloud.out.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ConnectionTypeEnum;
import com.yimao.cloud.base.enums.PayReceiveType;
import com.yimao.cloud.base.enums.PayStatus;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.enums.ProductCategoryLevel;
import com.yimao.cloud.base.enums.ScanCodeTypeEnum;
import com.yimao.cloud.base.enums.StationAreaServiceTypeEnum;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.enums.WaterDeviceCategoryEnum;
import com.yimao.cloud.base.enums.WorkOrderInstallStep;
import com.yimao.cloud.base.enums.WorkOrderStatusEnum;
import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.exception.YimaoRemoteException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.service.SmsService;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DESUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.MD5Util;
import com.yimao.cloud.base.utils.SmsUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.yun.YunOldIdUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.out.enums.DeviceScanEnum;
import com.yimao.cloud.out.enums.InternetStatusEnum;
import com.yimao.cloud.out.feign.CmsFeign;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.feign.ProductFeign;
import com.yimao.cloud.out.feign.SystemFeign;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.feign.WaterFeign;
import com.yimao.cloud.out.service.WorkOrderApi;
import com.yimao.cloud.out.utils.DecryptSupport;
import com.yimao.cloud.out.utils.ResultUtil;
import com.yimao.cloud.out.vo.EngineerVO;
import com.yimao.cloud.out.yunApi.constant.Constant;
import com.yimao.cloud.pojo.dto.cms.BannerDTO;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceDTO;
import com.yimao.cloud.pojo.dto.order.PayAccountDetail;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.pojo.dto.system.ReasonDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDynamicCipherConfigDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDynamicCipherRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：安装工调用云平台接口（除工单以外的）
 *
 * @Author Zhang Bo
 * @Date 2019/3/8 15:55
 */
@Slf4j
@RestController
@Api(tags = "EngineerApiController")
public class EngineerApiController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private WaterFeign waterFeign;
    // @Resource
    // private MongoTemplate mongoTemplate;
    @Resource
    private SmsService smsService;
    @Resource
    private WorkOrderApi workOrderApi;

    @Resource
    private RedisCache redisCache;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private DomainProperties domainProperties;
    @Resource
    private CmsFeign cmsFeign;

    /**
     * 安装工登录（原云平台）
     *
     * @param username 用户名
     * @param password 密码
     * @param appType  终端类型：1-Android；2-Ios
     * @param iccid    SIM卡号
     * @param version  APP版本信息
     */
    @PostMapping(value = "/api/customer/login")
    @ApiOperation(value = "安装工登录（原云平台）")
    public Map<String, Object> engineerLogin(@RequestParam String username,
                                             @RequestParam String password,
                                             @RequestParam Integer appType,
                                             @RequestParam(required = false, defaultValue = "") String iccid,
                                             @RequestParam(required = false, defaultValue = "") String version) {
        if (!com.yimao.cloud.base.constant.Constant.PRO_ENVIRONMENT) {
            log.info("安装工登录接口：/api/customer/login，username={}，appType={}，iccid={}，version={}", username, appType, iccid, version);
        }
        Map<String, Object> ru = new HashMap<>();
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        }
        if (!Objects.equals(engineer.getPassword(), password)) {
            ResultUtil.error(ru, Constant.PASSWORD_ERROR_CODE, Constant.PASSWORD_ERROR_MSG);
            return ru;
        }
        if (engineer.getForbidden()) {
            ResultUtil.error(ru, Constant.USER_LOGIN_VALID_CODE, Constant.USER_LOGIN_VALID_MSG3);
            return ru;
        }
        if (StringUtil.isBlank(iccid)) {
            ResultUtil.error(ru, Constant.USER_LOGIN_VALID_CODE, Constant.USER_LOGIN_VALID_MSG2);
            return ru;
        }
        engineer.setAppType(appType);
        if (StringUtil.isNotBlank(version)) {
            engineer.setVersion(version);
        }

        iccid = iccid.toLowerCase();
        String dbIccid = engineer.getIccid();
        if (StringUtil.isEmpty(dbIccid)) {
            engineer.setIccid(iccid);
            boolean flag = userFeign.checkEngineerIccid(iccid);
            if (flag) {
                log.error("安装工登录接口---Sim已绑定，无法登录：username={}，appType={}，iccid={}，version={}", username, appType, iccid, version);
                ResultUtil.error(ru, Constant.USER_LOGIN_VALID_CODE, Constant.USER_LOGIN_VALID_MSG4);
                return ru;
            }
        } else if (!Objects.equals(dbIccid, iccid)) {
            ResultUtil.error(ru, Constant.USER_LOGIN_VALID_CODE, Constant.USER_LOGIN_VALID_MSG);
            return ru;
        }
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(engineer.getProvince(), engineer.getCity(), engineer.getRegion());
        //服务站正在上线新流程校验
        if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
            ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录。");
            return ru;
        }

        //安装工登录时更新部分属性
        Integer loginCount = engineer.getLoginCount();
        engineer.setLoginCount((loginCount == null ? 0 : loginCount) + 1);
        userFeign.updateEngineer(engineer);

        Integer engineerId = engineer.getId();

        //1-工单已分配待接单（包含手动分配和系统分配）
        int assigned = orderFeign.countWorkOrderByEngineerId(engineerId, WorkOrderStatusEnum.ASSIGNED.value);
        //2-安装工接单
        int accepted = orderFeign.countWorkOrderByEngineerId(engineerId, WorkOrderStatusEnum.ACCEPTED.value);
        //3-安装中
        int installing = orderFeign.countWorkOrderByEngineerId(engineerId, WorkOrderStatusEnum.INSTALLING.value);
        //4-安装完成
        int completed = orderFeign.countWorkOrderByEngineerId(engineerId, WorkOrderStatusEnum.COMPLETED.value);
        //合计
        int total = orderFeign.countWorkOrderByEngineerId(engineerId, null);

        EngineerVO vo = new EngineerVO();
        vo.setId(engineer.getOldId());
        vo.setName(engineer.getRealName());
        vo.setImageName(engineer.getHeadImg());
        vo.setSex(String.valueOf(engineer.getSex()));
        vo.setPhone(engineer.getPhone());
        vo.setProvince(engineer.getProvince());
        vo.setCity(engineer.getCity());
        vo.setRegion(engineer.getRegion());
        vo.setAddress(engineer.getAddress());
        vo.setCompany("");
        vo.setJob("");
        vo.setWorkId(engineer.getWorkId());
        vo.setMail(engineer.getEmail());

        ru.put("customer", vo);
        ru.put("notaccepted", assigned);
        ru.put("accepted", accepted);
        ru.put("serving", installing);
        ru.put("complete", completed);
        ru.put("total", total);

        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 上传图片
     */
    @RequestMapping(value = "/api/customer/upload/image", method = RequestMethod.POST)
    @ResponseBody()
    public Map<String, Object> image(@RequestParam String username, @RequestParam String image) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        }
        log.info("安装工APP上传的头像url为{}，该怎么保存呢？", image);
        return ru;
    }

    /**
     * 安装工详情
     */
    @GetMapping(value = "/api/customer/details")
    @ApiOperation(value = "安装工详情（原云平台）")
    public Map<String, Object> details(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        }
        if (!Objects.equals(engineer.getPassword(), password)) {
            ResultUtil.error(ru, Constant.PASSWORD_ERROR_CODE, Constant.PASSWORD_ERROR_MSG);
            return ru;
        }
        if (engineer.getForbidden()) {
            ResultUtil.error(ru, Constant.USER_LOGIN_VALID_CODE, Constant.USER_LOGIN_VALID_MSG);
            return ru;
        }
        EngineerVO vo = new EngineerVO();
        vo.setName(engineer.getRealName());
        vo.setImageName(engineer.getHeadImg());
        vo.setSex(String.valueOf(engineer.getSex()));
        vo.setPhone(engineer.getPhone());
        vo.setProvince(engineer.getProvince());
        vo.setCity(engineer.getCity());
        vo.setRegion(engineer.getRegion());
        vo.setAddress(engineer.getAddress());
        vo.setCompany("");
        vo.setJob("");
        vo.setWorkId(engineer.getWorkId());
        vo.setMail(engineer.getEmail());
        ru.put("customer", vo);
        return ru;
    }

    /**
     * 安装工修改密码（原云平台）
     *
     * @param username    安装工用户名
     * @param oldpassword 旧密码
     * @param newpassword 新密码
     */
    @PostMapping(value = "/api/customer/change")
    @ApiOperation(value = "安装工修改密码（原云平台）")
    public Map<String, Object> change(@RequestParam String username,
                                      @RequestParam(required = false, defaultValue = "") String oldpassword,
                                      @RequestParam String newpassword) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        }
        if ("".equals(oldpassword) || oldpassword.equalsIgnoreCase(engineer.getPassword())) { //将md5加密后的用户输入的密码与数据库中的密码进行比较
            EngineerDTO updateDTO = new EngineerDTO();
            updateDTO.setId(engineer.getId());
            updateDTO.setPassword(newpassword.trim());
            userFeign.updatePassword(updateDTO);
        } else {
            ResultUtil.error(ru, Constant.PASSWORD_ERROR_CODE, Constant.PASSWORD_ERROR_MSG);
        }
        return ru;
    }

    /**
     * 验证安装工手机号是否绑定
     */
    @PostMapping(value = "/api/customer/bind")
    @ApiOperation(value = "验证安装工手机号是否绑定（原云平台）")
    public Map<String, Object> bind(@RequestParam String username, @RequestParam String phone) {//客服
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        }
        String telphone = engineer.getPhone();
        if (!phone.equals(telphone)) {
            ResultUtil.error(ru, Constant.PHONE_NOT_EXIST_CODE, Constant.PHONE_NOT_EXIST_MSG);
        }
        return ru;
    }

    /**
     * 获取验证码
     */
    @PostMapping(value = "/api/customer/get/smscode")
    @ApiOperation(value = "安装工获取验证码（原云平台）")
    public Map<String, Object> smscode(@RequestParam() String username, @RequestParam int type) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        if (type == 1) {
            ResultUtil.error(ru, Constant.MSGCODE_FAIL_CODE, Constant.MSGCODE_FAIL_MSG);
            return ru;
        }
        EngineerDTO engineer = userFeign.getEngineerByPhone(username.trim());
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        }
        try {
            String code = smsService.getCode(engineer.getPhone(), com.yimao.cloud.base.constant.Constant.COUNTRY_CODE);
            String text = "【翼猫服务APP】您绑定手机的验证码是" + code + "。";
            String s = SmsUtil.sendSms(text, engineer.getPhone());
            log.info("发送的验证码为：" + code + "，短信接口返回为：" + s);
            ResultUtil.success(ru);
        } catch (Exception e) {
            ResultUtil.error(ru, Constant.MSGCODE_FAIL_CODE, Constant.MSGCODE_FAIL_MSG);
        }
        return ru;
    }

    /**
     * 验证验证码
     */
    @PostMapping(value = "/api/customer/validate/code")
    @ApiOperation(value = "安装工验证验证码（原云平台）")
    public Map<String, Object> code(@RequestParam String username,
                                    @RequestParam String phone, @RequestParam() String code) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        }
        //校验手机验证码
        Boolean bool = smsService.verifyCode(engineer.getPhone(), com.yimao.cloud.base.constant.Constant.COUNTRY_CODE, code);
        if (!bool) {
            ResultUtil.error(ru, Constant.MSGCODE_ERROR_CODE, Constant.MSGCODE_ERROR_MSG);
        }
        return ru;
    }

    /**
     * 安装工获取当天、当周、当月、当年的工单数量（原云平台）
     *
     * @param username 安装工用户名
     */
    @GetMapping(value = "/api/customer/workorder/count")
    @ApiOperation(value = "安装工获取当天、当周、当月、当年的工单数量（原云平台）")
    public Map<String, Object> countEngineerWorkOrder(@RequestParam String username) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        } else {
            Date dayStartTime = DateUtil.getCurrentDayBeginTime();
            Date weekStartTime = DateUtil.getCurrentWeekBeginTime();
            Date monthStartTime = DateUtil.getCurrentMonthBeginTime();
            Date yearStartTime = DateUtil.getCurrentYearBeginTime();
            Date end = DateUtil.getCurrentDayEndTime();
            Integer engineerId = engineer.getId();
            int daycount = orderFeign.countWorkOrderByEngineerId(engineerId, dayStartTime, end);
            int weekcount = orderFeign.countWorkOrderByEngineerId(engineerId, weekStartTime, end);
            int monthcount = orderFeign.countWorkOrderByEngineerId(engineerId, monthStartTime, end);
            int total = orderFeign.countWorkOrderByEngineerId(engineerId, yearStartTime, end);
            ru.put("daycount", daycount);
            ru.put("weekcount", weekcount);
            ru.put("monthcount", monthcount);
            ru.put("total", total);
        }
        return ru;
    }

    /**
     * 安装工安装收益（家用和商用）（原云平台）
     */
    @GetMapping(value = "/api/customer/workorder/income")
    @ApiOperation(value = "安装工安装收益（家用和商用）（原云平台）")
    public Map<String, Object> incomeworkorder(@RequestParam String username,
                                               @RequestParam(required = false) String time,
                                               @RequestParam(required = false) String year,
                                               @RequestParam(required = false) String month,
                                               @RequestParam Integer type) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        } else {
            Date start = null;
            Date end = null;
            if ("day".equals(time)) {
                start = DateUtil.getCurrentDayBeginTime();
                end = DateUtil.getCurrentDayEndTime();
            }

            if (StringUtil.isNotEmpty(year)) {
                start = DateUtil.transferStringToDate(year, "yyyy");
                end = DateUtil.getYearEndTime(start);
            }

            if (StringUtil.isNotEmpty(month)) {
                String date = year + "-" + month;
                start = DateUtil.transferStringToDate(date, "yyyy-MM");
                end = DateUtil.getMonthEndTime(start);
            }

            List<Integer> productIdList = null;
            if (type == 1) {//家用
                productIdList = productFeign.listProductIdsByCategoryName(WaterDeviceCategoryEnum.FAMILY.name, ProductCategoryLevel.SECOND.value);
            } else if (type == 2) {//商用
                productIdList = productFeign.listProductIdsByCategoryName(WaterDeviceCategoryEnum.BUSINESS.name, ProductCategoryLevel.SECOND.value);
            }
            Integer engineerId = engineer.getId();
            long notaccepted = orderFeign.countWorkOrderByEngineerId(engineerId, WorkOrderStatusEnum.ASSIGNED.value, start, end, productIdList);
            long accepted = orderFeign.countWorkOrderByEngineerId(engineerId, WorkOrderStatusEnum.ACCEPTED.value, start, end, productIdList);
            long serving = orderFeign.countWorkOrderByEngineerId(engineerId, WorkOrderStatusEnum.INSTALLING.value, start, end, productIdList);
            long complete = orderFeign.countWorkOrderByEngineerId(engineerId, WorkOrderStatusEnum.COMPLETED.value, start, end, productIdList);
            ru.put("notaccepted", notaccepted);
            ru.put("accepted", accepted);
            ru.put("serving", serving);
            ru.put("complete", complete);
        }
        return ru;
    }

    /**
     * 所在区域的客服列表
     */
    @GetMapping(value = "/api/customer/list")
    @ApiOperation(value = "所在区域的客服列表（原云平台）")
    public Map<String, Object> list(@RequestParam String province,
                                    @RequestParam String city,
                                    @RequestParam String region) {
        Map<String, Object> ru = new HashMap<>();
        Integer areaId=systemFeign.getRegionIdByPCR(province, city, region);
        if(null==areaId) {
        	log.error("===============省市区所属区域id 为空:provice="+province+",city="+city+",region="+region);
        	ResultUtil.error(ru); 
        	return ru;
        }
        List<EngineerDTO> list = userFeign.listEngineerByRegion(areaId);
        List<EngineerVO> voList = new ArrayList<>();
        for (EngineerDTO dto : list) {
            EngineerVO vo = new EngineerVO();
            vo.setId(String.valueOf(dto.getId()));
            vo.setName(dto.getRealName());
            vo.setPhone(dto.getPhone());
            voList.add(vo);
        }
        ru.put("list", voList);
        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 客服动态密码 扫描二维码解密
     */
    @RequestMapping(value = "/api/customer/decrypt/code", method = RequestMethod.GET)
    @ResponseBody()
    public Map<String, Object> decrypt(@RequestParam() String username,
                                       @RequestParam() String text) {
        Map<String, Object> ru = new HashMap<>();
        if (text.split("#").length != 2) {
            ResultUtil.error(ru, Constant.PARAM_ERROR_CODE, Constant.PARAM_ERROR_MSG);
            return ru;
        }
        String code = DecryptSupport.decrypt(text);
        ru.put("code", code);
        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 安装工APP-查询工单列表
     */
    @GetMapping(value = "/api/workorder/customer/list")
    @ApiOperation(value = "安装工APP-查询工单列表")
    public Map<String, Object> list(@RequestParam String username,//客服
                                    @RequestParam String password,
                                    @RequestParam Integer state,
                                    @RequestParam(required = false) Integer paystate,
                                    @RequestParam(required = false) Integer type) {
        Integer localPayStatus = null;
        if (paystate != null) {
            if (paystate == 0) {
                localPayStatus = 1;
            } else if (paystate == 1) {
                localPayStatus = 2;
            } else if (paystate == 2) {
                localPayStatus = 4;
            } else if (paystate == 3) {
                localPayStatus = 3;
            }
        }
        // 根据姓名获取安装工信息
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        Map<String, Object> ru = new HashMap<>();
        if (null == engineer) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        }
        // 校验密码
        if (!engineer.getPassword().equalsIgnoreCase(password)) {
            ResultUtil.error(ru, Constant.PASSWORD_ERROR_CODE, Constant.PASSWORD_ERROR_MSG);
            return ru;
        }
        log.info("安装工id=" + engineer.getId());
        List<WorkOrderDTO> workOrderList;
        if (type != null) {
            workOrderList = orderFeign.getWorkOrderInfo(engineer.getId(), null, null, type);
        } else {
            workOrderList = orderFeign.getWorkOrderInfo(engineer.getId(), state, localPayStatus, null);
        }
        //获取所有计费方式列表
        List<JSONObject> voList = new ArrayList<>();
        //工单信息补充
        for (WorkOrderDTO workOrder : workOrderList) {
            JSONObject json = new JSONObject();
            json.put("id", workOrder.getId());
            json.put("workorderId", workOrder.getId());
            json.put("costId", workOrder.getCostId() == null ? "" : String.valueOf(workOrder.getCostId()));
            json.put("costName", workOrder.getCostName());
            json.put("time", DateUtil.transferDateToString(workOrder.getServiceTime()));
            json.put("createTime", DateUtil.dateToString(workOrder.getCreateTime()));
            json.put("province", workOrder.getProvince());
            json.put("city", workOrder.getCity());
            json.put("region", workOrder.getRegion());
            json.put("address", workOrder.getAddress());
            json.put("step", workOrder.getStep());
            if (workOrder.getPay() != null && workOrder.getPay()) {
                json.put("paystate", 3);//已支付
            } else {
                //支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败
                Integer payStatus = workOrder.getPayStatus();
                if (payStatus != null) {
                    if (payStatus == 1) {
                        json.put("paystate", 0);//未支付
                    } else if (payStatus == 2) {
                        json.put("paystate", 1);//审核中
                    } else if (payStatus == 4) {
                        json.put("paystate", 2);//未通过
                    } else if (payStatus == 3) {
                        json.put("paystate", 3);//已支付
                    }
                }
            }
            if (workOrder.getDistributorId() != null) {
                json.put("distributorId", String.valueOf(workOrder.getDistributorId()));
                json.put("distributor", workOrder.getDistributorName());
                json.put("disphone", workOrder.getDistributorPhone());
                if (null != workOrder.getSubDistributorId()) {
                    json.put("distributorId", String.valueOf(workOrder.getSubDistributorId()));
                    json.put("distributor", workOrder.getSubDistributorName());
                    json.put("disphone", workOrder.getSubDistributorPhone());
                }
            }
            //水机用户信息设置
            json.put("name", workOrder.getUserName());
            json.put("phone", workOrder.getUserPhone());
            if (null != workOrder.getUserId()) {
                WaterDeviceUserDTO deviceUser = userFeign.getWaterDeviceUserById(workOrder.getUserId());
                json.put("name", deviceUser.getRealName());
                json.put("phone", deviceUser.getPhone());
                json.put("degree", deviceUser.getDegree());
                json.put("userType", deviceUser.getType());
                json.put("mail", deviceUser.getEmail());
            }
            //如果user实体里面没有type,则默认为个人
            if (json.getInteger("userType") == null) {
                json.put("userType", 1);
            }
            json.put("deviceName", workOrder.getDeviceModel());
            json.put("deviceScope", YunOldIdUtil.getProductScope(workOrder.getDeviceModel()));
            json.put("money", workOrder.getFee().doubleValue());
            json.put("excuse", workOrder.getNotPassReason());
            if (workOrder.getCompleteType() != null) {
                json.put("completeType", workOrder.getCompleteType());
            }
            json.put("billing", workOrder.getInvoice());
            json.put("chargeback", workOrder.getChargeback());
            json.put("chargebackremark", workOrder.getChargebackRemark());
            json.put("chargebackreason", workOrder.getChargebackReason());
            json.put("discontinue", workOrder.getDiscontinue());
            json.put("discontinueremark", workOrder.getDiscontinueRemark());
            json.put("discontinuereason", workOrder.getDiscontinueReason());
            json.put("count", workOrder.getCount());
            json.put("remark", workOrder.getRemark());

            if (workOrder.getChargeback() != null && workOrder.getChargeback()) {//退单显示state=5 方便app
                json.put("state", 5);
            } else {
                json.put("state", workOrder.getStatus());
            }
            json.put("scope", workOrder.getUserScore());
            json.put("content", workOrder.getUserContent());
            if (workOrder.getOperationTime() != null) {
                json.put("operationTime", DateUtil.dateToString(workOrder.getOperationTime()));
            } else {
                json.put("operationTime", DateUtil.dateToString(workOrder.getCreateTime()));
            }
            String dgetDescribe = ScanCodeTypeEnum.getDgetDescribe(workOrder.getScanCodeType());
            if (dgetDescribe != null) {
                json.put("sourceStr", dgetDescribe);
            } else {
                WorkOrderTypeEnum byType = WorkOrderTypeEnum.getByType(String.valueOf(workOrder.getType()));
                json.put("sourceStr", byType != null ? byType.getTypeName() : null);
            }
            json.put("deviceScopeId", YunOldIdUtil.getProductScopeId(workOrder.getDeviceModel()));
            //TODO 为了应对多个产品获取不到对应的计费方式，所以这里去新系统的产品ID传给客户端
            // json.put("productModel", YunOldIdUtil.getProductModelId(workOrder.getDeviceModel()));
            json.put("productModel", workOrder.getProductId());
            json.put("productType", 1);//1-净水设备；2-健康食品；
            json.put("price", workOrder.getFee().subtract(workOrder.getOpenAccountFee()).doubleValue());
            voList.add(json);
        }
        // Collections.sort(voList, new Comparator<WorkOrderVO>() {
        //     public int compare(WorkOrderVO w1, WorkOrderVO w2) {
        //         return w2.getCreateTime().compareTo(w1.getCreateTime());
        //     }
        // });
        ru.put("list", voList);
        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 安装工APP-工单详情
     *
     * @param id       工单号
     * @param child
     * @param username 安装工账号
     * @param password 安装工密码
     */
    @GetMapping(value = "/api/workorder/details")
    @ApiOperation(value = "安装工APP-工单详情")
    public Map<String, Object> details(@RequestParam String id, @RequestParam(defaultValue = "0") int child, String username, String password) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        // 根据姓名获取安装工信息
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        }

        //ios老流程传入的密码是明文,需要加密处理
        if (null != engineer.getAppType() && engineer.getAppType() == 2) {
            password = MD5Util.encodeMD5(password);
        }
        // 校验密码
        if (!engineer.getPassword().equalsIgnoreCase(password)) {
            ResultUtil.error(ru, Constant.PASSWORD_ERROR_CODE, Constant.PASSWORD_ERROR_MSG);
            return ru;
        }
        WorkOrderDTO wo = orderFeign.getWorkOrderById(id);
        if (wo == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        if (!Objects.equals(engineer.getId(), wo.getEngineerId())) {
            ResultUtil.error(ru, "您无权限查看该工单");
            return ru;
        }
        JSONObject vo = new JSONObject();
        vo.put("workorderId", wo.getId());
        vo.put("id", wo.getId());
        if (wo.getChargeback() != null && wo.getChargeback()) {
            vo.put("state", 5);//退单显示state=5 用于app端显示
        } else {
            // if (wo.getStatus() == WorkOrderStatusEnum.REVIEW_FAILURE.value
            //         || wo.getStatus() == WorkOrderStatusEnum.WAITING_AUDIT.value
            //         || wo.getStatus() == WorkOrderStatusEnum.PAID.value
            //         || wo.getStatus() == WorkOrderStatusEnum.ASSIGNED.value
            //         || wo.getStatus() == WorkOrderStatusEnum.NEED_PAY.value
            //         || wo.getStatus() == WorkOrderStatusEnum.REFUSE.value) {
            //     vo.put("state", 1);//1 未受理,2 已受理,3 处理中,4 已完成
            // } else if (wo.getStatus() == WorkOrderStatusEnum.ACCEPTED.value) {
            //     vo.put("state", 2);
            // } else if (wo.getStatus() == WorkOrderStatusEnum.INSTALLING.value) {
            //     vo.put("state", 3);
            // } else if (wo.getStatus() == WorkOrderStatusEnum.COMPLETED.value) {
            //     vo.put("state", 4);
            // }
            vo.put("state", wo.getStatus());
        }
        //TODO 红包
        vo.put("code", wo.getId());
        vo.put("province", wo.getProvince());
        vo.put("city", wo.getCity());
        vo.put("region", wo.getRegion());
        vo.put("costId", wo.getCostId() == null ? "" : String.valueOf(wo.getCostId()));
        vo.put("costName", wo.getCostName());
        vo.put("price", wo.getFee().subtract(wo.getOpenAccountFee()).doubleValue());
        vo.put("address", wo.getAddress());
        vo.put("step", wo.getStep());
        if (wo.getPayStatus() != null) {
            if (wo.getPayStatus() == PayStatus.UN_PAY.value) {
                vo.put("paystate", 0);
            } else if (wo.getPayStatus() == PayStatus.WAITING_AUDIT.value) {
                vo.put("paystate", 1);
            } else if (wo.getPayStatus() == PayStatus.PAY.value) {
                vo.put("paystate", 3);
            } else if (wo.getPayStatus() == PayStatus.FAIL.value) {
                vo.put("paystate", 2);
            }
        }
        vo.put("distributor", wo.getDistributorName());
        vo.put("disphone", wo.getDistributorPhone());
        vo.put("distributorId", wo.getOldDistributorId());

        vo.put("customer", wo.getEngineerName());
        vo.put("cusphone", wo.getEngineerPhone());

        vo.put("name", wo.getUserName());
        vo.put("phone", wo.getUserPhone());

        if (wo.getEngineerId() == null) {
            vo.put("allot", 1);
        }

        vo.put("payterminal", wo.getPayTerminal());
        vo.put("deviceName", wo.getDeviceModel());
        vo.put("deviceScopeId", YunOldIdUtil.getProductScopeId(wo.getDeviceModel()));
        vo.put("deviceScope", YunOldIdUtil.getProductScope(wo.getDeviceModel()));

        vo.put("excuse", wo.getNotPassReason());
        vo.put("pass", wo.getChargebackStatus());
        vo.put("count", wo.getCount());
        vo.put("billing", wo.getInvoice());
        vo.put("completeType", wo.getCompleteType());
        vo.put("chargeback", wo.getChargeback());
        vo.put("chargebackremark", wo.getChargebackRemark());
        vo.put("chargebackreason", wo.getChargebackReason());
        vo.put("discontinue", wo.getDiscontinue());
        vo.put("discontinueremark", wo.getDiscontinueRemark());
        vo.put("discontinuereason", wo.getDiscontinueReason());
        vo.put("money", wo.getFee().doubleValue());
        vo.put("remark", wo.getRemark());
        vo.put("score", wo.getUserScore());
        if (wo.getUserScore() != null) {
            if (wo.getUserScore() > 0 && wo.getUserScore() < 60) {
                vo.put("scoreText", "差");
            } else if (wo.getUserScore() >= 60 && wo.getUserScore() < 80) {
                vo.put("scoreText", "一般");
            } else if (wo.getUserScore() >= 80 && wo.getUserScore() <= 100) {
                vo.put("scoreText", "好");
            }
        }
        vo.put("content", wo.getUserContent());
        // vo.put("addWorkOrderId", wo.getAddWorkOrderId());
        vo.put("addWorkOrderId", "");
        vo.put("time", DateUtil.dateToString(wo.getAcceptTime()));
        vo.put("createTime", DateUtil.dateToString(wo.getCreateTime()));

        if (wo.getSubDistributorId() != null) {
            if (child == 1) {
                vo.put("distributor", wo.getSubDistributorName());
                vo.put("disphone", wo.getSubDistributorPhone());
            }
        }
        vo.put("sourceStr", wo.getType() != null && wo.getType() == 1 ? "经销商添加" : "用户自助下单");
        ru.put("workorder", vo);
        return ru;
    }

    /**
     * 安装工APP-拒绝工单
     *
     * @param username 安装工账号
     * @param password 安装工密码
     * @param reason   拒单原因
     * @param id       工单号
     */
    @PostMapping(value = "/api/workorder/refuse")
    @ApiOperation(value = "安装工APP-拒绝工单")
    public Map<String, Object> refuse(@RequestParam String username,
                                      @RequestParam String password,
                                      @RequestParam String reason,
                                      @RequestParam String id) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        // 根据姓名获取安装工信息
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        }
        // 校验密码
        if (!engineer.getPassword().equalsIgnoreCase(password)) {
            ResultUtil.error(ru, Constant.PASSWORD_ERROR_CODE, Constant.PASSWORD_ERROR_MSG);
            return ru;
        }
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        if (workOrder.getChargeback()) {
            ResultUtil.error(ru, Constant.WORKORDER_CHARGEBACK_CODE, Constant.WORKORDER_CHARGEBACK_MSG);
        } else {
            //服务站正在上线新流程校验
            OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
            if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
                ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录。");
                return ru;
            }
            if (StatusEnum.isYes(workOrder.getNotRefuse())) {
                ResultUtil.error(ru, Constant.SYSTEM_ERROR_CODE, "工单不允许拒单");
                return ru;
            }
            try {
                orderFeign.refuseWorkOrder(id, engineer.getId(), engineer.getRealName(), reason);
            } catch (Exception e) {
                if (e instanceof YimaoException) {
                    ResultUtil.error(ru, Constant.SYSTEM_ERROR_CODE, e.getMessage());
                } else {
                    ResultUtil.error(ru, Constant.SYSTEM_ERROR_CODE, "安装工程师拒单失败");
                }
            }
        }
        return ru;
    }

    /**
     * 安装工APP-接单
     *
     * @param username 安装工账号
     * @param password 安装工密码
     * @param id       工单号
     */
    @PostMapping(value = "/api/workorder/accept")
    @ApiOperation(value = "安装工APP-接单")
    public Map<String, Object> accept(@RequestParam String username, @RequestParam String password, @RequestParam String id) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        // 根据姓名获取安装工信息
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        }
        // 校验密码
        if (!engineer.getPassword().equalsIgnoreCase(password)) {
            ResultUtil.error(ru, Constant.PASSWORD_ERROR_CODE, Constant.PASSWORD_ERROR_MSG);
            return ru;
        }
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        if (workOrder.getChargeback() != null && workOrder.getChargeback()) {
            ResultUtil.error(ru, Constant.WORKORDER_CHARGEBACK_CODE, Constant.WORKORDER_CHARGEBACK_MSG);
        } else {
            //服务站正在上线新流程校验
            OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
            if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
                ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录。");
                return ru;
            }
            try {
                orderFeign.acceptWorkOrder(id, engineer.getId(), null);
            } catch (Exception e) {
                if (e instanceof YimaoException) {
                    ResultUtil.error(ru, Constant.SYSTEM_ERROR_CODE, e.getMessage());
                } else {
                    ResultUtil.error(ru, Constant.SYSTEM_ERROR_CODE, "安装工程师接单失败");
                }
            }
        }
        return ru;
    }

    /**
     * 安装工APP-提货
     *
     * @param id 工单号
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PostMapping(value = "/api/workorder/pick")
    @ApiOperation(value = "安装工APP-提货")
    public Map<String, Object> pick(@RequestParam String id) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }

        String province = workOrder.getProvince();
        String city = workOrder.getCity();
        String region = workOrder.getRegion();
        Integer productId = workOrder.getProductId();
        Integer count = 0;
        Map<String, Object> map = new HashMap<>();
        map.put("province", province);
        map.put("city", city);
        map.put("region", region);
        map.put("productModel", workOrder.getDeviceModel());
        map.put("count", -1);
        Date now = new Date();

        //服务站正在上线新流程校验
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(province, city, region);
        if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
            ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录。");
            return ru;
        }

        // Integer distributorId = workOrder.getDistributorId();
        // // 根据姓名获取安装工信息
        // DistributorDTO distributor = userFeign.getDistributorById(distributorId);
        // if (distributor != null && distributor.getUserName().toLowerCase().contains("clxd") && DistributorRoleLevel.DISCOUNT.value == distributor.getRoleLevel()) {
        //     count = systemFeign.getStoreHouseCount(province, city, region, productId, 1);
        //     if (count == null) {
        //         ResultUtil.error(ru, Constant.NO_STOCK_CODE, Constant.NO_STOCK_MSG);//提示没有仓库
        //         return ru;
        //     }
        //     map.put("special", 1);
        // } else {
        //获取普通仓数量
        count = systemFeign.getStoreHouseCount(province, city, region, productId, 0);
        if (count == null) {
            ResultUtil.error(ru, Constant.NO_STOCK_CODE, Constant.NO_STOCK_MSG);//提示没有仓库
            return ru;
        }

        map.put("special", 0);
        // }

        if (count <= 0) {
            WorkOrderDTO update = new WorkOrderDTO();
            update.setId(id);
            update.setOperationTime(now);
            update.setStep(WorkOrderInstallStep.OUTSTOCK.value);
            orderFeign.updateWorkOrderPart(update);
            ResultUtil.error(ru, Constant.NO_STORE_CODE, Constant.NO_STORE_MSG);
            return ru;
        }

        //提货成功减库存
        rabbitTemplate.convertAndSend(RabbitConstant.INCREASE_OR_DECREASE_STOCK, map);

        WorkOrderDTO update = new WorkOrderDTO();
        update.setId(id);
        update.setOperationTime(now);
        update.setPickTime(now);
        update.setStep(WorkOrderInstallStep.PICK.value);
        orderFeign.updateWorkOrderPart(update);
        return ru;
    }

    /**
     * 安装工APP-开始服务
     *
     * @param id 工单号
     */
    @PostMapping(value = "/api/workorder/service")
    @ApiOperation(value = "安装工APP-开始服务")
    public Map<String, Object> service(@RequestParam String id) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        //服务站正在上线新流程校验
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
        if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
            ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录。");
            return ru;
        }
        Date now = new Date();
        WorkOrderDTO update = new WorkOrderDTO();
        update.setId(id);
        update.setOperationTime(now);
        update.setStatus(WorkOrderStatusEnum.INSTALLING.value);
        orderFeign.updateWorkOrderPart(update);
        return ru;
    }

    /**
     * 安装工APP-开始服务之输入协议编号
     *
     * @param id           工单号
     * @param protocol     协议编号
     * @param confirmation 确认单号
     */
    @PostMapping(value = "/api/workorder/service/protocol")
    @ApiOperation(value = "安装工APP-开始服务之输入协议编号")
    public Map<String, Object> protocol(@RequestParam String id, @RequestParam String protocol, @RequestParam String confirmation) {
        protocol = protocol.trim();
        confirmation = confirmation.trim();
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        //服务站正在上线新流程校验
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
        if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
            ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录。");
            return ru;
        }
        Date now = new Date();
        WorkOrderDTO update = new WorkOrderDTO();
        update.setId(id);
        update.setOperationTime(now);
        update.setStep(WorkOrderInstallStep.PROTOCOL.value);
        update.setProtocol(protocol);
        update.setConfirmation(confirmation);
        orderFeign.updateWorkOrderPart(update);
        return ru;
    }

    /**
     * 安装工APP-开始服务之扫描二维码
     *
     * @param id              工单号
     * @param sncode          SN码
     * @param logisticsCoding 物流编码
     */
    @PostMapping(value = "/api/workorder/service/sncode")
    @ApiOperation(value = "安装工APP-开始服务之扫描二维码")
    public Map<String, Object> sncode(@RequestParam String id, @RequestParam String sncode, @RequestParam String logisticsCoding) {
        sncode = sncode.trim();
        logisticsCoding = logisticsCoding.trim();
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        //物流编码唯一
        if (logisticsCoding == null || logisticsCoding.length() != 13 && logisticsCoding.length() != 16) {
            ResultUtil.error(ru, Constant.LOGISTICSCODING_ERROR_CODE, "物流编码长度只能为13为和16位");
            return ru;
        }
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        //服务站正在上线新流程校验
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
        if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
            ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录。");
            return ru;
        }
        //根据物流编码查询工单是否存在
        boolean exists = orderFeign.existsWorkOrderWithLogisticsCode(logisticsCoding);
        if (exists) {
            ResultUtil.error(ru, Constant.LOGISTICSCODING_ERROR_CODE, Constant.LOGISTICSCODING_ERROR_MSG);
            return ru;
        }
        String deviceModel = workOrder.getDeviceModel();
        try {
            //翼猫老系统提交批次码检验
            Map<String, Object> map = BaideApiUtil.validateBatchCode(sncode, logisticsCoding.trim(), deviceModel);
            if (map == null) {
                ResultUtil.error(ru, Constant.LOGISTICSCODING_ERROR_CODE, "售后系统批次码检验失败");
                return ru;
            }
            if (!"00000000".equals(map.get("code").toString())) {
                ResultUtil.error(ru, Constant.LOGISTICSCODING_ERROR_CODE, map.get("msg").toString());
                return ru;
            }
        } catch (Exception e) {
            ResultUtil.error(ru, Constant.LOGISTICSCODING_ERROR_CODE, "售后系统批次码检验异常");
            return ru;
        }
        if (!StringUtil.isEmpty(deviceModel) && !deviceModel.equals(sncode.substring(4, 9))) {
            ResultUtil.error(ru, "产品型号不符,无法提交!");
            return ru;
        }
        WaterDeviceDTO device = waterFeign.getWaterDeviceBySnCode(sncode);
        if (device != null) {
            ResultUtil.error(ru, Constant.SNCODE_EXIST_ERROR_CODE, Constant.SNCODE_EXIST_ERRPR_MSG);
            return ru;
        } else {

            try {
                //保存水机设备信息
                //device = waterFeign.createWaterDevice(device);
                //初始化水机设备信息
                workOrder.setSn(sncode);
                workOrder.setLogisticsCode(logisticsCoding);
                device = workOrderApi.createWaterDevice(workOrder);
            } catch (Exception e) {
                ResultUtil.error(ru, "程序遇到问题，请稍后重试。");
                return ru;
            }
            WorkOrderDTO update = new WorkOrderDTO();
            update.setId(id);
            update.setOperationTime(new Date());
            update.setStep(WorkOrderInstallStep.SNCODE.value);
            update.setSn(sncode);
            update.setDeviceId(device.getId());
            update.setSnCodeTime(device.getSnEntryTime());
            orderFeign.updateWorkOrderPart(update);
        }
        return ru;
    }

    /**
     * 安装工APP-开始服务之激活SIM卡
     *
     * @param id  工单号
     * @param sim sim卡iccid
     */
    @PostMapping(value = "/api/workorder/service/activeSim")
    @ApiOperation(value = "安装工APP-开始服务之激活SIM卡")
    public Map<String, Object> activeSim(@RequestParam String id, @RequestParam String sim) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        //服务站正在上线新流程校验
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
        if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
            ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录。");
            return ru;
        }
        //校验SIM卡号是否已被绑定
        WaterDeviceDTO device = waterFeign.getWaterDeviceByIccid(sim.trim());
        if (device != null) {
            ResultUtil.error(ru, Constant.SIMCARD_USED_CODE, Constant.SIMCARD_USED_MSG);
            return ru;
        }
        device = waterFeign.getWaterDeviceById(workOrder.getDeviceId());
        if (device != null) {
            waterFeign.activatingSimCard(device.getId(), sim.trim());
        }
        Date now = new Date();
        WorkOrderDTO update = new WorkOrderDTO();
        update.setId(id);
        update.setOperationTime(now);
        update.setSimCard(sim);
        update.setSimCardTime(now);
        if (workOrder.getPay() != null && workOrder.getPay()) {
            update.setStep(WorkOrderInstallStep.PAID.value);
            update.setPayStatus(PayStatus.PAY.value);
        } else {
            update.setStep(WorkOrderInstallStep.SIMCARD.value);
        }
        orderFeign.updateWorkOrderPart(update);
        return ru;
    }

    /**
     * 安装工APP-编辑用户信息
     *
     * @param id 工单号
     */
    @PostMapping(value = "/api/workorder/service/edituser")
    @ApiOperation(value = "安装工APP-编辑用户信息")
    public Map<String, Object> edituser(@RequestParam String id,
                                        @RequestParam(required = false) String degree,
                                        @RequestParam(required = false, defaultValue = "0") Integer count,
                                        @RequestParam(required = false) String company,
                                        @RequestParam(required = false) String legal,
                                        @RequestParam(required = false) String industry,
                                        @RequestParam(required = false) String dimensions,
                                        @RequestParam(required = false, defaultValue = "") String image,
                                        @RequestParam(required = false) String hobby,
                                        @RequestParam(required = false, defaultValue = "0") Integer childAge,
                                        @RequestParam(required = false) String childSex,
                                        @RequestParam(required = false) Boolean haveChild,
                                        @RequestParam(required = false) Boolean haveOld,
                                        @RequestParam(required = false) Boolean marry,
                                        @RequestParam(required = false) Boolean studyAbroad,
                                        @RequestParam(required = false, defaultValue = "") String mail,
                                        @RequestParam(required = false, defaultValue = "1") int userType) {
        Map<String, Object> result = new HashMap<>();
        ResultUtil.success(result);
        WorkOrderDTO update = new WorkOrderDTO();
        update.setId(id);
        update.setOperationTime(new Date());
        update.setStep(WorkOrderInstallStep.MSG.value);
        orderFeign.updateWorkOrderPart(update);
        //TODO 同步工单信息
        return result;
    }

    /**
     * 安装工APP-开发票
     *
     * @param id 工单号
     */
    @PostMapping(value = "/api/workorder/service/bill")
    @ApiOperation(value = "开发票（原云平台）")
    public Map<String, Object> bill(@RequestParam String id,
                                    @RequestParam(required = false, defaultValue = "1") Integer billtype,
                                    @RequestParam(required = false, defaultValue = "") String title,
                                    @RequestParam(required = false, defaultValue = "0") Integer invoice,
                                    @RequestParam(required = false, defaultValue = "") String taxreceiptNum,
                                    @RequestParam(required = false, defaultValue = "") String bank,
                                    @RequestParam(required = false, defaultValue = "") String bankNum,
                                    @RequestParam(required = false, defaultValue = "") String billaddress,
                                    @RequestParam(required = false, defaultValue = "") String billphone,
                                    @RequestParam boolean isBilling,
                                    @RequestParam(required = false, defaultValue = "") String email,
                                    @RequestParam(required = false, defaultValue = "") String... licence) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        try {
            //服务站正在上线新流程校验
            OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
            if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
                ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录。");
                return ru;
            }
            Date now = new Date();
            WorkOrderDTO update = new WorkOrderDTO();
            update.setId(id);
            if (isBilling) {
                update.setInvoice(true);
                update.setInvoiceType(invoice);
                update.setInvoiceHeaderType(billtype);
                update.setInvoiceTitle(title);
                update.setInvoiceTaxNum(taxreceiptNum);
                update.setInvoiceBank(bank);//开户行
                update.setInvoiceBankNum(bankNum);//开户号
                update.setInvoiceAddress(billaddress); //开票地址
                update.setInvoicePhone(billphone); //开票手机号
                update.setInvoiceEmail(email);//开票邮箱
                //开票
                insertInvoice(billphone, email, taxreceiptNum, billaddress, billtype, invoice, bank, bankNum, workOrder, title, update, 2);
            } else {
                update.setInvoice(false);
            }
            update.setInvoiceTime(now);
            update.setOperationTime(now);
            update.setStep(WorkOrderInstallStep.BILL.value);
            orderFeign.updateWorkOrderPart(update);
            return ru;
        } catch (Exception e) {
            log.error("老流程开发票发生错误，" + e.getMessage(), e);
            ResultUtil.error(ru, Constant.SYSTEM_ERROR_CODE, Constant.SYSTEM_ERROR_MSG);
            return ru;
        }
    }

    private void insertInvoice(String billphone, String email, String taxreceiptNum, String billaddress, Integer billtype, Integer invoice, String bank, String bankNum, WorkOrderDTO workOrder, String title, WorkOrderDTO update, Integer applyStatus) {
        //开票
        OrderInvoiceDTO orderInvoice = new OrderInvoiceDTO();
        orderInvoice.setMainOrderId(workOrder.getMainOrderId());
        orderInvoice.setOrderId(workOrder.getSubOrderId());
        orderInvoice.setUserId(workOrder.getUserId());
        orderInvoice.setProductId(workOrder.getProductId());
        orderInvoice.setProductName(workOrder.getProductName());
        orderInvoice.setApplyStatus(applyStatus);//开票状态：0-未申请  2-已申请
        if (applyStatus == 2) {
            orderInvoice.setApplyTime(new Date());
            orderInvoice.setInvoiceType(invoice);
            orderInvoice.setInvoiceHead(billtype);//发票抬头1、公司发票；2、个人发票
            orderInvoice.setBankAccount(bankNum);//开户号
            orderInvoice.setBankName(bank);//开户行
            orderInvoice.setDutyNo(taxreceiptNum);//税号
            orderInvoice.setApplyAddress(billaddress);//开票地址
            orderInvoice.setCompanyPhone(billphone); //电话
            if (billtype == 1) { //发票抬头 1-公司
                orderInvoice.setCompanyName(title); //公司名称
                orderInvoice.setCompanyAddress(billaddress); //公司地址
            }
            orderInvoice.setApplyUser(workOrder.getUserName());
            orderInvoice.setApplyPhone(workOrder.getUserPhone());//申请用户手机号
            if (StringUtil.isEmpty(email)) {
                StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion(),StationAreaServiceTypeEnum.AFTER_SALE.value);
                if (stationCompany != null) {
                    //如果收件人邮箱为空，则获取服务站公司的邮箱
                    email = stationCompany.getEmail();
                }
            } else {
                // 邮箱验证规则
                String regEx = "[a-zA-Z_]{0,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}";
                // 编译正则表达式
                Pattern pattern = Pattern.compile(regEx);
                // 忽略大小写的写法
                // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(email);
                // 字符串是否与正则表达式相匹配
                boolean flag = matcher.matches();
                if (!flag) {
                    log.info("Email格式不正确，用服务站公司的邮箱代替用户输入的Email");
                    StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion(),StationAreaServiceTypeEnum.AFTER_SALE.value);
                    if (stationCompany != null) {
                        //如果收件人邮箱为空，则获取服务站公司的邮箱
                        email = stationCompany.getEmail();
                    }
                }
            }
            orderInvoice.setApplyEmail(email);
            update.setInvoiceEmail(email);
        }
        orderInvoice.setType(1); //订单类型：1-普通订单  2-续费订单
        // orderInvoice.setRenewId(); //续费单号
        orderInvoice.setSourceType(1);  //来源：1-安装工 app;2-健康e家公众号
        //创建发票信息
        rabbitTemplate.convertAndSend(RabbitConstant.INSERT_WORK_ORDER_INVOICE, orderInvoice);
    }

    /**
     * 安装工APP-中止工单
     *
     * @param id 工单号
     */
    @PostMapping(value = "/api/workorder/service/discontinue")
    public Map<String, Object> discontinue(@RequestParam String id,
                                           @RequestParam String reason,
                                           @RequestParam String remark,
                                           @RequestParam int reasonNum) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        //服务站正在上线新流程校验
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
        if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
            ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录。");
            return ru;
        }
        try {
            orderFeign.discontinueWorkOrder(id, reason, remark, reasonNum);
        } catch (Exception e) {
            log.error("老流程中止工单发生错误，" + e.getMessage(), e);
            if (e instanceof YimaoException) {
                ResultUtil.error(ru, Constant.SYSTEM_ERROR_CODE, e.getMessage());
            } else {
                ResultUtil.error(ru, Constant.SYSTEM_ERROR_CODE, Constant.SYSTEM_ERROR_MSG);
            }
        }
        return ru;
    }

    /**
     * 安装工APP-更换设备
     *
     * @param id 工单号
     */
    @PostMapping(value = "/api/workorder/service/changedevice")
    @ApiOperation(value = "安装工APP-更换设备")
    public Map<String, Object> changedevice(@RequestParam String id) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        //服务站正在上线新流程校验
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
        if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
            ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录。");
            return ru;
        }
        try {
            orderFeign.changedeviceWorkOrder(id);
        } catch (Exception e) {
            log.error("老流程更换设备发生错误，" + e.getMessage(), e);
            ResultUtil.error(ru, Constant.SYSTEM_ERROR_CODE, Constant.SYSTEM_ERROR_MSG);
        }
        return ru;
    }

    /**
     * 安装工APP-继续服务
     *
     * @param id 工单号
     */
    @PostMapping(value = "/api/workorder/service/continueworkorder")
    @ApiOperation(value = "安装工APP-继续服务")
    public Map<String, Object> continueworkorder(@RequestParam String id) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        //服务站正在上线新流程校验
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
        if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
            ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录。");
            return ru;
        }
        try {
            orderFeign.continueWorkOrder(id);
        } catch (Exception e) {
            log.error("老流程继续服务发生错误，" + e.getMessage(), e);
            ResultUtil.error(ru, Constant.SYSTEM_ERROR_CODE, Constant.SYSTEM_ERROR_MSG);
        }
        return ru;
    }

    /**
     * 安装工APP-退单
     *
     * @param id 工单号
     */
    @PostMapping(value = "/api/workorder/service/chargeback")
    @ApiOperation(value = "安装工APP-退单")
    public Map<String, Object> chargeback(@RequestParam String id,
                                          @RequestParam String reason,
                                          @RequestParam String remark,
                                          @RequestParam int reasonNum) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        //服务站正在上线新流程校验
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
        if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
            ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录。");
            return ru;
        }
        try {
            orderFeign.chargebackWorkOrder(id, reason, remark, reasonNum);
        } catch (Exception e) {
            log.error("老流程退单发生错误，" + e.getMessage(), e);
            ResultUtil.error(ru, Constant.SYSTEM_ERROR_CODE, Constant.SYSTEM_ERROR_MSG);
        }
        return ru;
    }

    /**
     * 安装工APP-完成工单
     *
     * @param id 工单号
     */
    @PostMapping(value = "/api/workorder/complete")
    @ApiOperation(value = "安装工APP-完成工单")
    public Map<String, Object> chargeback(@RequestParam String id) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        try {
            orderFeign.completeWorkOrder(id);
        } catch (Exception e) {
            log.error("老流程完成工单发生错误，" + e.getMessage(), e);
            ResultUtil.error(ru, Constant.SYSTEM_ERROR_CODE, Constant.SYSTEM_ERROR_MSG);
        }
        return ru;
    }

    /**
     * 未受理的工单数
     */
    @PostMapping(value = "/api/customer/notaccepted")
    @ResponseBody()
    public Map<String, Object> customernotaccepted(@RequestParam String username,//客服
                                                   @RequestParam String password) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        }
        if (!Objects.equals(engineer.getPassword(), password)) {
            ResultUtil.error(ru, "14", "密码输入错误。");
            return ru;
        }
        //1-工单已分配待接单（包含手动分配和系统分配）
        int assigned = orderFeign.countWorkOrderByEngineerId(engineer.getId(), WorkOrderStatusEnum.ASSIGNED.value);
        ru.put("notaccepted", assigned);
        return ru;
    }

    /**
     * 安装工获取退单原因列表（原云平台）
     */
    @GetMapping(value = "/api/workorder/reason/list")
    @ApiOperation(value = "安装工获取退单原因列表（原云平台）")
    public Map<String, Object> reason() {
        Map<String, Object> ru = new HashMap<>();
        // Query query = new Query();
        // List<Reason> list = mongoTemplate.find(query, Reason.class, "reason");
        List<ReasonDTO> list = systemFeign.listReason();
        List<JSONObject> voList = new ArrayList<>();
        for (ReasonDTO reason : list) {
            JSONObject json = new JSONObject();
            json.put("reason", reason.getReason());
            json.put("reasonNum", reason.getReasonNum());
            json.put("type", reason.getType());
            voList.add(json);
        }
        ru.put("list", voList);
        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 安装工获取未接单、已接单、安装中、安装完成、合计的工单数量（原云平台）
     *
     * @param username 安装工用户名
     * @param password 安装工密码
     */
    @PostMapping(value = "/api/customer/count")
    @ApiOperation(value = "安装工获取未接单、已接单、安装中、安装完成、合计的工单数量（原云平台）")
    public Map<String, Object> countEngineerWorkOrder(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        } else if (!Objects.equals(engineer.getPassword(), password)) {
            ResultUtil.error(ru, Constant.PASSWORD_ERROR_CODE, Constant.PASSWORD_ERROR_MSG);
            return ru;
        } else {
            Integer engineerId = engineer.getId();
            //1-工单已分配待接单（包含手动分配和系统分配）
            int assigned = orderFeign.countWorkOrderByEngineerId(engineerId, WorkOrderStatusEnum.ASSIGNED.value);
            //2-安装工接单
            int accepted = orderFeign.countWorkOrderByEngineerId(engineerId, WorkOrderStatusEnum.ACCEPTED.value);
            //3-安装中
            int installing = orderFeign.countWorkOrderByEngineerId(engineerId, WorkOrderStatusEnum.INSTALLING.value);
            //4-安装完成
            int completed = orderFeign.countWorkOrderByEngineerId(engineerId, WorkOrderStatusEnum.COMPLETED.value);
            //合计
            int total = orderFeign.countWorkOrderByEngineerId(engineerId, null);
            ru.put("notaccepted", assigned);
            ru.put("accepted", accepted);
            ru.put("serving", installing);
            ru.put("complete", completed);
            ru.put("total", total);
        }
        return ru;
    }

    /**
     * 安装工APP首页轮播图（原云平台）
     */
    @GetMapping(value = "/api/image/list")
    @ApiOperation(value = "安装工APP首页轮播图（原云平台）")
    public ResponseEntity<Object> image() {
        Map<String, Object> ru = new HashMap<>();
        List<BannerDTO> list = cmsFeign.getEngineerAppImage();
        List<JSONObject> voList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            for (BannerDTO image : list) {
                JSONObject json = new JSONObject();
                json.put("imageName", image.getAdImg());
                json.put("url", image.getUrl());
                voList.add(json);
            }
            ru.put("list", voList);
        }
        ResultUtil.success(ru);
        return ResponseEntity.ok(ru);
    }

    /**
     * 安装工APP获取操作指南（原云平台）
     */
    @GetMapping(value = "/api/operation")
    @ApiOperation(value = "安装工APP获取操作指南（原云平台）")
    public ResponseEntity<Object> operation(@RequestParam String type) {
        Map<String, Object> ru = new HashMap<>();
        // Query query = new Query();
        // query.addCriteria(Criteria.where("type").is(type));
        // OperationNote note = mongoTemplate.findOne(query, OperationNote.class, "operationnote");
        // ru.put("url", "https://yun.yimaokeji.com/operationNote/detail/" + note.getId());
        ru.put("url", "https://yun.yimaokeji.com/operationNote/detail/1");
        ResultUtil.success(ru);
        return ResponseEntity.ok(ru);
    }

    /**
     * 安装工APP公司公告（原云平台）
     */
    @GetMapping(value = "/api/notice/list")
    @ApiOperation(value = "安装工APP公司公告（原云平台）")
    public ResponseEntity<Object> notice(@RequestParam String type) {
        Map<String, Object> ru = new HashMap<>();
        // Query query = new Query();
        // query.addCriteria(Criteria.where("type").is(type));
        // List<Notice> list = mongoTemplate.find(query, Notice.class, "notice");
        // List<JSONObject> voList = new ArrayList<>();
        // for (Notice notice : list) {
        //     JSONObject json = new JSONObject();
        //     json.put("title", notice.getTitle());
        //     json.put("url", "https://yun.yimaokeji.com/notice/detail/" + notice.getId());
        //     voList.add(json);
        // }
        // ru.put("list", voList);
        ResultUtil.success(ru);
        return ResponseEntity.ok(ru);
    }

    // /**
    //  * 安装工获取版本信息（原云平台）
    //  */
    // @GetMapping(value = "/api/version/app/getVersion")
    // @ApiOperation(value = "安装工获取版本信息（原云平台）")
    // public ResponseEntity<Object> getVersion(@RequestParam String version, @RequestParam String systemType, @RequestParam String appType) {
    //     Map<String, Object> ru = new HashMap<>();
    //     Query query = new Query();
    //     query.addCriteria(Criteria.where("systemType").is(systemType));
    //     query.addCriteria(Criteria.where("appType").is(appType));
    //     Sort sort = new Sort(Sort.Direction.DESC, "version");
    //     query.with(sort);
    //     List<NewAppVersion> list = mongoTemplate.find(query, NewAppVersion.class, "newappversion");
    //     if (list.size() > 0) {
    //         NewAppVersion newAppVersion = list.get(0);
    //         if (newAppVersion.getVersion() > Integer.parseInt(version)) {
    //             ru.put("version", newAppVersion.getVersion());
    //             ru.put("versionName", newAppVersion.getVersionName());
    //             ru.put("versionDesc", newAppVersion.getVersionDesc());
    //             ru.put("popout", newAppVersion.getPopout());
    //             ru.put("forceUpdate", newAppVersion.getForceUpdate());
    //             ru.put("outLink", newAppVersion.getOutLink());
    //         }
    //     }
    //     ResultUtil.success(ru);
    //     return ResponseEntity.ok(ru);
    // }

    /**
     * 安装工APP服务中查询（原云平台）
     */
    @GetMapping(value = "/api/servicesite/search")
    @ApiOperation(value = "安装工APP服务中查询（原云平台）")
    public ResponseEntity<Object> servicesite(@RequestParam(required = false) String province, @RequestParam(required = false) String city,
                                              @RequestParam(required = false) String region, @RequestParam(required = false) String name) {
        Map<String, Object> ru = new HashMap<>();
        List<StationDTO> stationList = systemFeign.getStationByPCR(province, city, region, name);
        if (StringUtil.isEmpty(region) && StringUtil.isEmpty(name)) {
            stationList.clear();
        }
        List<JSONObject> voList = new ArrayList<>();
        for (StationDTO station : stationList) {
            JSONObject json = new JSONObject();
            json.put("name", station.getName());
            json.put("person", station.getContact());
            json.put("phone", station.getContactPhone());
            json.put("address", station.getAddress());
            voList.add(json);
        }
        ru.put("list", voList);
        ResultUtil.success(ru);
        return ResponseEntity.ok(ru);
    }

    /**
     * 安装工APP查询设备列表（原云平台）
     */
    @GetMapping(value = "/api/device/list")
    @ApiOperation(value = "安装工APP查询设备列表（原云平台）")
    public ResponseEntity<Object> listDevice(@RequestParam String username,
                                             @RequestParam String password,
                                             @RequestParam String search,
                                             @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                             @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Map<String, Object> ru = new HashMap<>();
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null || !password.equalsIgnoreCase(engineer.getPassword())) {
            ResultUtil.error(ru, "14", "密码输入错误");
            return ResponseEntity.ok(ru);
        }

        PageVO<WaterDeviceDTO> page = waterFeign.pageDeviceForEngineerApp(pageNo, pageSize, engineer.getId(), search);
        List<JSONObject> voList = new ArrayList<>();
        long count = page.getTotal();
        List<WaterDeviceDTO> list = page.getResult();
        if (list != null && list.size() > 0) {
            for (WaterDeviceDTO device : list) {
                JSONObject json = new JSONObject();
                json.put("id", device.getId());
                json.put("sncode", device.getSn());
                json.put("simcard", device.getIccid());
                json.put("batchCode", device.getLogisticsCode());
                json.put("netStatus", InternetStatusEnum.OFFLINE.getIndex());
                json.put("netStatusText", InternetStatusEnum.OFFLINE.getName());
                Date lastOnlineTime = device.getLastOnlineTime();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, -125);//125分钟之前的时间
                if (lastOnlineTime != null && lastOnlineTime.after(cal.getTime())) {
                    json.put("netStatus", InternetStatusEnum.ONLINE.getIndex());
                    json.put("netStatusText", InternetStatusEnum.ONLINE.getName());
                }
                voList.add(json);
            }
        }
        ru.put("data", voList);
        ru.put("count", count);
        ResultUtil.success(ru);
        return ResponseEntity.ok(ru);
    }

    /**
     * 安装工APP查询设备详情（原云平台）
     */
    @GetMapping(value = "/api/device/detail")
    @ApiOperation(value = "安装工APP查询设备详情（原云平台）")
    public ResponseEntity<Object> listDevice(@RequestParam String username, @RequestParam String password, @RequestParam String id) {
        Map<String, Object> ru = new HashMap<>();
        if (StringUtil.isEmpty(id)) {
            ResultUtil.error(ru, "39", "非法请求参数");
            return ResponseEntity.ok(ru);
        }
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null || !password.equalsIgnoreCase(engineer.getPassword())) {
            ResultUtil.error(ru, "14", "密码输入错误");
            return ResponseEntity.ok(ru);
        } else {
            WaterDeviceDTO device = waterFeign.getWaterDeviceById(Integer.parseInt(id));
            JSONObject json = new JSONObject();
            if (device != null) {
                json.put("id", device.getId());
                json.put("sncode", device.getSn());
                json.put("simcard", device.getIccid());
                json.put("batchCode", device.getLogisticsCode());
                json.put("netStatus", InternetStatusEnum.OFFLINE.getIndex());
                json.put("netStatusText", InternetStatusEnum.OFFLINE.getName());
                Date lastOnlineTime = device.getLastOnlineTime();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, -125);//125分钟之前的时间
                if (lastOnlineTime != null && lastOnlineTime.after(cal.getTime())) {
                    json.put("netStatus", InternetStatusEnum.ONLINE.getIndex());
                    json.put("netStatusText", InternetStatusEnum.ONLINE.getName());
                }
                json.put("disName", device.getDistributorName());
                json.put("disPhone", device.getDistributorPhone());
                json.put("userName", device.getDeviceUserName());
                json.put("userPhone", device.getDeviceUserPhone());
                long days = 0L;
                long hours = 0L;
                long minutes = 0L;
                Date activeTime = device.getSimActivatingTime();
                if (activeTime != null) {
                    Long currentTimeStamp = System.currentTimeMillis() - (activeTime == null ? 0L : activeTime.getTime());
                    days = currentTimeStamp / (1000 * 60 * 60 * 24);
                    hours = (currentTimeStamp % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                    minutes = (currentTimeStamp % (1000 * 60 * 60)) / (1000 * 60);
                }
                json.put("usedTime", days + "天" + hours + "时" + minutes + "分");
                json.put("usedflow", device.getCurrentTotalFlow() == null ? 0 : device.getCurrentTotalFlow());
                json.put("money", device.getMoney());
                json.put("netType", ConnectionTypeEnum.getNameByValue(device.getConnectionType()));
            }
            ru.put("data", json);
            ResultUtil.success(ru);
            return ResponseEntity.ok(ru);
        }
    }

    /**
     * 安装工APP获取水机设备动态密码（原云平台）
     */
    @PostMapping(value = "/api/device/getDynamicCipher")
    @ApiOperation(value = "安装工APP获取水机设备动态密码（原云平台）")
    public Map<String, Object> getDynamicCipher(@RequestParam String username, @RequestParam String password, @RequestParam String sncode) {
        log.info("客服: " + username + " 输入设备二维码获取动态密码: " + sncode);
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null || !password.equalsIgnoreCase(engineer.getPassword())) {
            ResultUtil.error(ru, "14", "密码输入错误");
            return ru;
        }
        if (StringUtil.isEmpty(sncode)) {
            ResultUtil.error(ru, "61", "参数不得为空");
            return ru;
        }
        WaterDeviceDTO device = waterFeign.getWaterDeviceBySnCode(sncode);
        if (device == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        Integer deviceEngineerId = device.getEngineerId();
        if (deviceEngineerId == null) {
            ResultUtil.error(ru, "62", "设备服务的工程师数据异常");
            return ru;
        }
        WaterDeviceDynamicCipherRecordDTO record = waterFeign.getDynamicCipherRecordBySnCode(sncode);
        if (record != null && !Objects.equals(record.getEngineerId(), engineer.getId())) {
            ResultUtil.error(ru, "当前时间段不允许其他客服扫描!");
            return ru;
        } else if (record != null && Objects.equals(record.getEngineerId(), engineer.getId())) {
            JSONObject json = new JSONObject();
            json.put("pwd", record.getPassword());
            ru.put("data", json);
            return ru;
        }
        WaterDeviceDynamicCipherConfigDTO config = waterFeign.getDynamicCipherConfig();
        if (config == null) {
            ResultUtil.error(ru, "63", "配置项不存在!请联系管理员处理");
            return ru;
        }
        if (DeviceScanEnum.OWNER.getDeviceScope() == config.getTypeCode() && !Objects.equals(engineer.getId(), deviceEngineerId)) {
            ResultUtil.error(ru, "64", "您没有权限操作");
            return ru;
        }
        record = new WaterDeviceDynamicCipherRecordDTO();
        record.setEngineerId(engineer.getId());
        record.setEngineerName(engineer.getRealName());
        record.setTerminal("engineer");
        record.setSn(sncode);
        //if (StringUtil.isEmpty(password)) {
            password = new Random().nextInt(899999) + 100000 + "";
        // }

        record.setPassword(password);
        record.setPasswordDesStr(DESUtil.encrypt(password, DESUtil.KEY));
        record.setValidStatus(StatusEnum.YES.value());
        Calendar cal = Calendar.getInstance();
        record.setCreateTime(cal.getTime());
        cal.add(Calendar.MINUTE, config.getValidMinute());
        record.setValidTime(cal.getTime());
        record = waterFeign.createDynamicCipherRecord(record);
        if (record == null || record.getId() == null) {
            ResultUtil.error(ru, "00", "系统异常");
            return ru;
        }
        JSONObject json = new JSONObject();
        json.put("pwd", record.getPassword());
        ru.put("data", json);
        return ru;
    }

    /**
     * 安装工APP扫描水机设备二维码（原云平台）
     */
    @PostMapping(value = "/api/device/analysisDeviceQrCode")
    @ApiOperation(value = "安装工APP扫描水机设备二维码（原云平台）")
    public Map<String, Object> analysisDeviceQrCode(@RequestParam String username, @RequestParam String password, @RequestParam String cipherText) {
        log.info("客服: " + username + " 扫描了设备二维码, 密文: " + cipherText);
        Map<String, Object> ru = new HashMap<>();
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null || !password.equalsIgnoreCase(engineer.getPassword())) {
            ResultUtil.error(ru, "14", "密码输入错误");
            return ru;
        }
        if (StringUtil.isEmpty(cipherText)) {
            ResultUtil.error(ru, "61", "参数不得为空");
            return ru;
        }

        String cipherStr;
        try {
            cipherStr = DESUtil.decryptByEngineerApp(cipherText, DESUtil.KEY);
        } catch (Exception var13) {
            ResultUtil.error(ru, "71", Constant.QRCODE_ILLEGALITY_MSG);
            return ru;
        }

        if (StringUtil.isEmpty(cipherStr)) {
            ResultUtil.error(ru, "71", Constant.QRCODE_ILLEGALITY_MSG);
            return ru;
        }
        if (!cipherStr.contains("&")) {
            JSONObject json = new JSONObject();
            json.put("pwd", cipherStr);
            ru.put("data", json);
            ResultUtil.success(ru);
            return ru;
        }

        String[] cipherArr = cipherStr.split("&");
        String sncode = cipherArr[0];
        String cipher = cipherArr[1];

        WaterDeviceDynamicCipherConfigDTO config = waterFeign.getDynamicCipherConfig();
        if (config == null) {
            ResultUtil.error(ru, "63", "配置项不存在!请联系管理员处理");
            return ru;
        }
        if (DeviceScanEnum.OWNER.getDeviceScope() == config.getTypeCode()) {
            WaterDeviceDTO device = waterFeign.getWaterDeviceBySnCode(sncode);
            if (device == null) {
                ResultUtil.error(ru, "19", "sn码不存在");
                return ru;
            }
            Integer deviceEngineerId = device.getEngineerId();
            if (deviceEngineerId == null) {
                ResultUtil.error(ru, "62", "设备服务的工程师数据异常");
                return ru;
            }
            if (!Objects.equals(engineer.getId(), deviceEngineerId)) {
                ResultUtil.error(ru, "64", "您没有权限操作");
                return ru;
            }
        }

        WaterDeviceDynamicCipherRecordDTO record = waterFeign.getDynamicCipherRecordBySnCode(sncode);
        if (record != null) {
            if (!Objects.equals(record.getEngineerId(), engineer.getId())) {
                WaterDeviceDynamicCipherRecordDTO update = new WaterDeviceDynamicCipherRecordDTO();
                update.setId(record.getId());
                update.setValidStatus(StatusEnum.NO.value());
                waterFeign.updateDynamicCipherRecord(update);

                record.setId(null);
                record.setEngineerId(engineer.getId());
                record.setEngineerName(engineer.getRealName());
                record.setTerminal("engineer");
                record.setCreateTime(new Date());
                record = waterFeign.createDynamicCipherRecord(record);
            }
            if (Objects.equals(record.getPassword(), cipher)) {
                JSONObject json = new JSONObject();
                json.put("pwd", record.getPassword());
                ru.put("data", json);
                ResultUtil.success(ru);
                return ru;
            }
            //还在有效期内的密码全部置为无效
            waterFeign.setDeviceAllDynamicCipherInValid(sncode);
        }
        WaterDeviceDynamicCipherRecordDTO recordDTO = new WaterDeviceDynamicCipherRecordDTO();
        recordDTO.setEngineerId(engineer.getId());
        recordDTO.setEngineerName(engineer.getRealName());
        recordDTO.setTerminal("engineer");
        recordDTO.setSn(sncode);
        recordDTO.setPassword(cipher);
        recordDTO.setPasswordDesStr(DESUtil.encrypt(cipher, "yimao018"));
        recordDTO.setValidStatus(StatusEnum.YES.value());
        Calendar cal = Calendar.getInstance();
        recordDTO.setCreateTime(cal.getTime());
        cal.add(Calendar.MINUTE, config.getValidMinute());
        recordDTO.setValidTime(cal.getTime());
        waterFeign.createDynamicCipherRecord(recordDTO);
        JSONObject json = new JSONObject();
        json.put("pwd", cipher);
        ru.put("data", json);
        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 安装工APP获取水机设备动态密码列表（原云平台）
     */
    @PostMapping(value = "/api/device/getDynamicCipherList")
    @ApiOperation(value = "安装工APP获取水机设备动态密码列表（原云平台）")
    public ResponseEntity<Object> getDynamicCipherList(@RequestParam String username, @RequestParam String password,
                                                       @RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        Map<String, Object> ru = new HashMap<>();
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize <= 0) {
            pageSize = 10;
        }
        if (pageSize >= 100) {
            pageSize = 10;
        }
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null || !password.equalsIgnoreCase(engineer.getPassword())) {
            ResultUtil.error(ru, "14", "密码输入错误");
            return ResponseEntity.ok(ru);
        }
        List<WaterDeviceDynamicCipherRecordDTO> list = waterFeign.pageDynamicCipherRecord(pageNo, pageSize, engineer.getId());
        List<JSONObject> voList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            for (WaterDeviceDynamicCipherRecordDTO dto : list) {
                JSONObject json = new JSONObject();
                json.put("pwd", dto.getPassword());
                json.put("validTime", DateUtil.transferDateToString(dto.getValidTime(), "yyyy-MM-dd HH:mm:ss"));
                json.put("sncode", dto.getSn());
                voList.add(json);
            }
        }
        ru.put("data", voList);
        ResultUtil.success(ru);
        return ResponseEntity.ok(ru);
    }

    /**
     * 安装工获取设备类型列表（原云平台）
     */
    @GetMapping(value = "/api/workorder/deviceModel")
    public Map<String, Object> deviceModel(Integer product) {
        Map<String, Object> ru = new HashMap<>();
        //兼容之前的接口 如果product不传默认1 就是水机
        // product = null != product ? product : 1;
        List<JSONObject> list = new ArrayList<>();
        JSONObject json = new JSONObject();
        json.put("id", "56f4fa3922d91e04568b58b1");
        json.put("type", "1601T");
        json.put("name", "家用");
        list.add(json);
        json = new JSONObject();
        json.put("id", "58c004266c8c1f2c148786f1");
        json.put("type", "1602T");
        json.put("name", "家用");
        list.add(json);
        json = new JSONObject();
        json.put("id", "58c004566c8c1f2c148786f3");
        json.put("type", "1603T");
        json.put("name", "家用");
        list.add(json);
        json = new JSONObject();
        json.put("id", "5715caeb22d91e5b4b336dd3");
        json.put("type", "1601L");
        json.put("name", "商用");
        list.add(json);
        ru.put("list", list);
        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 客服位置
     */
    @PostMapping(value = "/api/customer/position")
    public Map<String, Object> position(@RequestParam String username,//客服
                                        @RequestParam String longitude, @RequestParam String latitude) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, "13", "用户名不存在。");
            return ru;
        }
        log.info("安装工APP上传位置信息为longitude={}，latitude={}", longitude, latitude);
        return ru;
    }

    /**
     * 原云平台接口-其他支付方式获取
     */
    @GetMapping(value = "/api/workorder/otherpaidType")
    @ResponseBody
    @ApiOperation(value = "其他支付方式获取")
    public Map<String, Object> getOtherPayType() {
        Map<String, Object> ru = new HashMap<>();
        ru.put("data", PayType.OtherPayList());
        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 原云平台接口-安装工app调用 获取其他支付账号信息
     */
    @GetMapping(value = "/api/order/otherPayAccountInfo")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "名称", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "productModel", value = "产品类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productType", value = "productType", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "clientType", value = "clientType", dataType = "Long", paramType = "query")
    })
    @ApiOperation(value = "安装工app调用-获取其他支付账号信息")
    public Map<String, Object> otherPayAccountInfo(@RequestParam String username,
                                                   @RequestParam String password,
                                                   @RequestParam String productModel,
                                                   @RequestParam String productType,
                                                   @RequestParam Integer clientType) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
        if (engineer == null) {
            ResultUtil.error(ru, Constant.USER_NOT_EXIST_CODE, Constant.USER_NOT_EXIST_MSG);
            return ru;
        }
        if (!Objects.equals(engineer.getPassword(), password)) {
            ResultUtil.error(ru, Constant.PASSWORD_ERROR_CODE, Constant.PASSWORD_ERROR_MSG);
            return ru;
        }
        Map<String, Object> data = new HashMap<>();
        PayAccountDetail payAccountDetail = orderFeign.getPayAccount(10000, PayType.OTHER.value, SystemType.ENGINEER.value, PayReceiveType.ONE.value);//获取线下支付账号信息
        data.put("account", payAccountDetail.getBankAccount());// 收款账号
        data.put("userName", payAccountDetail.getCompanyName());// 收款人
        data.put("bankName", payAccountDetail.getBankName());// 开户行
        ru.put("data", data);
        return ru;
    }

    /**
     * 原云平台接口-安装工app调用 计费方式列表
     */
    @GetMapping(value = "/api/cost/list")
    public Map<String, Object> listCost(@RequestParam(required = false) String type) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        List<ProductCostDTO> costList = productFeign.productCostList(null);
        List<JSONObject> volist = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(costList)) {
            Integer t = null;
            if (StringUtil.isNotEmpty(type) && !"0".equals(type)) {
                t = Integer.parseInt(type);
            }
            for (ProductCostDTO cost : costList) {
                if (t != null && !Objects.equals(cost.getType(), t)) {
                    continue;
                }
                JSONObject json = new JSONObject();
                json.put("id", cost.getId());
                json.put("type", cost.getType());
                json.put("name", cost.getName());
                json.put("costType", cost.getName().contains("商用") ? 1 : 2);//计费类型(1 商用 2 家用)
                json.put("price", cost.getRentalFee().doubleValue());
                json.put("openaccount", cost.getInstallationFee().doubleValue());
                volist.add(json);
            }
        }
        ru.put("list", volist);
        return ru;
    }

    /**
     * 原云平台接口-安装工app调用 工单是否支付
     */
    @GetMapping(value = "/api/workorder/ispaid")
    public Map<String, Object> ispaid(@RequestParam String workorderId) {
        log.info("=================/api/workorder/ispaid ===workorderId=" + workorderId);
        Map<String, Object> ru = new HashMap<>();
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(workorderId);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        Boolean pay = workOrder.getPay();
        if (pay != null && pay) {
            ResultUtil.success(ru);
            return ru;
        } else {
            ResultUtil.error(ru, Constant.WORKORDER_NOT_PAID_CODE, Constant.WORKORDER_NOT_PAID_MSG);
            return ru;
        }
    }

    /**
     * 原云平台接口-安装工app调用 工单是否支付
     */
    @PostMapping(value = "/image/upload")
    public Map<String, Object> imageUpload(@RequestParam String accessKey,
                                           @RequestParam String imageName,
                                           @RequestParam(required = false) String imageName2,
                                           @RequestParam(required = false) String imageName3,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "file2", required = false) MultipartFile file2,
                                           @RequestParam(value = "file3", required = false) MultipartFile file3) {
        Map<String, Object> ru = new HashMap<>();
        if (!Objects.equals(accessKey, "LH6AB08F8G7324H6GBC42D3OC72GLJ25")) {
            ResultUtil.error(ru, "16", "授权码不正确");
            return ru;
        } else {
            String path = workOrderApi.upload(file, file2, file3, null, null);
            String key = "";
            key += StringUtil.isNotEmpty(imageName) ? imageName : "";
            key += StringUtil.isNotEmpty(imageName2) ? "," + imageName2 : "";
            key += StringUtil.isNotEmpty(imageName3) ? "," + imageName3 : "";

            //由于老系统的设计问题，先把上传的图片路径保存在缓存中，下一个接口取出来设置到工单上
            log.info("/image/upload---key=" + key);

            String workorderId = redisCache.get(key);
            if (StringUtil.isNotEmpty(workorderId)) {
                String[] str = workorderId.split("_");
                if (str.length == 2) {
                    workorderId = str[0];
                    int otherPayType = Integer.parseInt(str[1]);

                    WorkOrderDTO workOrder = orderFeign.getWorkOrderById(workorderId);
                    if (workOrder == null) {
                        ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
                        return ru;
                    }
                    WorkOrderDTO update = new WorkOrderDTO();
                    update.setId(workorderId);
                    update.setPayCredential(path);
                    update.setPayCredentialSubmitTime(new Date());
                    update.setPayStatus(PayStatus.WAITING_AUDIT.value);
                    update.setPayType(otherPayType);
                    update.setStep(WorkOrderInstallStep.PAID.value);
                    orderFeign.updateWorkOrderPart(update);

                    //@ 更新订单状态
                    orderFeign.otherPaySubmitCredential(workOrder.getSubOrderId(), workOrder.getId(), update.getPayType(), update.getPayCredential());
                    redisCache.delete(key);
                }
            } else {
                redisCache.set(key, path, 30L);
            }
            ResultUtil.success(ru);
            return ru;
        }
    }

    /**
     * 原云平台接口-安装工app调用 老流程其他支付方式
     *
     * @param workorderId 工单号
     */
    @PostMapping(value = "/api/workorder/otherpaid")
    public Map<String, Object> otherpaid(@RequestParam String workorderId,
                                         @RequestParam(required = false, defaultValue = "0") int otherPayType,//其他支付类型：1-POS机；2-转账
                                         @RequestParam String... image) {
        Map<String, Object> ru = new HashMap<>();
        if (otherPayType != 0) {
            //加这个是为了兼容老的app
            if (PayType.find(otherPayType) == null) {
                ResultUtil.error(ru, Constant.OTHER_PAY_UNKNOW_CODE, Constant.OTHER_PAY_UNKNOW_MSG);
                return ru;
            }
        }
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(workorderId);
        if (workOrder == null) {
            ResultUtil.error(ru, Constant.WORKORDER_IS_NOT_EXIST_CODE, Constant.WORKORDER_IS_NOT_EXIST_MSG);
            return ru;
        }
        //服务站正在上线新流程校验
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
        if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
            ResultUtil.error(ru, "74", "您所在地区正在升级新流程，其他支付不允许支付!");
            return ru;
        }

        int len = image.length;
        String key = "";
        for (int i = 0; i < len; i++) {
            key += i == 0 ? image[i] : "," + image[i];
        }
        log.info("/api/workorder/otherpaid---key=" + key);
        String path = redisCache.get(key);
        if (StringUtil.isNotEmpty(path)) {
            WorkOrderDTO update = new WorkOrderDTO();
            update.setId(workorderId);
            update.setPayCredential(path);
            update.setPayCredentialSubmitTime(new Date());
            update.setPayStatus(PayStatus.WAITING_AUDIT.value);
            update.setPayType(otherPayType);
            update.setStep(WorkOrderInstallStep.PAID.value);
            orderFeign.updateWorkOrderPart(update);
            //@ 更新订单状态
            orderFeign.otherPaySubmitCredential(workOrder.getSubOrderId(), workOrder.getId(), update.getPayType(), update.getPayCredential());
            redisCache.delete(key);
        } else {
            redisCache.set(key, workorderId + "_" + otherPayType, 30L);
        }
        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 老流程-工单更新计费方式
     *
     * @param id     工单号
     * @param costId 新系统计费方式ID
     */
    @PostMapping(value = "/api/workorder/update")
    public Map<String, Object> update(@RequestParam String id, @RequestParam Integer costId) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(id);
        if (workOrder == null) {
            ResultUtil.error(ru, "错误的工单信息");
            return ru;
        }
        //服务站正在上线新流程校验
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
        if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
            ResultUtil.error(ru, "您所在服务站正在升级新流程，请在升级成功后进行登录");
            return ru;
        }
        ProductCostDTO cost = productFeign.productCostGetById(costId);
        if (cost == null) {
            ResultUtil.error(ru, "错误的信息");
            return ru;
        }
        try {
            List<ProductCostDTO> costList = productFeign.productCostList(workOrder.getProductId());
            boolean check = costList.stream().anyMatch(costDTO -> Objects.equals(costDTO.getId(), costId));
            if (!check) {
                ResultUtil.error(ru, "不能更换为该计费方式，如有疑问可咨询客服！");
                return ru;
            }
            orderFeign.changeWorkOrderProductAndCostByEngineer(id, workOrder.getProductId(), costId);
            return ru;
        } catch (Exception e) {
            if (e instanceof YimaoRemoteException) {
                ResultUtil.error(ru, e.getMessage());
                return ru;
            } else {
                ResultUtil.error(ru, "修改计费方式遇到错误");
                return ru;
            }
        }
    }
}
