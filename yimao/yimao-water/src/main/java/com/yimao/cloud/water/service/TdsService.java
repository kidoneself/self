package com.yimao.cloud.water.service;

import com.yimao.cloud.water.po.Tds;

public interface TdsService {

    Tds getById(Integer id);

    void remove(Integer id);
}
