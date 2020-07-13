package com.yimao.cloud.engineer.service;

import org.springframework.web.multipart.MultipartFile;

import com.yimao.cloud.engineer.utils.ResultBean;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderReqDTO;

/***
 * 工单业务处理类
 * @author zhangbaobao
 *
 */
public interface WorkOrderBusinessService {
	
	/****
	 * 扫描sn、批次码、sim卡
	 * @param req
	 */
	 void scanCode(WorkOrderReqDTO req);
	
	/***
	 * 采集水源
	 * @param req
	 */
	void collectWater(WorkOrderReqDTO req);
	
	/***
	 * 安装工改约挂单
	 * @param req
	 */
	void changeAppoint(WorkOrderReqDTO req);
	
	/***
	 * 更换机型/计费方式
	 * @param req
	 */
	void changeTypeAndModel(WorkOrderReqDTO req);
	
	/***
	 * 安装工退单:接单之后，签约完成之前才可以退单
	 * @param workOrderId
	 * @param reason
	 * @param remark
	 */
	void chargebackWorkOrder(WorkOrderReqDTO req);
	
	/***
	 * 其他支付
	 * @param workOrderId
	 * @param otherPayType
	 * @param file1
	 * @param file2
	 * @param file3
	 */
	void otherPay(String workOrderId, Integer otherPayType, MultipartFile file1, MultipartFile file2,
			MultipartFile file3);
	
	/***
	 * 签约
	 * @param req
	 */
	void signContract(WorkOrderReqDTO req);
	
	/***
	 * 云签签署成功
	 * @param code
	 * @param orderId
	 * @param string
	 * @return
	 */
	 ResultBean<WorkOrderDTO> signSuccess(String code, String orderId, String string);

	 /**
	  * @description   更换设备
	  * @author Liu Yi
	  * @date 2020/7/9 17:11
	  * @param
	  * @return void
	  */
	void changeWaterDevice(String workOrderId, String logisticsCode, Integer productId, String snCode, String simCard,Integer costId);

}
