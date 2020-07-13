package com.yimao.cloud.station.controller;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.MessageModelTypeEnum;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.StationQueryEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.cms.ContentDTO;
import com.yimao.cloud.pojo.dto.cms.VideoDTO;
import com.yimao.cloud.pojo.dto.station.StationAdminCacheDTO;
import com.yimao.cloud.pojo.dto.station.StationMessageDTO;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.dto.system.MessagePushDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.query.station.*;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.station.aop.annotation.StationQuery;
import com.yimao.cloud.station.feign.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 控制台模块（概况+消息）
 *
 * @author yaoweijun
 */
@RestController
@Api(tags = "ConsoleController")
@Slf4j
public class ConsoleController {
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private HraFeign hraFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private UserCache userCache;
    @Resource
    private WaterFeign waterFeign;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private CmsFeign cmsFeign;

    /**
     * 概况-控制台-待办事项
     *
     * @return
     */
    @StationQuery(StationQueryEnum.ListQuery)
    @GetMapping("/station/console/schedule")
    @ApiOperation(value = "概况-控制台-待办事项")
    public StationScheduleDTO getSchedule(BaseQuery query) {

        log.info("query={}", JSON.toJSONString(query));

        StationAdminCacheDTO stationUserInfo = userCache.getStationUserInfo();
        Integer serviceType = stationUserInfo.getType(); //服务类型 0-售前+售后；1-售前；2-售后

        StationScheduleDTO res = new StationScheduleDTO();//待办事项统计数据
        if (PermissionTypeEnum.havePreSale(serviceType)) {//角色拥有售前权限，查询“所有用户”
            //获取售前服务权限
            Set<Integer> preServiceAreas = userCache.getStationUserAreas(1, PermissionTypeEnum.PRE_SALE.value);
            StationScheduleDTO user = userFeign.getStationDistributorNum(preServiceAreas);
            res.setAgentNum(user.getAgentNum());
            res.setDistributorNum(user.getDistributorNum());
            res.setCommonUserNum(user.getCommonUserNum());
            res.setVipUserNum(user.getVipUserNum());
        }
        //获取售后服务区域
        Set<Integer> afterServiceAreas = userCache.getStationUserAreas(1, PermissionTypeEnum.AFTER_SALE.value);
        //查询售后服务区域下所有安装工id
        List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(afterServiceAreas);

        if (PermissionTypeEnum.haveAfterSale(serviceType)) {//角色拥有售后权限，查询“水机设备”，“安装工单”
            //设备（新安装）
            ExclusiveQuery exclusiveQuery = new ExclusiveQuery();
            exclusiveQuery.setEngineerIds(engineerIds);
            StationScheduleDTO device = waterFeign.getDeviceTotalAndNewInstallNum(exclusiveQuery);
            //工单与设备（续费）
            WorkOrderQuery orderQuery = new WorkOrderQuery();
            orderQuery.setEngineerIds(engineerIds);
            StationScheduleDTO order = orderFeign.getStationWorkerOrderAndRenewNum(orderQuery);
            res.setYesterdayInstallNum(device.getYesterdayInstallNum());
            res.setYesterdayRenewNum(order.getYesterdayRenewNum());
            res.setTotalDeviceNum(device.getTotalDeviceNum());
            res.setRenewNum(order.getRenewNum());
            res.setNotAcceptedNum(order.getNotAcceptedNum());
            res.setAcceptedNum(order.getAcceptedNum());
            res.setProcessingNum(order.getProcessingNum());
            res.setFinishNum(order.getFinishNum());
        }

        //======================售前售后用户都拥有的查看权限===========================
        //预约评估
        StationScheduleDTO hra = hraFeign.getStationHraNum(query.getStations());

        res.setYesterdayFinishAssess(hra.getYesterdayFinishAssess());
        res.setTodayNeedAssess(hra.getTodayNeedAssess());
        res.setTotalFinishAssess(hra.getTotalFinishAssess());

        //推荐资讯
        List<ContentDTO> information = cmsFeign.getInformationByTop();
        //推荐视频
        List<VideoDTO> video = cmsFeign.getVideoByRecommend();
        res.setInformation(information);
        res.setVideo(video);

        return res;
    }

