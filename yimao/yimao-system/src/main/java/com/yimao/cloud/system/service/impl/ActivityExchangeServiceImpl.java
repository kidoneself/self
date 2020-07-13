package com.yimao.cloud.system.service.impl;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.system.mapper.DictionaryMapper;
import com.yimao.cloud.system.po.Dictionary;
import com.yimao.cloud.system.service.ActivityExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/4/19
 */
@Service
@Slf4j
public class ActivityExchangeServiceImpl implements ActivityExchangeService {


    @Resource
    private DictionaryMapper dictionaryMapper;


    @Override
    public Dictionary exchangeAstrict(String exchange) {
        Example example = new Example(Dictionary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("DICTYPE", exchange);
        List<Dictionary> dic = dictionaryMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(dic)) {
            return dic.get(0);
        }
        throw new NotFoundException("未找到相关字典数据");
    }

    @Override
    public String findSideOrChannel(String dicType, List<String> dicCode, String msgName) {
        Example example = new Example(Dictionary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("groupCode", dicType);
        criteria.andIn("code", dicCode);
        List<Dictionary> dicList = dictionaryMapper.selectByExample(example);
        StringBuffer buffer;
        if (CollectionUtil.isNotEmpty(dicList)) {
            buffer = new StringBuffer();
            String dicValue;
            for (Dictionary dic : dicList) {
                dicValue = dic.getName();
                //截取后面的批次码代码
                if (dicValue.contains("(")) {
                    dicValue = dicValue.substring(0, dicValue.lastIndexOf("("));
                }
                buffer.append(dicValue).append("/");
            }
            String buff = buffer.toString();
            if (buff.contains("/")) {
                return buff.substring(0, buff.lastIndexOf("/"));
            }
            return buff;
        }
        return msgName;
    }
}
