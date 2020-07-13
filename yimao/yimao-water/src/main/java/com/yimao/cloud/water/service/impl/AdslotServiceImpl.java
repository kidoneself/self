package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.framework.aop.OperationType;
import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;
import com.yimao.cloud.framework.aop.parser.DefaultContentParse;
import com.yimao.cloud.pojo.dto.water.AdslotDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.enums.ScreenLocationEnum;
import com.yimao.cloud.water.mapper.AdslotMapper;
import com.yimao.cloud.water.po.Adslot;
import com.yimao.cloud.water.service.AdslotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 描述：广告位
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:50
 * @Version 1.0
 */
@Service
@Slf4j
public class AdslotServiceImpl implements AdslotService {

    @Resource
    private AdslotMapper adslotMapper;
    @Resource
    private UserCache userCache;

    /**
     * 创建广告位
     *
     * @param adslot
     */
    @EnableOperationLog(
            name = "创建广告位",
            type = OperationType.SAVE,
            daoClass = AdslotMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"adslotId", "position", "duration", "name"},
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void save(Adslot adslot) {
        if (Objects.isNull(adslot.getDuration())) {
            throw new BadRequestException("请添加播放时长。");
        }
        if (Objects.isNull(adslot.getPosition())) {
            throw new BadRequestException("请添加屏幕位置。");
        }
        if (Objects.isNull(adslot.getName())) {
            throw new BadRequestException("请添加广告位名称。");
        }
        adslot.setForbidden(false);
        adslot.setDeleted(false);
        //生成广告编码
        Adslot ad = new Adslot();
        ad.setDeleted(false);
        ad.setPosition(adslot.getPosition());
        int id = adslotMapper.selectCount(ad) + 1;
        if (adslot.getPosition().equals(ScreenLocationEnum.ONE.value)) {
            adslot.setAdslotId("DP" + getCode(id));
        } else if (adslot.getPosition().equals(ScreenLocationEnum.TWO.value)) {
            adslot.setAdslotId("XP" + getCode(id));
        }

        adslot.setCreator(userCache.getCurrentAdminRealName());
        adslot.setCreateTime(new Date());
        adslotMapper.insert(adslot);

    }

    /**
     * 生成编程格式
     *
     * @param num
     * @return
     */
    public String getCode(int num) {
        Format f1 = new DecimalFormat("000");
        return f1.format(num);
    }

    /**
     * 更新广告位
     *
     * @param adslot
     */
    @EnableOperationLog(
            name = "更新广告位",
            type = OperationType.UPDATE,
            daoClass = AdslotMapper.class,
            parseClass = DefaultContentParse.class,
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void updateById(Adslot adslot) {
        if (Objects.isNull(adslot.getId())) {
            throw new BadRequestException("广告ID不能为空。");
        }

        adslot.setUpdater(userCache.getCurrentAdminRealName());
        adslot.setUpdateTime(new Date());
        adslotMapper.updateByPrimaryKeySelective(adslot);

    }


    /**
     * 查询广告位列表信息
     *
     * @param platform
     * @param position
     * @return
     */
    @Override
    public PageVO<AdslotDTO> queryListByCondition(Integer platform, Integer position, Integer forbidden, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(Adslot.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleted", 0);
        if (!Objects.isNull(platform)) {
            criteria.andEqualTo("platform", platform);
        }
        if (!Objects.isNull(position)) {
            criteria.andEqualTo("position", position);
        }
        if (!Objects.isNull(forbidden)) {
            criteria.andEqualTo("forbidden", forbidden);
        }
        Page<Adslot> page = (Page<Adslot>) adslotMapper.selectByExample(example);
        return new PageVO<>(pageNum, page, Adslot.class, AdslotDTO.class);
    }

    /**
     * 根据广告位编码查询剩余广告位名称
     *
     * @param adslotIdList
     * @return
     */
    @Override
    public Set<String> selectAdslotName(Set<String> adslotIdList) {
        Set<String> set = new HashSet<>();
        Example example = new Example(Adslot.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleted", 0);
        criteria.andEqualTo("forbidden", 0);
        if (CollectionUtil.isNotEmpty(adslotIdList)) {
            criteria.andNotIn("adslotId", adslotIdList);
        }
        List<Adslot> list = adslotMapper.selectByExample(example);
        for (Adslot ad : list) {
            set.add(ad.getName());
        }
        return set;
    }

    /**
     * 根据条件查询有效广告位数量
     *
     * @param adslot
     * @return
     */
    @Override
    public int selectCount(Adslot adslot) {
        adslot.setDeleted(false);
        adslot.setForbidden(false);
        return adslotMapper.selectCount(adslot);
    }


    /**
     * 提前生成广告位名称
     *
     * @param position
     * @return
     */
    @Override
    public String getAdslotName(Integer position) {
        //生成广告位名称
        String name = "";
        Adslot ad = new Adslot();
        ad.setDeleted(false);
        ad.setPosition(position);
        int count = adslotMapper.selectCount(ad) + 1;
        if (Objects.equals(position, ScreenLocationEnum.ONE.value)) {
            name = "大屏位置" + count;
        } else if (Objects.equals(position, ScreenLocationEnum.TWO.value)) {
            name = "小屏位置" + count;
        }
        return name;
    }
}
