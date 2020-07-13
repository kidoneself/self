package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.MoveWaterDeviceOrder;
import com.yimao.cloud.pojo.dto.order.MapOrderDTO;
import com.yimao.cloud.pojo.dto.order.MoveWaterDeviceOrderDTO;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.query.order.MoveWaterDeviceOrderQuery;
import com.yimao.cloud.pojo.vo.order.MoveWaterDeviceOrderVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MoveWaterDeviceOrderMapper extends Mapper<MoveWaterDeviceOrder> {

    List<MoveWaterDeviceOrderDTO> getWaitDisposeListByEngineerId(@Param("engineerId") Integer engineerId,@Param("serviceType") Integer serviceType);

    List<MoveWaterDeviceOrderDTO> getPendingListByEngineerId(@Param("engineerId") Integer engineerId);

    List<MoveWaterDeviceOrderDTO> getDisposeListByEngineerId(@Param("engineerId") Integer engineerId);

    Page<MoveWaterDeviceOrderVO> getCompleteListByEngineerId(@Param("engineerId") Integer engineerId, @Param("sort") Boolean sort);

    List<RenewDTO> getMoveWaterDeviceList(@Param("completeTime") String completeTime,
                                          @Param("engineerId") Integer engineerId,
                                          @Param("timeType") Integer timeType);

    Page<MoveWaterDeviceOrderDTO> getPage(MoveWaterDeviceOrderQuery query);

    int updateDismantleEngineerStationInfoByPCR(MoveWaterDeviceOrderDTO updateMoveWaterDeviceOrder);

    int updateInstallEngineerStationInfoByPCR(MoveWaterDeviceOrderDTO updateMoveWaterDeviceOrder);

    int updateDismantleEngineerInfoByPCR(MoveWaterDeviceOrderDTO updateMoveWaterDeviceOrder);

    int updateInstallEngineerInfoByPCR(MoveWaterDeviceOrderDTO updateMoveWaterDeviceOrder);

    int updateDismantleEngineerInfo(MoveWaterDeviceOrderDTO mwdo);

    int updateInstallEngineerInfo(MoveWaterDeviceOrderDTO mwdo);

    Integer getMoveModelTotalCount(@Param("engineerId") Integer engineerId);

    List<MapOrderDTO> getMoveWaterDeviceOrder(Integer engineerId);
}
