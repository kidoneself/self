package com.yimao.cloud.cms.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.cms.po.Video;
import com.yimao.cloud.pojo.dto.cms.VideoDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/21
 */
public interface VideoMapper extends Mapper<Video> {

    Page<VideoDTO> listVideo(@Param("liveId") Integer liveId,
                             @Param("liveTypeId") Integer liveTypeId,
                             @Param("liveTypeSubId") Integer liveTypeSubId,
                             @Param("recommend") Integer recommend,
                             @Param("liveStatus") Integer liveStatus,
                             @Param("deleteFlag") Integer deleteFlag,
                             @Param("platform") Integer platform,
                             @Param("status") Integer status,
                             @Param("name") String name,
                             @Param("sort") Integer sort,
                             @Param("startReleaseTime") Date startReleaseTime,
                             @Param("endReleaseTime") Date endReleaseTime);

    List<VideoDTO> getVideoByRecommend();
}
