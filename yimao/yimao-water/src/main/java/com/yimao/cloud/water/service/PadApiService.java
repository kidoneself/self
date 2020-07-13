package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.water.po.FilterData;
import com.yimao.cloud.water.po.WaterDevice;
import com.yimao.cloud.water.po.WaterDeviceConsumable;
import com.yimao.cloud.water.po.WaterDeviceReplaceRecord;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PadApiService {

    Map<String, Map<String, Object>> getFilterMap(WaterDevice waterDevice, List<WaterDeviceConsumable> consumableList);

    Map<String, Integer> getFilterFlow(FilterData filterData, List<WaterDeviceConsumable> consumableList);

    WaterDeviceReplaceRecord getReplaceRecordBySnCode(String sn);

    Date getFilterChangeDate(String sncode, Date simActivatingTime, String filterName, WaterDeviceReplaceRecord replaceRecord);

    void caculateMoney(WaterDevice device, WorkOrderDTO workOrder, Integer flow, Integer time, Map<String, Object> ru, WaterDevice update);

    void pushMsgToApp(WaterDevice device, Integer pushType, Integer mechanism, Integer pushMode, String title, Integer receiverId, String receiverName, Integer devices, Map<String, String> distributorMessage, Integer filterType);

    Boolean pushMsgToAppByMessageFilter(WaterDevice device, Integer faultType, Integer filterType, String filterName);

	void pushMsgToStation(WaterDevice device, int pushType, int mechanism, String title, Integer areaId,
			Map<String, String> stationMessage, Integer filterType);

}
