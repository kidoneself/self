package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.system.AdvertDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.AdvertMapper;
import com.yimao.cloud.system.po.Advert;
import com.yimao.cloud.system.service.AdvertService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author KID
 * @date 2018/11/12
 */
@Service
@Slf4j
public class AdvertServiceImpl implements AdvertService {

    @Resource
    private AdvertMapper advertMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 分页查询活动信息
     *
     * @param apId
     * @param title
     * @param conditions
     * @param
     * @param
     * @return
     */
    @Override
    public PageVO<AdvertDTO> listAdvert(Integer apId, String title, Integer conditions, Integer pageNum, Integer pageSize) {
        Example example = new Example(Advert.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(title)) {
            criteria.andEqualTo("title", "%" + title + "%");
        }
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<Advert> AdvertPage = (Page<Advert>) advertMapper.selectByExample(example);
        return new PageVO<>(pageNum, AdvertPage, Advert.class, AdvertDTO.class);
    }


    /**
     * 添加活动
     *
     * @param advert
     * @return
     */
    @Override
    public Advert saveAdvert(Advert advert) {
        if (StringUtils.isEmpty(advert.getName()) && advert.getApId() == null) {
            throw new BadRequestException("标题不能为空。");
        }
        advert.setCreateTime(new Date());
        int insert = advertMapper.insert(advert);
        if (insert < 1) {
            throw new YimaoException("操作失败。");
        }
        return advert;

    }

    /**
     * 修改广告
     *
     * @param advert
     * @return
     */
    @Override
    public Advert updateAdvert(Advert advert) {

        Advert ad = advertMapper.selectByPrimaryKey(advert.getId()); //原来的
        if (ad == null) {
            throw new BadRequestException("操作对象不存在。");
        }
        advert.setUpdateTime(new Date());
        int count = advertMapper.updateByPrimaryKeySelective(advert);   //现在的
        if (count > 0) {
            return advert;
        }
        throw new YimaoException("操作失败。");
    }

    /**
     * 删除广告
     *
     * @param advert
     * @return
     */
    @Override
    public void removeAdvert(Advert advert) {
        if (advert == null) {
            throw new YimaoException("操作对象不存在。");
        }
        int count = advertMapper.delete(advert);
        if (count < 1) {
            throw new NotFoundException("删除广告失败");
        }
    }

    /**
     * 广告上下线
     *
     * @param id
     * @return
     */
    @Override
    public Advert updateAdvertState(Integer id) {
        Advert advert = advertMapper.selectByPrimaryKey(id);
        if (advert != null) {
            if (advert.getConditions() == 0) {
                advert.setConditions(-1);
            } else if (advert.getConditions() == -1) {
                advert.setConditions(0);
            }
            int count = advertMapper.updateByPrimaryKey(advert);
            if (count < 1) {
                throw new NotFoundException("广告上下线失败");
            }
            return advert;
        }
        throw new NotFoundException("广告不存在");
    }

    /**
     * 查询广告
     *
     * @param id
     * @return
     */
    @Override
    public Advert queryAdvert(Integer id) {
        return advertMapper.selectByPrimaryKey(id);
    }


}
