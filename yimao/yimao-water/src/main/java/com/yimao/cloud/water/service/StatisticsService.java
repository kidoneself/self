package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.EffectStatisticsDTO;
import com.yimao.cloud.pojo.dto.water.EffectStatisticsForAppDTO;
import com.yimao.cloud.pojo.dto.water.VersionDetailStatisticsDTO;
import com.yimao.cloud.pojo.dto.water.VersionStatisticsDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StatisticsService {

    PageVO<EffectStatisticsDTO> queryEffectDetail(Integer materielId, Integer advertisingId, String adslotId, Date time, String snCode, Integer pageNum, Integer pageSize);

    PageVO<EffectStatisticsDTO> queryDetail(Integer materielId, Integer advertisingId, String adslotId, Date beginTime, Date endTime, Integer pageNum, Integer pageSize);

    PageVO<EffectStatisticsDTO> queryEffectListByCondition(Integer platform, Integer screenLocation, String materielName, Integer pageNum, Integer pageSize);
    /***
     * 功能描述:根据sn查询效果统计
     *
     * @param: [snCode, beginTime, endTime, pageNum, pageSize]
     * @auther: liu yi
     * @date: 2019/6/14 10:49
     * @return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.water.EffectStatisticsDTO>
     */
    PageVO<EffectStatisticsDTO> queryEffectListBySn(String snCode, Date beginTime, Date endTime, Integer pageNum, Integer pageSize);
    /***
     * 功能描述:根据sn查询效果统计数量
     *
     * @param: [snCode, beginTime, endTime]
     * @auther: liu yi
     * @date: 2019/6/14 15:50
     * @return: com.yimao.cloud.pojo.dto.water.EffectStatisticsDTO
     */
    EffectStatisticsDTO queryEffectCountBySn(String snCode, Date beginTime, Date endTime);

    void saveEffect(List<EffectStatisticsForAppDTO> list);

    EffectStatisticsDTO queryEffectDetailByAdvertising(Integer advertisingId);

    void exportEffect(Integer platform, Integer screenLocation, String materielName, HttpServletResponse response);

    void exportEffectDetail(Integer materielId, Integer advertisingId, String adslotId, Date beginTime, Date endTime, HttpServletResponse response);

    void exportEffectDetailByDay(Integer materielId, Integer advertisingId, String adslotId, Date time, String snCode, HttpServletResponse response);

    Map<String, Object> queryEffectCountByYesterday();

    List<VersionStatisticsDTO> queryVersionList();

    void saveVersion(VersionDetailStatisticsDTO dto);

    PageVO<WaterDeviceVO> queryVersionDetailList(Integer areaId, String model, String version, Integer deviceGroup, String snCode, Integer pageNum, Integer pageSize);

}
