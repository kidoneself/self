package com.yimao.cloud.cms.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.BeanHelper;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.cms.mapper.BannerMapper;
import com.yimao.cloud.cms.po.Banner;
import com.yimao.cloud.cms.service.BannerService;
import com.yimao.cloud.pojo.dto.cms.BannerDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * (BannerDTO)表服务实现类
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
@Service("bannerService")
public class BannerServiceImpl implements BannerService {
    @Resource
    private BannerMapper bannerMapper;

    @Resource
    private UserCache userCache;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public BannerDTO findById(Integer id) {
        Banner banner = bannerMapper.selectByPrimaryKey(id);
        if (banner != null) {
            BannerDTO bannerDTO = new BannerDTO();
            banner.convert(bannerDTO);
            return bannerDTO;
        }
        return null;
    }

    /**
     * 查询多条数据
     *
     * @param pageNum  查询起始位置
     * @param pageSize 查询条数
     * @return 对象列表
     */
    @Override
    public PageVO<BannerDTO> findBannerPage(int pageNum, int pageSize, BannerDTO banner) {
        PageHelper.startPage(pageNum, pageSize);
        Page<BannerDTO> page = bannerMapper.findBannerPage(banner);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 保存
     *
     * @param bannerDTO
     * @return
     */
    @Override
    public Integer save(BannerDTO bannerDTO) {
        if (StringUtil.isEmpty(bannerDTO.getPositionCode())) {
            throw new BadRequestException("广告位不能为空");
        }
        if (bannerDTO.getTerminal() == null) {
            throw new BadRequestException("端不能为空");
        }
        //状态 1已发布 2 未发布 4 已删除 默认未发布
        if (bannerDTO.getStatus() == null) {
            bannerDTO.setStatus(2);
        }
        bannerDTO.setCreateTime(new Date());
        bannerDTO.setUpdateTime(new Date());
        Banner banner = new Banner(bannerDTO);
        return bannerMapper.insert(banner);
    }

    /**
     * 批量修改 banner
     *
     * @param bannerIdList
     * @param dto
     * @return
     */
    @Override
    public Integer batchUpdate(List<Integer> bannerIdList, BannerDTO dto) {
        Example example = new Example(Banner.class);
        example.createCriteria().andIn("id", bannerIdList);
        Banner banner = new Banner(dto);
        return bannerMapper.updateByExampleSelective(banner, example);
    }

    @Override
    public Integer update(BannerDTO bannerDTO) {
        if (StringUtil.isEmpty(bannerDTO.getPositionCode())) {
            throw new BadRequestException("广告位不能为空");
        }
        if (bannerDTO.getTerminal() == null) {
            throw new BadRequestException("端不能为空");
        }
        Banner banner = new Banner(bannerDTO);
        return bannerMapper.updateByPrimaryKeySelective(banner);
    }

    @Override
    public Integer updateBannerSorts(Integer id, Integer sorts) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setSorts(sorts);
        banner.setUpdater(userCache.getCurrentAdminRealName());
        banner.setUpdateTime(new Date());
        return bannerMapper.updateByPrimaryKeySelective(banner);
    }

    @Override
    public List<BannerDTO> getEngineerAppImage() {
        Example example = new Example(Banner.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", 1); //状态为可用
        criteria.andEqualTo("terminal", 5); //安装工app
        criteria.andEqualTo("positionCode", "1001");//首页轮播广告位
        example.orderBy("sorts").desc();
        List<Banner> banners = bannerMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(banners)) {
            return BeanHelper.copyWithCollection(banners, BannerDTO.class);
        }
        return null;
    }
}
