package com.yimao.cloud.system.jobs;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.system.mapper.StationMapper;
import com.yimao.cloud.system.po.Station;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/6/11
 */

@Slf4j
@Component
@EnableScheduling
public class UpdateContractStatus {

    @Resource
    private StationMapper stationMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateContractStatus() {
        log.info("开始更新承包状态");
        List<Integer> hasids = new ArrayList<>();
        List<Integer> noids = new ArrayList<>();

        List<Station> stations = stationMapper.selectAll();
        if (CollectionUtil.isNotEmpty(stations)) {
            for (int i = 0; i < stations.size(); i++) {
                Station station = stations.get(i);
                Date contractEndTime = station.getContractEndTime();
                Date contractStartTime = station.getContractStartTime();
                Date date = new Date();
                //如果承包时间有一个为空continue
                if (contractEndTime == null || contractStartTime == null) {
                    noids.add(station.getId());
                    continue;
                }

                //已经承包   start  当前时间在 end
                if ((date).before(contractEndTime) && (date).after(contractStartTime)) {
                    hasids.add(station.getId());
                } else {
                    noids.add(station.getId());
                }
            }

            log.info("开始将未承包的更新为已承包");
            Example hasidsExample = new Example(Station.class);
            Example.Criteria hasidsCriteria = hasidsExample.createCriteria();
            hasidsCriteria.andIn("id", hasids);
            Station hasidsStation = new Station();
            hasidsStation.setContract(true);
            stationMapper.updateByExampleSelective(hasidsStation, hasidsExample);
        /*========================================================================================================*/
            log.info("开始更新承包时间为空，或者承包已到期的");
            Example noidsExample = new Example(Station.class);
            Example.Criteria noidsEriteria = noidsExample.createCriteria();
            noidsEriteria.andIn("id", noids);
            Station noidsStation = new Station();
            noidsStation.setContract(false);
            stationMapper.updateByExampleSelective(noidsStation, noidsExample);
        }

        log.info("承包状态更新结束");
    }
}
