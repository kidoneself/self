package com.yimao.cloud.order.mapper;

import com.yimao.cloud.order.dto.RankingQuery;
import com.yimao.cloud.order.po.StationSales;
import com.yimao.cloud.pojo.dto.order.StationSalesDTO;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/2/28
 */
public interface StationLeaderBoardMapper extends Mapper<StationSales> {
    BigDecimal taskStationRanking(String region);

    List<StationSalesDTO> getStationNationalRankingLastWeek(Integer BatchId);

    StationSales getSubMyStationRanking(String region, Integer batchId);

    StationSales getDealerMyStationRanking(String region, Integer batchId);

    StationSales getFirstStationRanking(Integer batchId);

    Integer insertLinkId(Integer id, Integer dealerSalesId);

    StationSales selectById(Integer rankingId);

    Integer selectLinkRecord(Integer dealerId, Integer rankingId);

    Integer deleteLinkRecord(Integer id);

    List<RankingQuery> getAllStationNationalRankingLastWeek();

    Integer getHasPraised(Integer stationSalesDTOId, Integer dealerId);

    StationSalesDTO getLinkCount(Integer oldBatchId, int j);
}
