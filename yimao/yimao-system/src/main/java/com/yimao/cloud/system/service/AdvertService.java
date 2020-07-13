package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.AdvertDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.Advert;

/**
 * @author KID
 * @date 2018/11/12
 */

public interface AdvertService {
    /**
     * 获取广告列表
     *
     * @param apId
     * @param title
     * @param conditions
     * @param page
     * @param size
     * @return
     */
    PageVO<AdvertDTO> listAdvert(Integer apId, String title, Integer conditions, Integer page, Integer size);

    /**
     * 添加广告
     *
     * @param advert
     * @return
     */
    Advert saveAdvert(Advert advert);

    /**
     * 更新广告
     *
     * @param advert
     * @return
     */
    Advert updateAdvert(Advert advert);

    /**
     * 删除广告
     *
     * @param advert
     * @return
     */
    void removeAdvert(Advert advert);

    /**
     * 广告上下线
     *
     * @param id
     * @return
     */
    Advert updateAdvertState(Integer id);

    /**
     * 查询广告
     *
     * @param id
     * @return
     */
    Advert queryAdvert(Integer id);
}
