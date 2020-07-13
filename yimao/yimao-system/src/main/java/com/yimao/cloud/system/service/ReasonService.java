package com.yimao.cloud.system.service;

import com.yimao.cloud.system.po.Reason;

import java.util.List;

public interface ReasonService {

    List<Reason> listByType(Integer type);

}
