package com.yimao.cloud.cms.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.cms.po.VideoType;
import com.yimao.cloud.pojo.dto.cms.VideoTypeDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/21
 */
public interface VideoTypeMapper extends Mapper<VideoType> {

    /**
     * 获取视频类型
     *
     * @param typeId 类型id
     * @param platform
     * @return list
     */
    Page<VideoTypeDTO> videoTypeList(@Param("typeId") Integer typeId,
                                     @Param("platform") Integer platform,
                                     @Param("level") Integer level
    );

    List<VideoTypeDTO> getAllVideoType();
}
