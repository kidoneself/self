package com.yimao.cloud.system.controller;


import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.ExportUrlConstant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.DeviceFaultFilterType;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.enums.MessageFilterTypeEnum;
import com.yimao.cloud.base.enums.MessageMechanismEnum;
import com.yimao.cloud.base.enums.MessageModelTypeEnum;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.enums.MessagePushObjectEnum;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.utils.SmsUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.station.StationMessageDTO;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.system.MessagePushDTO;
import com.yimao.cloud.pojo.dto.system.MessageTemplateDTO;
import com.yimao.cloud.pojo.dto.system.SmsMessageDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.query.station.StationMessageQuery;
import com.yimao.cloud.pojo.query.system.MessagePushQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.UserFeign;
import com.yimao.cloud.system.feign.WaterFeign;
import com.yimao.cloud.system.mapper.MessagePushMapper;
import com.yimao.cloud.system.po.MessagePush;
import com.yimao.cloud.system.service.ExportRecordService;
import com.yimao.cloud.system.service.MessagePushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/***
 * 功能描述: 消息记录
 *
 * @auther: liu yi
 * @date: 2019/4/30 10:09
 */
@RestController
@Slf4j
@Api(tags = "MessagePushController")
public class MessagePushController {
    @Resource
    private MessagePushService messagePushService;
    @Resource
    private MessagePushMapper messagePushMapper;
    @Resource
    private UserCache userCache;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private WaterFeign waterFeign;
    @Resource
    private UserFeign userFeign;

