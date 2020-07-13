package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.system.RegionMessageDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.RegionMessageMapper;
import com.yimao.cloud.system.po.RegionMessage;
import com.yimao.cloud.system.service.RegionMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Lizhqiang
 * @date 2019/1/16
 */
@Service
@Slf4j
public class RegionMessageServiceImpl implements RegionMessageService {


    @Resource
    private RegionMessageMapper regionMessageMapper;

    /**
     * 省市区管理分页显示
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<RegionMessageDTO> pageQueryRegionMessage(RegionMessageDTO query, Integer pageNum, Integer pageSize) {

        //开始分页
        PageHelper.startPage(pageNum, pageSize);
        Page<RegionMessageDTO> page = regionMessageMapper.pageQueryRegionMessage(query);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 更新区域信息
     *
     * @param regionMessage
     */
    @Override
    public void update(RegionMessage regionMessage) {
        int i = regionMessageMapper.updateByPrimaryKeySelective(regionMessage);
        if (i < 1) {
            throw new YimaoException("更新失败");
        }
    }


}
