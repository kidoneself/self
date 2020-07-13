package com.yimao.cloud.water.service.impl;

import com.yimao.cloud.water.mapper.TdsMapper;
import com.yimao.cloud.water.po.Tds;
import com.yimao.cloud.water.service.TdsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/5/9
 */
@Service
public class TdsServiceImpl implements TdsService {

    @Resource
    private TdsMapper tdsMapper;

    @Override
    public Tds getById(Integer id) {
        return tdsMapper.selectByPrimaryKey(id);
    }

    @Override
    public void remove(Integer id) {
        tdsMapper.deleteByPrimaryKey(id);
    }

}
