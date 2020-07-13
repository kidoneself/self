package com.yimao.cloud.system.service.impl;

import com.yimao.cloud.system.mapper.ReasonMapper;
import com.yimao.cloud.system.po.Reason;
import com.yimao.cloud.system.service.ReasonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhilin.he
 * @description 原因管理
 * @date 2019/5/10 10:20
 **/
@Service
@Slf4j
public class ReasonServiceImpl implements ReasonService {

    @Resource
    private ReasonMapper reasonMapper;

    @Override
    public List<Reason> listByType(Integer type) {
        return reasonMapper.selectByType(type);
    }

}
