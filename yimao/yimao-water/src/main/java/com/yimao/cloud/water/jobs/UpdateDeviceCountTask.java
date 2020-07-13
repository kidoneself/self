package com.yimao.cloud.water.jobs;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.framework.cache.RedisLock;
import com.yimao.cloud.pojo.dto.out.DeviceQuery;
import com.yimao.cloud.water.mapper.ServiceStationWaterDeviceMapper;
import com.yimao.cloud.water.mapper.VersionDetailStatisticsMapper;
import com.yimao.cloud.water.mapper.VersionStatisticsMapper;
import com.yimao.cloud.water.po.ServiceStationWaterDevice;
import com.yimao.cloud.water.po.VersionDetailStatistics;
import com.yimao.cloud.water.po.VersionStatistics;
import com.yimao.cloud.water.service.WaterDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author Chen Hui Yang
 * @date 2019/4/11
 */
@Slf4j
@DisallowConcurrentExecution//该注解表示前一个任务执行结束再执行下一次任务
public class UpdateDeviceCountTask extends QuartzJobBean {

    private static final String UPDATE_DEVICE_COUNT_LOCK_STR = "UPDATE_DEVICE_COUNT_LOCK_STR";

    @Resource
    private WaterDeviceService waterDeviceService;
    @Resource
    private VersionStatisticsMapper versionStatisticsMapper;
    @Resource
    private VersionDetailStatisticsMapper versionDetailStatisticsMapper;
    @Resource
    private ServiceStationWaterDeviceMapper serviceStationWaterDeviceMapper;

    @Resource
    private RedisLock redisLock;


    /**
     * 描述： 每天零点时刻更新版本统计的设备总数和成功更新设备数
     *
     * @param context
     */
    @Override
    protected void executeInternal(JobExecutionContext context) {
        try {
            //获取锁
            if (!redisLock.lockWithTimeout(UPDATE_DEVICE_COUNT_LOCK_STR, 300L)) {
                return;
            }
            Example example = new Example(VersionStatistics.class);
            example.setOrderByClause("version desc");
            //根据版本号进行排序查询版本列表
            List<VersionStatistics> list = versionStatisticsMapper.selectByExample(example);
            if (CollectionUtil.isNotEmpty(list)) {
                //只更新数组中的第一个
                VersionStatistics ver = list.get(0);
                //根据当前时间查询设备数量
                DeviceQuery deviceQuery = new DeviceQuery();
                deviceQuery.setCreateTime(new Date());
                //查询用户组设备总数
                // Long device = outFeign.countDevice(deviceQuery);
                int device = waterDeviceService.countDevice(deviceQuery);
                // 查询服务站设备总数
                int stationCount = serviceStationWaterDeviceMapper.selectCount(new ServiceStationWaterDevice());
                int count = device + stationCount;

                //查询当前版本已经更新的设备数量
                Example example1 = new Example(VersionDetailStatistics.class);
                Example.Criteria criteria = example1.createCriteria();
                criteria.andEqualTo("version", ver.getVersion());
                int i = versionDetailStatisticsMapper.selectCountByExample(example1);

                //更新设备总数量
                VersionStatistics versionStatistics = new VersionStatistics();
                versionStatistics.setId(ver.getId());
                versionStatistics.setDeviceCount(count);
                versionStatistics.setSuccessCount(i);
                versionStatisticsMapper.updateByPrimaryKeySelective(versionStatistics);
            }
        } finally {
            redisLock.unLock(UPDATE_DEVICE_COUNT_LOCK_STR);
        }
    }
}
