package com.yimao.cloud.cms.service;

import com.yimao.cloud.pojo.dto.cms.BannerDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;

/**
 * (Banner)表服务接口
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
public interface BannerService {


    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @param id
     * @return
     */
    BannerDTO findById(Integer id);

    /**
     * 查询多条数据
     *
     * @param pageNum  查询起始位置
     * @param pageSize 查询条数
     * @param banner
     * @return 对象列表
     */
    PageVO<BannerDTO> findBannerPage(int pageNum, int pageSize, BannerDTO banner);


    /**
     * 保存 banner 信息
     *
     * @param banner
     * @return
     */
    Integer save(BannerDTO banner);


    /**
     * 批量修改
     *
     * @param bannerIds
     * @param banner
     * @return
     */
    Integer batchUpdate(List<Integer> bannerIds, BannerDTO banner);


    /**
     * 更新
     *
     * @param bannerDTO
     * @return
     */
    Integer update(BannerDTO bannerDTO);

    /**
     * 更新排序
     *
     * @param id
     * @param sorts
     */
    Integer updateBannerSorts(Integer id, Integer sorts);

    List<BannerDTO> getEngineerAppImage();
}
