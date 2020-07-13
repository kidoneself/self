package com.yimao.cloud.system.service;


import com.yimao.cloud.system.po.Dictionary;

import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/4/19
 */
public interface ActivityExchangeService {

    Dictionary exchangeAstrict(String exchange);

    String findSideOrChannel(String dicType, List<String> dicCode, String msgName);
}
