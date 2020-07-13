package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.TrafficStatisticsDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.TrafficStatisticsVO;

import java.util.List;
import java.util.Map;

public interface TrafficStatisticsService {


    /***
     * 功能描述:根据平台查询流量统计数据
     *
     * @param: [platform, screenLocation, materielName, pageNum, pageSize]
     * @auther: liu yi
     * @date: 2019/6/12 15:54
     * @return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.water.TrafficStatisticsDTO>
     */
    Map<Integer, List<TrafficStatisticsDTO>> queryTrafficListByPlatform(Integer source, String sn);

    /***
     * 功能描述:根据sn查询设备流量统计列表
     *
     * @param: [platform, screenLocation, materielName, pageNum, pageSize]
     * @auther: liu yi
     * @date: 2019/6/12 15:54
     * @return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.water.TrafficStatisticsDTO>
     */
    PageVO<TrafficStatisticsVO> queryTrafficListBySn(Integer source, String sn, Integer pageNum, Integer pageSize);

    /***
     * 功能描述:保存流量统计数据
     *
     * @param: [list]
     * @auther: liu yi
     * @date: 2019/6/12 15:54
     * @return: void
     */
    void saveTraffic(List<TrafficStatisticsDTO> list);
}
