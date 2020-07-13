package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.system.AdvertPositionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.AdvertMapper;
import com.yimao.cloud.system.mapper.AdvertPositionMapper;
import com.yimao.cloud.system.po.Advert;
import com.yimao.cloud.system.po.AdvertPosition;
import com.yimao.cloud.system.service.AdvertPositionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author KID
 * @date 2018/11/13
 */

@Service
@Slf4j
public class AdvertPositionServiceImpl implements AdvertPositionService {


    @Resource
    private AdvertPositionMapper advertPositionMapper;

    @Resource
    private AdvertMapper advertMapper;


    /**
     * 获取广告位列表
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageVO<AdvertPositionDTO> listAdvertPosition(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        Example example = new Example(AdvertPosition.class);
        example.setOrderByClause("sorts desc");
        Page<AdvertPosition> advertPositionPage = (Page<AdvertPosition>) advertPositionMapper.selectByExample(example);
        if (advertPositionPage == null) {
            throw new YimaoException("操作失败。");
        }
        return new PageVO<>(page, advertPositionPage, AdvertPosition.class, AdvertPositionDTO.class);

    }

    /**
     * 保存广告位
     *
     * @param advertPosition
     * @return
     */
    @Override
    public AdvertPosition saveAdvertPosition(AdvertPosition advertPosition) {
        if (StringUtils.isEmpty(advertPosition.getTypeCode())) {
            throw new BadRequestException("信息不全，请检查！");
        }
        AdvertPosition position = advertPositionMapper.selectOne(advertPosition);
        if (advertPosition.getId() != null || position != null) {
            throw new BadRequestException("操作失败，对象已存在！");
        }
        advertPositionMapper.insertSelective(advertPosition);
        return advertPosition;
    }

    /**
     * 判断广告位是否存在广告
     *
     * @param typeCode
     * @return
     */
    @Override
    public boolean isExist(String typeCode) {
        AdvertPosition query = new AdvertPosition();
        query.setTypeCode(typeCode);
        AdvertPosition advertPosition = advertPositionMapper.selectOne(query);
        return advertPosition != null;
    }

    /**
     * 删除广告位
     *
     * @param id
     */
    @Override
    public void removeAdvertPosition(Integer id) {
        AdvertPosition advertPosition = advertPositionMapper.selectByPrimaryKey(id);
        Example example = new Example(Advert.class);
        example.createCriteria().andEqualTo("apId", id);
        List<Advert> adList = advertMapper.selectByExample(example);
        if (adList != null && adList.size() > 0) {
            throw new YimaoException("此广告位下有广告不能删除");
        } else {
            advertPositionMapper.delete(advertPosition);
        }
    }

    /**
     * 更新广告位
     *
     * @param advertPosition
     * @return
     */
    @Override
    public AdvertPosition updateAdvertPosition(AdvertPosition advertPosition) {
        if (advertPosition == null || advertPosition.getId() == null) {
            throw new BadRequestException("操作失败。");
        }
        if (StringUtils.isEmpty(advertPosition.getTypeCode())) {
            throw new BadRequestException("操作失败。");
        }
        AdvertPosition position = advertPositionMapper.selectOne(advertPosition);
        if (advertPosition.getId() != null || position != null) {
            throw new BadRequestException("名称不能重复.");
        }
        advertPosition.setUpdateTime(new Date());
        int i = advertPositionMapper.updateByPrimaryKeySelective(advertPosition);
        if (i < 1) {
            throw new YimaoException("操作失败。");
        }
        return advertPosition;
    }
}

