package com.yimao.cloud.order.jobs;

import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.framework.cache.RedisLock;
import com.yimao.cloud.order.dto.RankingQuery;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.mapper.DealerLeaderBoardMapper;
import com.yimao.cloud.order.mapper.StationLeaderBoardMapper;
import com.yimao.cloud.order.po.DealerSales;
import com.yimao.cloud.order.po.StationSales;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/3/11
 */
@Slf4j
@Component
@EnableScheduling
public class LeaderBoardTask {

    private static final String LEADER_BOARD_LOCK_1 = "LEADER_BOARD_LOCK_1";
    private static final String LEADER_BOARD_LOCK_2 = "LEADER_BOARD_LOCK_2";

    @Resource
    private RedisLock redisLock;

    @Resource
    private DealerLeaderBoardMapper dealerLeaderBoardMapper;

    @Resource
    private StationLeaderBoardMapper stationLeaderBoardMapper;

    @Resource
    private SystemFeign systemFeign;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");


    /**
     * 经销商翼猫榜数据定时任务
     */
    @Scheduled(cron = "0 00 23 ? * SUN")
    public void DealerLeaderJob() {
        try {
            //获取锁
            if (!redisLock.lockWithTimeout(LEADER_BOARD_LOCK_1, 300L)) {
                return;
            }
            //获取所有经销商业绩，排名，id的集合
            List<RankingQuery> query = dealerLeaderBoardMapper.getDealerNationalRankingLastWeek();
            DealerSales insert = new DealerSales();
            //遍历该集合查找相等id的经销商
            for (RankingQuery rq : query) {
                Integer ranking = rq.getRanking().intValue();
                insert.setSort(ranking);
                insert.setDealerId(rq.getId());
                insert.setSalesAccount(rq.getMoney());
                //获取上周的排名，
                DealerSales queryLast = new DealerSales();
                queryLast.setBatchId(this.getOldBatchId());
                queryLast.setDealerId(rq.getId());
                DealerSales oldDealerSales = dealerLeaderBoardMapper.selectOne(queryLast);
                if (oldDealerSales == null) {
                    insert.setLastSort(null);
                    insert.setChampionCount(null);
                } else {
                    insert.setLastSort(oldDealerSales.getSort());
                    //如果这次是第一
                    if (rq.getRanking() == 1) {
                        if (oldDealerSales.getChampionCount() == null) {
                            insert.setChampionCount(1);
                        } else {
                            insert.setChampionCount(oldDealerSales.getChampionCount() + 1);
                        }
                    } else {
                        insert.setChampionCount(0);
                    }
                }
                insert.setCreateTime(new Date());
                //每周新数据点赞数为0
                insert.setLinkCount(0);
                //设置本周批次
                insert.setBatchId(this.getBatchId());
                StationDTO stationDTO = systemFeign.getStationByPRC(rq.getProvince(), rq.getCity(), rq.getRegion(), PermissionTypeEnum.PRE_SALE.value);
                insert.setStationId(stationDTO.getId());
                insert.setDealerName(stationDTO.getName());
                int i = dealerLeaderBoardMapper.insert(insert);
                if (i < 1) {
                    throw new YimaoException(500, "插入失败");
                }
            }
        } finally {
            redisLock.unLock(LEADER_BOARD_LOCK_1);
        }
    }


    /**
     * 服务站翼猫榜定时任务
     */
    @Scheduled(cron = "0 00 23 ? * SUN")
    public void StationLeaderJob() {
        try {
            //获取锁
            if (!redisLock.lockWithTimeout(LEADER_BOARD_LOCK_2, 300L)) {
                return;
            }
            //获取所有服务站业绩，排名，id的集合
            List<RankingQuery> query = stationLeaderBoardMapper.getAllStationNationalRankingLastWeek();
            //新建服务站排行榜po
            StationSales insert = new StationSales();
            //遍历该集合查找相等region的服务站
            for (RankingQuery rq : query) {
                Integer ranking = rq.getRanking().intValue();
                insert.setSort(ranking);
                insert.setSalesAccount(rq.getMoney());
                insert.setProvince(rq.getProvince());
                insert.setCity(rq.getCity());
                insert.setRegion(rq.getRegion());
                StationDTO stationDTO = systemFeign.getStationByPRC(rq.getProvince(), rq.getCity(), rq.getRegion(),PermissionTypeEnum.PRE_SALE.value);
                insert.setStationId(stationDTO.getId());
                insert.setStationName(stationDTO.getName());
                //获取上周的排名，
                StationSales queryLast = new StationSales();
                queryLast.setBatchId(this.getOldBatchId());
                queryLast.setStationId(stationDTO.getId());
                StationSales oldStationSales = stationLeaderBoardMapper.selectOne(queryLast);
                if (oldStationSales == null) {
                    //上周没数据
                    insert.setLastSort(null);
                    insert.setChampionCount(null);
                } else {
                    insert.setLastSort(oldStationSales.getSort());
                    //如果这次是第一
                    if (rq.getRanking() == 1) {
                        if (oldStationSales.getChampionCount() == null) {
                            insert.setChampionCount(1);
                        } else {
                            insert.setChampionCount(oldStationSales.getChampionCount() + 1);
                        }
                    } else {
                        insert.setChampionCount(0);
                    }
                }
                insert.setCreateTime(new Date());
                //每周新数据点赞数为0
                insert.setLinkCount(0);
                //设置本周批次
                insert.setBatchId(this.getBatchId());

                int i = stationLeaderBoardMapper.insert(insert);
                if (i < 1) {
                    throw new YimaoException(500, "插入失败");
                }
            }
        } finally {
            redisLock.unLock(LEADER_BOARD_LOCK_2);
        }
    }

    private Integer getOldBatchId() {
        //根据上周批次获取上周数据
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        log.info("上批次: " + sdf.format(calendar.getTime()));
        Integer oldBatchId = Integer.parseInt(sdf.format(calendar.getTime()));
        return oldBatchId;
    }

    private Integer getBatchId() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, 7);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        log.info("本批次: " + sdf.format(calendar.getTime()));
        Integer batchId = Integer.parseInt(sdf.format(calendar.getTime()));
        return batchId;
    }
}