    /**
     * 概况-消息通知-系统消息
     *
     * @return
     */
    @StationQuery(value=StationQueryEnum.ListQuery,serviceType=PermissionTypeEnum.ALL)
    @PostMapping("/station/console/message/{pageSize}/{pageNum}")
    @ApiOperation(value = " 概况-消息通知-系统消息")
    public Object getMessage(@PathVariable("pageNum") Integer pageNum,
                             @PathVariable("pageSize") Integer pageSize, @RequestBody StationMessageQuery query) {
        log.info("query={}", JSON.toJSONString(query));

        if (Objects.nonNull(query.getPushType()) && Objects.isNull(MessageModelTypeEnum.find(query.getPushType()))) {

            throw new BadRequestException("消息类型错误");
        }

        if (Objects.isNull(query.getSortType())) {
            query.setSort("desc");
        } else if (query.getSortType() == 0) {
            query.setSort("desc");
        } else if (query.getSortType() == 1) {
            query.setSort("asc");
        } else {
            query.setSort("desc");
        }
        
        
        //判别pushType
        if(Objects.isNull(query.getPushType())) {
        	//获取查询区域（所有）
        	Set<Integer> queryAreas= query.getAreas(); 
        	
        	Set<Integer> queryAreas2=new HashSet<Integer>();
        	queryAreas2.addAll(queryAreas);
        	
        	//用户所有售前区域权限
        	Set<Integer> preReceiverIds=userCache.getStationUserAreas(1, PermissionTypeEnum.PRE_SALE.value);
            //拆分售前区域，用来查询创建账号
        	preReceiverIds=filterReceiver(queryAreas,preReceiverIds);         	
        	log.info("售前消息类型查询区域={}",preReceiverIds);
        	
        	//用户所有售后区域权限
        	Set<Integer> afterReceiverIds=userCache.getStationUserAreas(1, PermissionTypeEnum.AFTER_SALE.value);
        	//拆分售后区域，用来除经销商创建其他查询
        	afterReceiverIds=filterReceiver(queryAreas2,afterReceiverIds);  
        	log.info("售后消息类型查询区域={}",afterReceiverIds);
        	//查询售后安装工集合
        	List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(afterReceiverIds);
        	
        	afterReceiverIds.addAll(engineerIds);
        	
        	if(CollectionUtil.isEmpty(preReceiverIds) && CollectionUtil.isEmpty(afterReceiverIds)) {
        		return null;
        	}
        	       	
        	query.setPreReceiverIds(preReceiverIds);
        	query.setAfterReceiverIds(afterReceiverIds);
        }else {
        	//消息类别条件查询
        	if(query.getPushType() == MessageModelTypeEnum.CREATE_STATION_DISTRIBUTOR_ACCOUNT.value) {
        		//售前查询
        		//获取查询区域
            	Set<Integer> queryAreas= query.getAreas();
          
            	Set<Integer> receiverIds=userCache.getStationUserAreas(1, PermissionTypeEnum.PRE_SALE.value);
            
            	Set<Integer> preReceiverIds=filterReceiver(queryAreas,receiverIds);
            	
            	log.info("仅售前消息类型查询区域={}",preReceiverIds);
            	
            	if(CollectionUtil.isEmpty(preReceiverIds)) {
            		return null;
            	}
            	
            	query.setPreReceiverIds(preReceiverIds);
            	//传空集合，sql判断无需校验null
            	query.setAfterReceiverIds(new HashSet());
        	}else {
        		//售后查询
        		//设置售后区域(根据安装工的绑定服务站门店id集合)
            	Set<Integer> receiverIds=userCache.getStationUserAreas(1, PermissionTypeEnum.AFTER_SALE.value);
            	
            	//获取查询售后区域
            	Set<Integer> queryAreas=query.getAreas();
            	
            	Set<Integer> afterReceiverIds = filterReceiver(queryAreas,receiverIds);
            	
            	log.info("仅售后消息类型查询区域={}",afterReceiverIds);
            	
            	if(CollectionUtil.isEmpty(afterReceiverIds)) {
            		return null;
            	}
            	
            	//查询售后安装工集合(查询安装工创建)
            	List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(afterReceiverIds);

            	//兼容旧数据根据安装工的区域id
            	afterReceiverIds.addAll(engineerIds);
          	
            	query.setAfterReceiverIds(afterReceiverIds);
            	//传空集合，sql判断无需校验null
            	query.setPreReceiverIds(new HashSet());
            	
        	}
        }

        PageVO<MessagePushDTO> page = systemFeign.getStationMessage(pageNum, pageSize, query);
        return page;
    }
    
