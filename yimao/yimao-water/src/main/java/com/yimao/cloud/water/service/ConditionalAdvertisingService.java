package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.water.ConditionalAdvertisingDTO;
import com.yimao.cloud.pojo.dto.water.FilterAdvertisingDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.ConditionalAdvertising;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ConditionalAdvertisingService {

    /**
     * 创建广告投放策略配置
     *
     * @param advertising 广告条件投放
     */
    void save(ConditionalAdvertising advertising);

    /**
     * 分页查询第三方投放列表
     *
     * @param platform 第三方广告平台
     * @param pageNum  页码
     * @param pageSize 页数
     * @return
     */
    PageVO<ConditionalAdvertisingDTO> page(Integer platform, Integer pageNum, Integer pageSize);


    /**
     * 分页查询自有投放列表
     *
     * @param screenLocation
     * @param startTime
     * @param endTime
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<ConditionalAdvertisingDTO> pageOwnList(Integer screenLocation, Date startTime, Date endTime, String name, String type, Integer pageNum, Integer pageSize);


    /**
     * 更新投放配置
     *
     * @param advertising
     */
    void update(ConditionalAdvertising advertising);

    void deleteConditionalAdvertising(ConditionalAdvertising advertising);

    void lowerAdvertising(ConditionalAdvertising advertising);

    Set<FilterAdvertisingDTO> checkAdvertisingExists(ConditionalAdvertising ad, Set<AreaDTO> areaSet, Set<String> modelSet);

    Map<String, Object> filter(ConditionalAdvertisingDTO advertising);

    Boolean judgeAdvertsingExists(Integer platform, String ownAdslotId, String adslotId);

    void exportHave(Integer platform, Integer screenLocation, Date startTime, Date endTime, String name, String type, HttpServletResponse response);

    ConditionalAdvertisingDTO selectAdById(Integer id);
}
