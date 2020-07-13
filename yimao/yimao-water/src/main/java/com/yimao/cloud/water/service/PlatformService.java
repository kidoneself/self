package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.PlatformDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.Platform;

public interface PlatformService {

    /**
     * 创建第三方广告平台
     *
     */
    void save(Platform adslot);

    void update(Platform adslot);

    /**
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<PlatformDTO> page(Integer pageNum, Integer pageSize,Integer forbidden);

    void deletePlatform( Platform platform);

    PlatformDTO getById(Integer id);
}