    //二次过滤
    private Set<Integer> filterReceiver(Set<Integer> queryAreas, Set<Integer> receiverIds) {
    	Iterator<Integer> iterator = queryAreas.iterator();
    	
    	while (iterator.hasNext()) {
			Integer area=iterator.next();
			
			if(! receiverIds.contains(area)) {
				iterator.remove();
			}
			
		}
    	
    	
		return queryAreas;
	}

	/**
     * 概况-消息通知-系统消息-消息类型筛选列表
     *
     * @return
     */
    @PostMapping("/station/console/message/pushTypeList")
    @ApiOperation(value = " 概况-消息通知-系统消息-消息类型筛选列表")
    public Object getMessagePushTypeList() {
    	Map<String,Integer> result=new HashedMap();
    	//判断用户售前售后
        StationAdminCacheDTO stationAdmin = userCache.getStationUserInfo();
        Integer type =stationAdmin.getType();
        
        //消息分类:1-阈值提醒,2-续费推送,3-报警推送,5-新工单分配,6-工单完成 12-创建安装工(仅发送站务系统消息) 13--创建经销商(仅发送站务系统消息)
        if(PermissionTypeEnum.PRE_SALE.value == type) {
        	result.put(MessageModelTypeEnum.CREATE_STATION_DISTRIBUTOR_ACCOUNT.name, MessageModelTypeEnum.CREATE_STATION_DISTRIBUTOR_ACCOUNT.value);
        }

        if(PermissionTypeEnum.AFTER_SALE.value == type) {
        	result.put(MessageModelTypeEnum.THRESHOLD_NOTICE.name, MessageModelTypeEnum.THRESHOLD_NOTICE.value);
        	result.put(MessageModelTypeEnum.RENEW_ORDER_PUSH.name, MessageModelTypeEnum.RENEW_ORDER_PUSH.value);
        	result.put(MessageModelTypeEnum.WARM_PUSH.name, MessageModelTypeEnum.WARM_PUSH.value);
        	result.put(MessageModelTypeEnum. CREATE_STATION_ENGINEER_ACCOUNT.name, MessageModelTypeEnum. CREATE_STATION_ENGINEER_ACCOUNT.value);
        	result.put(MessageModelTypeEnum.WATER_ORDER.name, MessageModelTypeEnum.WATER_ORDER.value);
        	result.put(MessageModelTypeEnum.ORDER_FINISH.name, MessageModelTypeEnum.ORDER_FINISH.value);
        }
        	
        
        if(PermissionTypeEnum.ALL.value == type) {
        	result.put(MessageModelTypeEnum.THRESHOLD_NOTICE.name, MessageModelTypeEnum.THRESHOLD_NOTICE.value);
        	result.put(MessageModelTypeEnum.RENEW_ORDER_PUSH.name, MessageModelTypeEnum.RENEW_ORDER_PUSH.value);
        	result.put(MessageModelTypeEnum.WARM_PUSH.name, MessageModelTypeEnum.WARM_PUSH.value);
        	result.put(MessageModelTypeEnum. CREATE_STATION_ENGINEER_ACCOUNT.name, MessageModelTypeEnum. CREATE_STATION_ENGINEER_ACCOUNT.value);
        	result.put(MessageModelTypeEnum.CREATE_STATION_DISTRIBUTOR_ACCOUNT.name, MessageModelTypeEnum.CREATE_STATION_DISTRIBUTOR_ACCOUNT.value);
        	result.put(MessageModelTypeEnum.WATER_ORDER.name, MessageModelTypeEnum.WATER_ORDER.value);
        	result.put(MessageModelTypeEnum.ORDER_FINISH.name, MessageModelTypeEnum.ORDER_FINISH.value);
        	
        }
        
        return result;
        
    }

    @PostMapping("/station/console/message/read/{id}")
    @ApiOperation(value = " 概况-消息通知-系统消息-消息已读")
    public Object readMessage(@PathVariable("id") Integer id) {
        if (Objects.isNull(id)) {
            throw new BadRequestException("消息id为空");
        }

        Integer stationUserId = userCache.getUserId();

        MessagePushDTO messagePush = new MessagePushDTO();
        messagePush.setId(id);
        messagePush.setReceiver(stationUserId + "");
        messagePush.setReadStatus(1);

        systemFeign.readMessage(messagePush);

        return ResponseEntity.noContent().build();

    }

    @PostMapping("/station/console/message/sendMessage")
    @ApiOperation(value = " 概况-消息通知-系统消息-发送短信")
    public Object sendMessage(@RequestBody StationMessageDTO stationMessageDTO) {
        if (Objects.isNull(stationMessageDTO.getId())) {
            log.info("发送短信id为空");
            throw new BadRequestException("发送短信id为空");
        }


        systemFeign.sendMessage(stationMessageDTO);

        return ResponseEntity.noContent().build();
    }

