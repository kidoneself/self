package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.AdvertPositionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.AdvertPosition;

/**
 * @author KID
 * @date 2018/11/13
 */

public interface AdvertPositionService {
    /**
     * 广告位查询
     *
     * @param page
     * @param size
     * @return
     */
    PageVO<AdvertPositionDTO> listAdvertPosition(Integer page, Integer size);

    /**
     * 添加广告位
     *
     * @param advertPosition
     * @return
     */
    AdvertPosition saveAdvertPosition(AdvertPosition advertPosition);

    /**
     * 判断广告位是否存在
     *
     * @param typeCode
     * @return
     */
    boolean isExist(String typeCode);

    /**
     * 删除广告位
     *
     * @param id
     */
    void removeAdvertPosition(Integer id);

    /**
     * 更新广告位
     *
     * @param advertPosition
     * @return
     */
    AdvertPosition updateAdvertPosition(AdvertPosition advertPosition);

}
