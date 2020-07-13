package com.yimao.cloud.order.processor;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.mapper.WorkOrderMapper;
import com.yimao.cloud.order.service.SyncWorkOrderService;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderQueryDTO;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;

import lombok.extern.slf4j.Slf4j;

/****
 * 地区上线同步百得工单
 * 
 * @author zhangbaobao
 * @date 2019/11/5
 */
@Component
@Slf4j
public class AreaOnlineSyncWorkOrderProcessor {

	@Resource
	private RabbitTemplate rabbitTemplate;

	@Resource
	private WorkOrderMapper workOrderMapper;
	
	@Resource
	private SyncWorkOrderService  syncWorkOrderService;
	
	@Resource
	private SystemFeign systemFeign;

	@RabbitListener(queues = RabbitConstant.AREA_ONLINE_SYNC_WORK_ORDER)
	@RabbitHandler
	public void processor(OnlineAreaDTO dto) {
		try {
			log.info("================AreaOnlineSyncWorkOrder start==========req" + JSONObject.toJSONString(dto));
			//根据省市区查询工单
			if (null != dto) {
				Boolean syncState=true;
				if (!StringUtil.isEmpty(dto.getProvince()) && !StringUtil.isEmpty(dto.getCity())
						&& !StringUtil.isEmpty(dto.getRegion())) {
					WorkOrderQueryDTO query = new WorkOrderQueryDTO();
					query.setProvince(dto.getProvince());
					query.setCity(dto.getCity());
					query.setRegion(dto.getRegion());
					Page<WorkOrderDTO> result = workOrderMapper.getWorkOrderList(query);
					log.info("================AreaOnlineSyncWorkOrder[workOrderList]======"
							+ JSONObject.toJSONString(result));
					if (null != result && !result.getResult().isEmpty()) {
						for (WorkOrderDTO workorder : result.getResult()) {
							try {
								Boolean flag=syncWorkOrderService.syncWorkOrder(workorder.getId());
								if(null==flag||!flag){
									syncState=false;
								}
							} catch (Exception e) {
								syncState=false;
								log.error("============地区上线同步售后工单异常====="+e.getMessage());
							}
						}
						
					}
					
				}
				
				//更新地区上线状态和同步状态,如果没有工单数据也更新上线状态和同步状态
				if(syncState){
					dto.setStatus(1);
					dto.setSyncState(StatusEnum.YES.value());
				}else{
					//同步失败
					dto.setSyncState(StatusEnum.FAILURE.value());
				}
				
				//更新状态
				rabbitTemplate.convertAndSend(RabbitConstant.AREA_ONLINE_STATUS_SYNC, dto);
			}
			log.info("================AreaOnlineSyncWorkOrder end==========");
		} catch (Exception e) {
			log.error("==========地区上线异常:", e.getMessage());
		}
	}

}
