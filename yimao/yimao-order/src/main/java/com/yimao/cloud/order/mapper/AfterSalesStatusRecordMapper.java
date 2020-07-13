package com.yimao.cloud.order.mapper;

import com.yimao.cloud.order.po.AfterSalesStatusRecord;
import tk.mybatis.mapper.common.Mapper;

/*****
 * @desc 售后单状态变更记录
 * @author zhangbaobao
 * @date 2019/9/21
 */
public interface AfterSalesStatusRecordMapper  extends Mapper<AfterSalesStatusRecord> {
	
	/***
	 * 保存售后单状态变更记录
	 * @param afterSalesStatusRecord
	 * @return
	 */
	int saveAfterSalesStatusRecord(AfterSalesStatusRecord afterSalesStatusRecord);
}
