package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.OrderFeign;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.order.DealerSalesDTO;
import com.yimao.cloud.pojo.dto.order.StationSalesDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/2/26
 */
@RestController
@Slf4j
public class LeaderBoardController {

    @Resource
    private OrderFeign orderFeign;

    /**
     * 经销商：我的全国排名(头部)
     *
     * @return
     */
    @GetMapping(value = "/ranking/dealer/self")
    @ApiOperation(notes = "我的全国排名", value = "我的全国排名")
    public Object dealerNationalRanking() {
        DealerSalesDTO dto = orderFeign.dealerNationalRanking();
        return dto;
    }

    /**
     * 经销商：全国TOP10
     *
     * @return
     */
    @GetMapping(value = "/ranking/dealer/top")
    @ApiOperation(notes = "经销商：全国TOP10", value = "经销商：全国TOP10")
    public Object topDealerNationalRanking() {
        List<DealerSalesDTO> topList = orderFeign.topDealerNationalRanking();
        return ResponseEntity.ok(topList);
    }


    /**
     * 点赞
     *
     * @return
     */
    @GetMapping(value = "/ranking/dealer/{rankingId}")
    @ApiOperation(notes = "点赞", value = "点赞")
    @ApiImplicitParam(name = "rankingId", value = "排行榜id", dataType = "Long", required = true, paramType = "path")
    public Object rankingDealerLink(@PathVariable(value = "rankingId") Integer rankingId) {
        orderFeign.rankingDealerLink(rankingId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 服务站：我的全国排名(头部)
     *
     * @return
     */
    @GetMapping(value = "/ranking/station/self")
    @ApiOperation(notes = "服务站我的全国排名", value = "服务站我的全国排名")
    public Object stationNationalRanking() {

        StationSalesDTO dto = orderFeign.stationNationalRanking();
        return ResponseEntity.ok(dto);
    }

    /**
     * 服务站：全国TOP10
     *
     * @return
     */
    @GetMapping(value = "/ranking/station/top")
    @ApiOperation(notes = "服务站：全国TOP10", value = "服务站：全国TOP10")
    public Object topStationNationalRanking() {
        List<StationSalesDTO> topList = orderFeign.topStationNationalRanking();
        return ResponseEntity.ok(topList);
    }

    /**
     * 点赞服务站
     *
     * @return
     */
    @GetMapping(value = "/ranking/station/{rankingId}")
    @ApiOperation(notes = "点赞", value = "点赞")
    @ApiImplicitParam(name = "rankingId", value = "排行榜id", dataType = "Long", required = true, paramType = "path")
    public Object rankingStationLink(@PathVariable(value = "rankingId") Integer rankingId) {
        orderFeign.rankingStationLink(rankingId);
        return ResponseEntity.noContent().build();
    }
}
