package com.yimao.cloud.cms.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.cms.po.Banner;
import com.yimao.cloud.pojo.dto.cms.BannerDTO;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 *
 * 广告位数据层操作
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
public interface BannerMapper extends Mapper<Banner> {

    /**
     *  分页查询
     * @param banner
     * @return
     */
    Page<BannerDTO> findBannerPage(BannerDTO banner);

}