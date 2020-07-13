package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.MoveWaterDeviceOrderDTO;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.query.order.MoveWaterDeviceOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.MoveWaterDeviceOrderVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MoveWaterDeviceOrderService {

    List<MoveWaterDeviceOrderVO> getWaitDisposeList(Integer engineerId, Boolean sort, Integer serviceType, Double longitude, Double latitude);

    List<MoveWaterDeviceOrderVO> getDisposeList(Integer engineerId, Boolean sort, Double longitude, Double latitude);

    List<MoveWaterDeviceOrderVO> getPendingList(Integer engineerId, Boolean sort, Double longitude, Double latitude);

    PageVO<MoveWaterDeviceOrderVO> getCompleteList(Integer engineerId, Boolean sort, Integer pageNum, Integer pageSize);

    MoveWaterDeviceOrderVO dismantle(String id);

    Map<String, Integer> completeDismantle(String id, Integer engineerId);

    MoveWaterDeviceOrderVO continueDismantle(String id);

    MoveWaterDeviceOrderVO waitDismantle(String id);

    MoveWaterDeviceOrderVO install(String id);

    MoveWaterDeviceOrderVO continueInstall(String id);

    MoveWaterDeviceOrderVO waitInstall(String id);

    void complete(String id);

    MoveWaterDeviceOrderVO appGetMoveWaterDeviceDetails(String id);

    void hangUp(String id, Integer type, String cause, Date startTime, Date endTime, Integer engineerId);

    void save(MoveWaterDeviceOrderDTO dto);

    Map<String, Integer> getMoveWaterDeviceCount(Integer engineerId);

    List<RenewDTO> getMoveWaterDeviceList(String completeTime, Integer engineerId, Integer timeType);

    void changeEngineer(String id, Integer engineerId, List<Integer> engineerIds, Integer type, Integer source, String operator);

    PageVO<MoveWaterDeviceOrderDTO> moveWaterOrderPage(Integer pageNum, Integer pageSize, MoveWaterDeviceOrderQuery query);

    MoveWaterDeviceOrderDTO getMoveWaterDeviceOrderById(String id);

    Integer getMoveModelTotalCount(Integer engineerId);
}
