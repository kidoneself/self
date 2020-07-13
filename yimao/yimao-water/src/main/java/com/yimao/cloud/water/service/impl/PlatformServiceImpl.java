package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.aop.OperationType;
import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;
import com.yimao.cloud.framework.aop.parser.DefaultContentParse;
import com.yimao.cloud.pojo.dto.water.PlatformDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.mapper.PlatformMapper;
import com.yimao.cloud.water.po.Platform;
import com.yimao.cloud.water.service.PlatformService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 描述：第三方广告平台
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:50
 * @Version 1.0
 */
@Service
@Slf4j
public class PlatformServiceImpl implements PlatformService {

    @Resource
    private PlatformMapper platformMapper;
    @Resource
    private UserCache userCache;


    /**
     * 新增第三方广告平台
     *
     * @param platform
     */
    @EnableOperationLog(
            name = "新增第三方广告平台",
            type = OperationType.SAVE,
            daoClass = PlatformMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id", "name"},
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void save(Platform platform) {
        if (StringUtil.isEmpty(platform.getName())) {
            throw new BadRequestException("请添加第三方广告平台名称。");
        }
        if (Objects.isNull(platform.getSort())) {
            throw new BadRequestException("请添加排序。");
        }

        platform.setCreator(userCache.getCurrentAdminRealName());
        platform.setCreateTime(new Date());
        platform.setDeleted(false);
        platformMapper.insert(platform);
    }

    /**
     * 根据id更新第三方广告平台
     *
     * @param platform
     */
    @EnableOperationLog(
            name = "更新第三方广告平台",
            type = OperationType.UPDATE,
            daoClass = PlatformMapper.class,
            parseClass = DefaultContentParse.class,
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void update(Platform platform) {
        if (Objects.isNull(platform.getId())) {
            throw new BadRequestException("ID不能为空。");
        }
        platform.setUpdater(userCache.getCurrentAdminRealName());
        platform.setUpdateTime(new Date());
        platformMapper.updateByPrimaryKeySelective(platform);
    }

    /**
     * 查询所有的第三方广告平台
     *
     * @return
     */
    @Override
    public PageVO<PlatformDTO> page(Integer pageNum, Integer pageSize, Integer forbidden) {
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(Platform.class);
        example.setOrderByClause("sort desc");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleted", 0);
        if (Objects.nonNull(forbidden)) {
            List<Integer> list = new ArrayList();
            list.add(0);
            if (Objects.equals(forbidden, 1)) {
                //第三方
                criteria.andNotIn("id", list);
            } else {
                //自有
                criteria.andIn("id", list);
            }
        }
        Page<Platform> page = (Page<Platform>) platformMapper.selectByExample(example);
        return new PageVO<>(pageNum, page, Platform.class, PlatformDTO.class);
    }

    /**
     * 删除第三方平台
     *
     * @param platform
     */
    @EnableOperationLog(
            name = "删除第三方平台",
            type = OperationType.DELETE,
            daoClass = PlatformMapper.class,
            parseClass = DefaultContentParse.class,
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void deletePlatform(Platform platform) {
        if (Objects.isNull(platform.getId())) {
            throw new BadRequestException("ID不能为空。");
        }
        platform.setDeleted(true);
        platform.setUpdater(userCache.getCurrentAdminRealName());
        platform.setUpdateTime(new Date());
        platformMapper.updateByPrimaryKeySelective(platform);
    }

    /**
     * 通过平台ID查询详情
     *
     * @param id
     * @return
     */
    @Override
    public PlatformDTO getById(Integer id) {
        Platform platform = platformMapper.selectByPrimaryKey(id);
        PlatformDTO dto = new PlatformDTO();
        platform.convert(dto);
        return dto;
    }


}
