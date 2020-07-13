package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.water.TrafficStatisticsDTO;
import com.yimao.cloud.pojo.vo.water.TrafficStatisticsVO;
import com.yimao.cloud.water.po.TrafficStatistics;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;


public interface TrafficStatisticsMapper extends Mapper<TrafficStatistics> {
    /***
     * 功能描述:根据平台查询流量统计数据
     *
     * @param: [platform, source]
     * @auther: liu yi
     * @date: 2019/6/13 14:24
     * @return: java.util.List<com.yimao.cloud.pojo.dto.water.TrafficStatisticsDTO>
     */
    List<TrafficStatisticsDTO> queryTrafficListByPlatform(@Param("platform")Integer platform,@Param("source") Integer source, @Param("sn")String sn);

    /***
     * 功能描述:根据sn分组查询流量统计数据
     *
     * @param: [platform, source, sn]
     * @auther: liu yi
     * @date: 2019/6/13 14:24
     * @return: com.github.pagehelper.Page<com.yimao.cloud.pojo.dto.water.TrafficStatisticsDTO>
     */
    Page<TrafficStatisticsVO> queryTrafficListBySn(@Param("source")Integer source, @Param("sn")String sn);


    /**
     * 批量插入
     *
     * @param list
     */
    void insertBatch(@Param("list")List<TrafficStatisticsDTO> list,@Param("platform")Integer platform);

    /***
     * 功能描述:7天以外的数据
     *
     * @param: [platform]
     * @auther: liu yi
     * @date: 2019/7/3 10:12
     * @return: java.util.List<java.lang.Integer>
     */
    List<Integer> getIdList7DaysAway(@Param("platform")Integer platform);

    void deleteBatch(@Param("platform")Integer platform, @Param("minId") Integer minId, @Param("maxId") Integer maxId);
}
