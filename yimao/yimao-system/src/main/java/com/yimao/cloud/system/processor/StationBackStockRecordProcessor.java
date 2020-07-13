package com.yimao.cloud.system.processor;

import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.pojo.dto.system.StationBackStockRecordDTO;
import com.yimao.cloud.system.mapper.StationBackStockRecordMapper;
import com.yimao.cloud.system.po.StationBackStockRecord;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StationBackStockRecordProcessor {
	
	@Resource
	private StationBackStockRecordMapper stationBackStockRecordMapper;
	
	@RabbitListener(queues = RabbitConstant.STATION_BACK_STOCK_RECORD)
    @RabbitHandler
	public void processor(StationBackStockRecordDTO dto) {
		
		try {
			
			if(Objects.isNull(dto)) {
				log.info("新增退机库存纪录失败，对象为空");
				return;
			}
			
			if(Objects.isNull(dto.getWorkorderBackCode())) {
				log.info("新增退机库存纪录失败，退机单号为空");
				return;
			}
			
			//校验是否有相同退单号的他退机库存
			StationBackStockRecord query =new StationBackStockRecord();
			query.setWorkorderBackCode(dto.getWorkorderBackCode());
			StationBackStockRecord origin = stationBackStockRecordMapper.selectOne(query);
			
			if(Objects.isNull(origin)) {//没有新增
				StationBackStockRecord record = new StationBackStockRecord(dto);
				record.setIsTransferStock(false);
				stationBackStockRecordMapper.insertSelective(record);
			}else {//存在更新
				StationBackStockRecord update = new StationBackStockRecord(dto);
				update.setId(origin.getId());
				stationBackStockRecordMapper.updateByPrimaryKeySelective(update);
			}
			
			
		} catch (Exception e) {
			log.info("新增退机库存纪录失败，退机单号={}",dto.getWorkorderBackCode());
		}
		 
	}
}
