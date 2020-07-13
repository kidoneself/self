package com.yimao.cloud.user.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.EngineerExportDTO;
import com.yimao.cloud.pojo.query.station.StationEngineerQuery;
import com.yimao.cloud.pojo.query.user.EngineerQuery;
import com.yimao.cloud.pojo.vo.station.EngineerVO;
import com.yimao.cloud.user.po.Engineer;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface EngineerMapper extends Mapper<Engineer> {

    void batchInsert(List<Engineer> list);

    boolean existsWithOldId(@Param("oldId") String oldId);

    Page<EngineerDTO> selectPage(EngineerQuery query);

    int updateIccidToNull(@Param("id") Integer id, @Param("updater") String updater, @Param("updateTime") Date updateTime);

    Page<EngineerExportDTO> exportEngineer(EngineerQuery query);

    EngineerDTO selectBasicInfoByIdForMsgPushInfo(@Param("id") Integer id);

    int bindingIccid(@Param("id") Integer id, @Param("updater") String updater, @Param("iccid") String iccid);

    Engineer selectOneByUserName(@Param("userName") String userName);

    Engineer selectOneByPhone(@Param("phone") String phone);

    Engineer selectStateAndCountById(@Param("id") Integer id);

    Page<EngineerVO> pageEngineerInfoToStation(StationEngineerQuery query);

    List<Integer> getEngineerIdsByAreaIds(@Param("areaIds") Set<Integer> areaIds);

    List<EngineerVO> getEngineerListByStationIds(@Param("stationIds") Set<Integer> stationIds);

	List<EngineerDTO> getEngineerListByStationId(@Param("stationId") Integer stationId,@Param("engineerId") Integer engineerId);

	List<EngineerDTO> getEngineerListByArea(@Param("areaId") Integer areaId);

	int selectCountByArea(Engineer query);

    List<EngineerDTO> getEngineerByStationId(@Param("stationId") Integer stationId);
}
