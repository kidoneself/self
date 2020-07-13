package com.yimao.cloud.hra.service.impl;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.CopyUtil;
import com.yimao.cloud.hra.mapper.HraBodyElementMapper;
import com.yimao.cloud.hra.po.HraBodyElement;
import com.yimao.cloud.hra.service.HraBodyElementService;
import com.yimao.cloud.pojo.dto.hra.HraBodyElementDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/10/31.
 */
@Service
@Slf4j
public class HraBodyElementServiceImpl implements HraBodyElementService {

    @Resource
    private HraBodyElementMapper hraBodyElementMapper;

    @Override
    public List<HraBodyElementDTO> getBodyElements() {
        List<HraBodyElement> elementList = hraBodyElementMapper.selectBodyElements();
        List<HraBodyElementDTO> list = CopyUtil.copyList(elementList, HraBodyElement.class, HraBodyElementDTO.class);
        if (CollectionUtil.isNotEmpty(list)) {
            return list;
        }
        throw new NotFoundException("获取身体元素为空");
    }

}
