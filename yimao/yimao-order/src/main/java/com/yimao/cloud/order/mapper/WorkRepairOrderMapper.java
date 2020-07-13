package com.yimao.cloud.order.mapper;

import java.util.List;

import com.yimao.cloud.pojo.dto.order.MapOrderDTO;
import com.yimao.cloud.pojo.dto.order.MoveWaterDeviceOrderDTO;
import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.WorkRepairMaterialUseRecord;
import com.yimao.cloud.order.po.WorkRepairOrder;
import com.yimao.cloud.pojo.dto.order.WorkRepairMaterialUseRecordDTO;
import com.yimao.cloud.pojo.query.order.WorkRepairOrderQuery;
import com.yimao.cloud.pojo.vo.station.WorkRepairOrderVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface WorkRepairOrderMapper extends Mapper<WorkRepairOrder> {

    int createRepairOrder(WorkRepairOrder workRepairOrder);

    Page<WorkRepairOrderVO> listRepairOrder(WorkRepairOrderQuery search);

    WorkRepairOrderVO selectConfirmInfo(Integer id);

    void batchInsertMaterialUseRecord(List<WorkRepairMaterialUseRecord> list);

    WorkRepairOrderVO selectSubmitRepairInfo(Integer id);

    List<WorkRepairMaterialUseRecordDTO> selectMaterialUseRecordById(Integer id);

    Integer getRepairOrderCount(@Param("engineerId") Integer engineerId, @Param("status") Integer status);

    int replaceRepairEngineer(@Param("workOrderNo") String workOrderNo, @Param("engineerId") Integer engineerId, @Param("engineerName") String engineerName);

    int updateStationInfoByPCR(WorkRepairOrderVO updateWorkRepairOrder);

    int updateEngineerInfoByPCR(WorkRepairOrderVO updateWorkRepairOrder);

    int updateEngineerInfo(WorkRepairOrderVO wro);

    Integer getRepairModelTotalCount(@Param("engineerId") Integer engineerId);

    List<MapOrderDTO> getRepairWorkOrder(Integer engineerId);

}