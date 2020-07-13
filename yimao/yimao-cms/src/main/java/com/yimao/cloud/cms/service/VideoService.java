package com.yimao.cloud.cms.service;

import com.yimao.cloud.cms.po.Video;
import com.yimao.cloud.pojo.dto.cms.VideoDTO;
import com.yimao.cloud.pojo.dto.cms.VideoTypeDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/21
 */
public interface VideoService {
    /**
     * 视频列表
     *
     * @param liveId        频道id
     * @param liveTypeId    分类一级标题
     * @param liveTypeSubId 分类二级标题
     * @param liveStatus    状态 1-直播中，2-回放，3-视频，4-预告
     * @param recommend     是否推荐  1：是  0:否
     * @param deleteFlag    是否删除  1：是 0：否
     * @return
     */
    PageVO<VideoDTO> listVideo(Integer liveId, Integer liveTypeId, Integer liveTypeSubId, Integer recommend, Integer liveStatus, Integer deleteFlag, Integer platform, Integer status, String name, Integer sort, Date startReleaseTime, Date endReleaseTime, Integer pageNum, Integer pageSize);

    /**
     * 添加视频
     *
     * @param video        视频
     * @param currentAdmin 登录人
     * @return
     */
    Integer saveVideo(Video video, String currentAdmin);

    /**
     * 添加视频
     *
     * @param video        视频
     * @param currentAdmin 登录人
     */
    Integer updateVideo(Video video, String currentAdmin);

    /**
     * 删除视频
     */
    void deleteVideo(Integer id);

    /**
     * 上下线视频
     */
    Video onLine(Integer id);

    /**
     * 分享、点赞、观看
     *
     * @param id     视频id
     * @param type   类型
     * @param userId 用户id
     */
    void updateCount(Integer id, String type, Integer userId);

    /**
     * 查询视频详情
     *
     * @param id     视频id
     * @param userId 用户id
     * @return
     */
    VideoDTO videoById(Integer id, Integer userId);

    /**
     * 前端视频分类
     *
     * @return list
     */
    List<VideoTypeDTO> videoTypeList();

    /**
     * 分享、点赞、观看累加
     *
     * @param map
     */
    void updateVideoCount(Map<String, Object> map);

    List<VideoDTO> getVideoByRecommend();
}
