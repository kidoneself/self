package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.query.water.SimCardNumberQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.SimCardNumberVO;
import com.yimao.cloud.water.po.SimCardNumber;

public interface SimCardNumberService {

    /**
     * 创建SIM号码段
     *
     * @param simCardNumber SIM号码段
     */
    void save(SimCardNumber simCardNumber);

    /**
     * 修改SIM号码段
     *
     * @param simCardNumber SIM号码段
     */
    void update(SimCardNumber simCardNumber);

    /**
     * 查询SIM号码段（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    PageVO<SimCardNumberVO> page(Integer pageNum, Integer pageSize, SimCardNumberQuery query);

}
