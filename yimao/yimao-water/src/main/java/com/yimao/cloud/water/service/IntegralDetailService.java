package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.IntegralDetailDTO;
import com.yimao.cloud.pojo.dto.water.IntegralDetailExportDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.IntegralDetail;

import java.util.Date;
import java.util.List;

public interface IntegralDetailService {

    /***
     * 功能描述:保存积分详情
     *
     * @param: [dto]
     * @auther: liu yi
     * @date: 2019/6/4 15:24
     * @return: void
     */
    void savePadIntegralDetail(IntegralDetailDTO dto);
    /**
     * 根据ID获取积分详情信息
     *
     * @param id 积分详情ID
     */
    IntegralDetailDTO getById(Integer id);

    /***
     * 功能描述:根据sn查找积分详情
     *
     * @param:
     * @auther: liu yi
     * @date: 2019/6/3 17:01
     * @return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.water.IntegralDetailDTO>
     */
    PageVO<IntegralDetailDTO> getDetailListBySn(String sn, Date startTime, Date endTime, Integer pageNum, Integer pageSize);

    /***
     * 功能描述:根据sn查找积分详情导出
     *
     * @param:
     * @auther: liu yi
     * @date: 2019/6/3 17:01
     * @return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.water.IntegralDetailDTO>
     */
    List<IntegralDetailExportDTO> getExportDetailListBySn(String sn, Date startTime, Date endTime);

    /***
     * 功能描述:根据规则id获取积分详情信息
     *
     * @param: [ruleId, sn, province, city, region]
     * @auther: liu yi
     * @date: 2019/6/3 16:44
     * @return: java.util.List<com.yimao.cloud.pojo.dto.water.IntegralDetailDTO>
     */
    PageVO<IntegralDetailDTO> getDetailListByRuleId(Integer ruleId,String sn,String province,String city,String region,Integer pageNum,Integer pageSize);
    /***
     * 功能描述:根据规则id导出积分信息
     *
     * @param: [ruleId, sn, province, city, region]
     * @auther: liu yi
     * @date: 2019/6/4 14:27
     * @return: java.util.List<com.yimao.cloud.pojo.dto.water.IntegralDetailDTO>
     */
    List<IntegralDetailDTO> getExportDetailListByRuleId(Integer ruleId, String sn, String province, String city, String region);
    /***
     * 功能描述:统计
     *
     * @param: [sn, startTime, endTime]
     * @auther: liu yi
     * @date: 2019/6/4 14:28
     * @return: int
     */
    int getCountBySn(String sn, Date startTime, Date endTime);

    /***
     * 功能描述:pad端设备有效积分
     *
     * @param: [sn]
     * @auther: liu yi
     * @date: 2019/6/4 14:28
     * @return: int
     */
    int getPadEffectiveTotalIntegralBySn(String sn);
    /**
     * 更新积分详情信息
     *
     * @param dto
     */
    void update(IntegralDetailDTO dto);

    /**
     * 根据积分详情ID删除积分详情
     *
     * @param detail 积分详情
     */
    void delete(IntegralDetail detail);
}