    /**
     * 概况-消息通知-总部消息
     *
     * @return
     */
    @PostMapping("/station/console/headOfficeMessage/{pageSize}/{pageNum}")
    @ApiOperation(value = " 概况-消息通知-总部消息")
    public Object getHeadOfficeMessage(@PathVariable("pageNum") Integer pageNum,
                                       @PathVariable("pageSize") Integer pageSize, @RequestBody HeadOfficeMessageQuery query) {
        log.info("query={}", JSON.toJSONString(query));

        if (Objects.isNull(query.getSortType())) {
            query.setSort("desc");
        } else if (query.getSortType() == 0) {
            query.setSort("desc");
        } else if (query.getSortType() == 1) {
            query.setSort("asc");
        } else {
            query.setSort("desc");
        }

        String tag = "";

        //查询用户绑定服务站
        Set<Integer> stations = userCache.getStationUserAreas(0,null);

        if (!CollectionUtil.isEmpty(stations)) {
            com.yimao.cloud.pojo.query.system.StationQuery stationQuery = new com.yimao.cloud.pojo.query.system.StationQuery();
            stationQuery.setIds(stations);
            List<StationDTO> list = systemFeign.getStationListByIds(stationQuery);

            if (!CollectionUtil.isEmpty(list)) {
                for (StationDTO stationDTO : list) {

                    if (tag.equals("M")) {
                        break;
                    }

                    if (stationDTO.getContract()) {//已承包
                        if (tag.equals("")) {
                            tag = "M_1";
                        }

                        if (tag.contains("M_2")) {
                            tag = "M";
                        }

                        if (tag.contains("M_1")) {
                            continue;
                        }


                    } else {//未承包
                        if (tag.equals("")) {
                            tag = "M_2";
                        }

                        if (tag.contains("M_1")) {
                            tag = "M";
                        }

                        if (tag.contains("M_2")) {
                            continue;
                        }

                    }
                }
            }

        }

        query.setTag(tag);

        return cmsFeign.getHeadOfficeMessageList(pageNum, pageSize, query);
    }

    /**
     * 概况-消息通知-总部消息-消息分类筛选项获取
     *
     * @return
     */
    @GetMapping("/station/console/headOfficeMessageType")
    @ApiOperation(value = " 概况-消息通知-总部消息-消息分类筛选项获取")
    public Object getHeadOfficeMessageType() {

        return cmsFeign.getHeadOfficeMessageType();
    }


    /**
     * 展示每个类型推荐的前三问题
     *
     * @return
     */
    @PostMapping("/station/console/helpCenter/list")
    @ApiOperation(value = "概况-控制台-帮助中心-默认类型推荐列表")
    public Object helpCenter() {

        return systemFeign.helpCenterData();

    }


    @GetMapping("/station/helpAndService/list")
    @ApiOperation(value = "全局-帮助中心-默认下拉前五问答")
    public Object helpAndService() {

        return systemFeign.getHelpAndServiceList();

    }

    /**
     * 根据类型查询全部问题列表
     *
     * @return
     */
    @GetMapping("/station/console/helpCenter/more/{typeCode}/{pageSize}/{pageNum}")
    @ApiOperation(value = " 概况-控制台-帮助中心-更多")
    public Object helpCenterMoreByTypeCode(@PathVariable("pageNum") Integer pageNum,
                                           @PathVariable("pageSize") Integer pageSize, @PathVariable("typeCode") Integer typeCode) {

        if (Objects.isNull(typeCode)) {
            throw new BadRequestException("typeCode为空");
        }

        return systemFeign.helpCenterMoreByTypeCode(pageNum, pageSize, typeCode);


    }

    /**
     * 某类型全部问题列表
     *
     * @return
     */
    @PostMapping("/station/console/helpCenterQuestion/searchList/{pageSize}/{pageNum}")
    @ApiOperation(value = "控制台-帮助中心-搜索")
    public Object helpCenterQuestionSearchList(@PathVariable("pageNum") Integer pageNum,
                                               @PathVariable("pageSize") Integer pageSize, String keywords) {

        if (StringUtils.isBlank(keywords)) {
            throw new BadRequestException("查询内容为空");
        }

        return systemFeign.helpCenterQuestionSearchList(pageNum, pageSize, keywords);


    }


}
