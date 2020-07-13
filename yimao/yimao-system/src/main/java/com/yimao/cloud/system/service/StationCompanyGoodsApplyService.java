package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.StationCompanyGoodsApplyDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.Date;

/**
 * @author Liu Long Jie
 * @date 2020-6-18 14:37:57
 */
public interface StationCompanyGoodsApplyService {

    PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyFirstAudit(Integer pageNum, Integer pageSize, String province, String city, String region, Integer categoryId);

    PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyAfterAudit(Integer pageNum, Integer pageSize, String province, String city, String region, Integer categoryId);

    PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyTwoAudit(Integer pageNum, Integer pageSize, String province, String city, String region, Integer categoryId, Integer status, Integer isAfterAudit);

    PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyHistory(Integer pageNum, Integer pageSize, String province, String city, String region, Integer isAfterAudit);

    PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyStation(Integer pageNum, Integer pageSize, Integer stationCompanyId, Integer categoryId, Date startTime, Date endTime);

    PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyStationHistory(Integer pageNum, Integer pageSize, Integer stationCompanyId, Integer categoryId, Integer status, Date startTime, Date endTime);

    void goodsApplyFirstAudit(String id, Integer isPass, String cause,Integer type);

    void goodsApplyTwoAudit(String id, Integer isPass, String cause);

    void goodsApplyDeliverGoods(String id);

    void goodsApplyAnewSubmit(StationCompanyGoodsApplyDTO dto);

    void goodsApplySave(StationCompanyGoodsApplyDTO dto);

    void goodsApplyConfirm(String id, String confirmImg);

    void goodsApplyCancel(String id);
}
