package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.DealerSalesDTO;
import com.yimao.cloud.pojo.dto.order.StationSalesDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;

import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/2/25
 */
public interface LeaderBoardService {

    DealerSalesDTO dealerNationalRanking(DistributorDTO distributorDTO);

    List<DealerSalesDTO> getTopDealerNationalRanking(Integer dealerId);

    void rankingDealerLink(Integer id, Integer rankingId);

    StationSalesDTO stationNationalRanking(DistributorDTO distributorDTO);

    List<StationSalesDTO> getTopStationNationalRanking(Integer dealerId);

    void rankingStationLink(Integer id, Integer rankingId);
}
