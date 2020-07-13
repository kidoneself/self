package com.yimao.cloud.hra.service;

import com.yimao.cloud.pojo.dto.hra.HraBodyElementDTO;
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/10/31.
 */
public interface HraBodyElementService {

    /**
     * 获取身体元素信息
     *
     * @return
     */
    List<HraBodyElementDTO> getBodyElements();
}
