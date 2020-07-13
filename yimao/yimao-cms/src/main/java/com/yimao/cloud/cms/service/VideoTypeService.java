package com.yimao.cloud.cms.service;

import com.yimao.cloud.pojo.dto.cms.VideoTypeDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/21
 */
public interface VideoTypeService {
    /**
     * 视频分类
     *
     * @param typeId   类型id
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @return page
     */
    PageVO<VideoTypeDTO> videoTypeList(Integer typeId, Integer platform, Integer level, Integer pageNum, Integer pageSize);

    /**
     * 添加
     *
     * @param videoTypeDTO
     */
    void addLiveType(VideoTypeDTO videoTypeDTO);

    /**
     * 修改
     *
     * @param videoTypeDTO
     */
    void updateLiveType(VideoTypeDTO videoTypeDTO);

    /**
     * 删除
     *
     * @param id 分类id
     */
    void deleteLiveType(Integer id);

    List<VideoTypeDTO> getAllVideoType();

    VideoTypeDTO getLiveType(Integer id);
}