    @RequestMapping(value = {"/messagePush"}, method = {RequestMethod.GET})
    @ApiOperation("推送消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "content", value = "内容", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "clicknotice", value = "点击通知打开方式:1-打开APP 2-打开网页", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "address", value = "链接地址", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "devices", value = "设备范围： 0-所有设备 1-Android设备2-IOS设备", defaultValue = "0", dataType = "Long", paramType = "query")
    })
    public Object push(@RequestParam String content,
                       @RequestParam Integer clicknotice,
                       @RequestParam String address,
                       @RequestParam Integer devices) {
        MessagePushDTO mp = new MessagePushDTO();

        mp.setReceiver(userCache.getCurrentAdminRealName());
        mp.setContent(content);
        mp.setClickNotice(clicknotice);
        mp.setCreateTime(new Date());
        mp.setDevices(devices);//设备范围： 0-所有设备 1-Android设备2-IOS设备
        mp.setTitle("系统推送");
        mp.setAddress(address);
        mp.setApp(0);

        //通过队列异步处理
        rabbitTemplate.convertAndSend(RabbitConstant.SYSTEM_MESSAGE_PUSH, mp);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = {"/messagePush"}, method = {RequestMethod.POST})
    @ApiOperation("新增消息记录")
    @ApiImplicitParam(name = "dto", value = "消息记录", required = true, dataType = "MessagePushDTO", paramType = "body")
    public Object add(@RequestBody MessagePushDTO dto) {
        MessagePush mp = new MessagePush(dto);
        messagePushService.insert(mp);
        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:分页查询消息推送
     *
     * @param: [pageSize, pageNum, type, content, startTime, endTime, devices]
     * @auther: liu yi
     * @date: 2019/5/5 11:34
     * @return: java.lang.Object
     */
    @RequestMapping(value = {"/messagePush/query/{pageSize}/{pageNum}"}, method = {RequestMethod.GET})
    @ApiOperation("分页查询消息推送")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "receiverId", value = "接收者id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pushType", value = "10-系统推送 1-阈值提醒,2-续费推送,3-报警推送,4-创建账号,5-新工单分配,6-工单完成,7-审核通过,8-审核不通过,9-经销商续配额", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "内容", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "devices", value = "设备范围： 0-所有设备 1-Android设备2-IOS设备", defaultValue = "0", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "app", value = "推送方式：1-推送给安装工 2-推送给经销商", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object page(@RequestParam(required = false) Integer receiverId,
                       @RequestParam(required = false) Integer pushType,
                       @RequestParam(required = false) String content,
                       @RequestParam(required = false) Date startTime,
                       @RequestParam(required = false) Date endTime,
                       @RequestParam(required = false) Integer devices,
                       @RequestParam(required = false) Integer app,
                       @PathVariable Integer pageSize,
                       @PathVariable Integer pageNum) {
        PageVO<MessagePushDTO> page = this.messagePushService.page(receiverId, pushType, content, devices, app, startTime, endTime, pageSize, pageNum);
        return ResponseEntity.ok(page);
    }

    /***
     * 功能描述:根据条件查找该时间最后一次记录
     *
     * @param: [deviceId, pushType, expireDate]
     * @auther: liu yi
     * @date: 2019/5/10 10:11
     * @return: com.yimao.cloud.pojo.dto.system.MessagePushDTO
     */
    @RequestMapping(value = {"/messagePush/lastMessagePush"}, method = {RequestMethod.GET})
    @ApiOperation("根据条件查找该时间最后一次记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", required = true, value = "设备Id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "filterType", required = true, value = "推送过滤类型：1-余额不足类型推送 2-制水故障类型推送 3-TDS异常故障类型推送 4-PP棉滤芯过期故障类型推送  5-CTO棉滤芯过期故障类型推送 6- UDF棉滤芯过期故障类型推送 7-T33棉滤芯过期故障类型推送", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "compareDate", required = true, value = "时间", dataType = "String", paramType = "query")
    })
    public Object findLastMessagePush(@RequestParam(value = "deviceId") String deviceId,
                                      @RequestParam(value = "filterType") Integer filterType,
                                      @RequestParam(value = "compareDate") Date compareDate) {
        MessagePushDTO dto = this.messagePushService.findLastMessagePush(deviceId, filterType, compareDate);
        return ResponseEntity.ok(dto);
    }

    /***
     * 功能描述:根据id获取详情
     *
     * @param: [model, id]
     * @auther: liu yi
     * @date: 2019/5/5 11:35
     * @return: java.lang.String
     */
    @RequestMapping(value = {"/messagePush/{id}"}, method = {RequestMethod.GET})
    @ApiOperation("根据id获取详情")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long", paramType = "path")
    public Object details(@PathVariable("id") Integer id) {
        MessagePushDTO messagePush = this.messagePushService.getMessagePushById(id);

        return ResponseEntity.ok(messagePush);
    }

    /***
     * 功能描述:批量删除
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/5/5 11:36
     * @return: java.lang.String
     */
    @DeleteMapping(value = {"/messagePush/{ids}/batch"})
    @ApiOperation("根据id删除消息")
    @ApiImplicitParam(name = "ids", value = "多个id用逗号隔开", required = true, dataType = "String", paramType = "path")
    public Object delete(@PathVariable("ids") String ids) {
        this.messagePushService.batchDeleteMessagePushByIds(ids);
        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:删除
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/5/5 11:36
     * @return: java.lang.String
     */
    @DeleteMapping(value = {"/messagePush/{id}"})
    @ApiOperation("根据id删除消息")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long", paramType = "path")
    public Object delete(@PathVariable("id") Integer id) {
        this.messagePushService.deleteMessagePushById(id);

        return ResponseEntity.noContent().build();
    }


    /***
     * 功能描述:更新信息为已读
     *
     * @param: [model, id]
     * @auther: liu yi
     * @date: 2019/5/5 11:36
     * @return: java.lang.String
     */
    @RequestMapping(value = {"/messagePush/{id}"}, method = {RequestMethod.PATCH})
    @ApiOperation("更新信息为已读")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long", paramType = "path")
    public Object setMessagePushIsRead(@PathVariable(name = "id") Integer id) {
        this.messagePushService.setMessagePushIsRead(id);

        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:获取用户未读取的信息数量
     *
     * @param: [pageSize, pageNum, type, content, startTime, endTime, devices]
     * @auther: liu yi
     * @date: 2019/5/5 11:34
     * @return: java.lang.Object
     */
    @RequestMapping(value = {"/messagePush/unReadCount"}, method = {RequestMethod.GET})
    @ApiOperation("获取用户未读取的信息数量")
    public Object getUnReadNum(@RequestParam Integer receiverId,
                               @RequestParam(required = false) Integer pushType,
                               @RequestParam(required = false) String content,
                               @RequestParam(required = false) Integer app) {
        Integer count = this.messagePushService.getUnReadNum(receiverId, pushType, content, app);

        return ResponseEntity.ok(count);
    }


    @PostMapping(value = {"/messagePush/export"})
    @ApiOperation("导出信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "推送者", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pushType", value = "推送类型:10-系统推送 1-阈值提醒,2-续费推送,3-报警推送,4-创建账号,5-新工单分配,6-工单完成,7-审核通过,8-审核不通过,9-经销商续配额", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "内容", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "devices", value = "设备范围： 0-所有设备 1-Android设备 2-IOS设备", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query")
    })
    public Object export(@RequestParam(required = false) String userName,
                         @RequestParam(required = false) Integer pushType,
                         @RequestParam(required = false) String content,
                         @RequestParam(required = false) Integer devices,
                         @RequestParam(required = false) Date startTime,
                         @RequestParam(required = false) Date endTime) {

        /*String header = "消息数据" + DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01);
        String[] beanPropertys = new String[]{"title", "content", "deviceTypeStr", "createTimeStr"};

        String[] titles = new String[]{"类别", "内容", "设备", "推送时间"};

        List<MessagePushExportDTO> dtolist = messagePushService.exportMessagePush(userName, pushType, content, devices, startTime, endTime);
        boolean boo = ExcelUtil.exportSXSSF(dtolist, header, titles.length - 1, titles, beanPropertys, response);
        if (!boo) {
            throw new YimaoException("导出记录失败!");
        }*/


        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        ExportRecordDTO record = exportRecordService.save(ExportUrlConstant.EXPORT_MESSAGEPUSH_URL, "消息数据");
        MessagePushQuery query = new MessagePushQuery();
        query.setUserName(userName);
        query.setContent(content);
        query.setPushType(pushType);
        query.setDevices(devices);
        query.setStartTime(startTime);
        query.setEndTime(endTime);

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_SYSTEM, map);
        }

        return CommResult.ok(record.getId());
    }
    
    /**
     * 站务系统-概况-消息通知-系统消息
     * @param query
     * @return
     */
    @GetMapping(value = "/messagePush/station/query/{pageSize}/{pageNum}")
    public PageVO<MessagePushDTO> getStationMessage(@PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize,@RequestBody StationMessageQuery query){
    	  PageVO<MessagePushDTO> page = messagePushService.stationPage(pageNum,pageSize,query);
    	  
    	  return page;
    }
    
    /**
     * 站务系统-概况-消息通知-系统消息-消息已读
    * @param query
    * @return
    */
    @PostMapping(value = "/messagePush/station/readMessage")
    public void readMessage(@RequestBody MessagePushDTO messagePush) {
    	//查询消息是否已读
    	MessagePush res=messagePushMapper.selectByPrimaryKey(messagePush.getId());
    	if(Objects.isNull(res)) {
    		return;
    	}
    	
    	if(res.getReadStatus() == 1) {
    		//已读
    		return;
    	}
    	MessagePush message = new MessagePush();
    	message.setId(messagePush.getId());
    	message.setReadStatus(messagePush.getReadStatus());
    	message.setReceiver(messagePush.getReceiver());
    	
    	messagePushMapper.updateByPrimaryKeySelective(message);
    }
    

    /**
     * 站务系统-概况-消息通知-系统消息-短信发送
    * @param query
    * @return
    */
    @PostMapping(value = "/messagePush/station/sendMessage")
	public void sendMessage(@RequestBody StationMessageDTO stationMessageDTO) {
    	//查询该消息
    	MessagePush message = messagePushMapper.selectByPrimaryKey(stationMessageDTO.getId());
    	
    	if(Objects.isNull(message)) {
    		log.info("未查询到该消息");
    		return;
    	}
    	
    	if(message.getPushType() == null || message.getPushType() != 3) {
    		log.info("该消息类型非警报推送，发送失败");
    		return;
    	}
    	
    	if(Objects.isNull(message.getSncode())) {
    		log.info("该消息sn码为空，发送失败");
    		return;
    	}
    	
    	WaterDeviceDTO dto=waterFeign.getBySnCode(message.getSncode());
    	
    	if(Objects.isNull(dto)) {
    		log.info("未查询到该水机设备，发送失败");
    		return;
    	}    	
    	
    	String userPhone = dto.getDeviceUserPhone();
    	
    	if(StringUtil.isBlank(userPhone)) {
    		log.info("水机设备用户手机号为空，发送失败");
    		return;
    	}
    	
        EngineerDTO engineer = userFeign.getEngineerBasicInfoByIdForMsgPushInfo(dto.getEngineerId());
    	
        if(Objects.isNull(engineer)) {
        	log.info("安装工未查询到，发送失败");
        	return;
    	}
        
        String filterName="";
        if (Objects.equals(message.getFilterType(), MessageFilterTypeEnum.PP.value)) {
        	filterName =  DeviceFaultFilterType.PP.name;
        } else if (Objects.equals(message.getFilterType(), MessageFilterTypeEnum.CTO.value)) {
        	filterName = DeviceFaultFilterType.CTO.name;
        } else if (Objects.equals(message.getFilterType(), MessageFilterTypeEnum.UDF.value)) {
        	filterName = DeviceFaultFilterType.UDF.name;
        } else if (Objects.equals(message.getFilterType(), MessageFilterTypeEnum.T33.value)) {
        	filterName = DeviceFaultFilterType.T33.name;
        }
        Map<String, String> map = new HashMap<>();
        map.put("#code#", message.getSncode());
        map.put("#code1#", engineer.getRealName());
        map.put("#code2#", engineer.getPhone());
        map.put("#code3#", filterName);
       	//3,6,5,1
        SmsMessageDTO smsMessage = new SmsMessageDTO();
        smsMessage.setType(MessageModelTypeEnum.WARM_PUSH.value);
        smsMessage.setPushObject(MessagePushObjectEnum.WATER_USER.value);
        smsMessage.setPhone(userPhone);
        smsMessage.setMechanism(MessageMechanismEnum.FILTER_CHANGE_BEFORE.value);
        smsMessage.setPushMode(MessagePushModeEnum.YIMAO_SMS.value);
        smsMessage.setContentMap(map);
        // 发送短信
        rabbitTemplate.convertAndSend(RabbitConstant.SMS_MESSAGE_PUSH, smsMessage);
    	
        log.info("服务站短信发送成功");
    	
    }
}
