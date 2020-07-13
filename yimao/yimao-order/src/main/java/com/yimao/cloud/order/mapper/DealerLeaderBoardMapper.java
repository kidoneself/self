package com.yimao.cloud.order.mapper;

import com.yimao.cloud.order.dto.RankingQuery;
import com.yimao.cloud.order.po.DealerSales;
import com.yimao.cloud.pojo.dto.order.DealerSalesDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/2/25
 */
public interface DealerLeaderBoardMapper extends Mapper<DealerSales> {
    //    BigDecimal dealerNationalRanking(Integer id);
    List<Integer> getDealerIdList();

    List<RankingQuery> getDealerNationalRankingLastWeek();

    DealerSales dealerNationalRanking(@Param("dealerId") Integer dealerId, @Param("batchId") Integer batchId);

    List<DealerSalesDTO> getTopDealerNationalRanking(Integer batchId);

    Integer insertLinkId(@Param("dealerId") Integer dealerId, @Param("rankingId") Integer rankingId);

    DealerSales subDealerNationalRanking(@Param("pid") Integer pid, @Param("batchId") Integer batchId);

    DealerSales agentNationalRanking();

    Integer getHasPraised(@Param("dealerSalesId") Integer dealerSalesId, @Param("dealerId") Integer dealerId);

    Integer selectLinkRecord(@Param("dealerId") Integer dealerId, @Param("rankingId") Integer rankingId);

    Integer deleteLinkRecord(Integer id);
}
