package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.RegionMessageDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.RegionMessage;


/**
 * @author Lizhqiang
 * @date 2019/1/16
 */
public interface RegionMessageService {

    PageVO<RegionMessageDTO> pageQueryRegionMessage(RegionMessageDTO query, Integer pageNum, Integer pageSize);

    void update(RegionMessage regionMessage);
}
