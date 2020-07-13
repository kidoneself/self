package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.ActivityDTO;
import com.yimao.cloud.system.po.Activity;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author by liuhao@yimaokeji.com
 */
public interface ActivityMapper extends Mapper<Activity> {

    /**
     * 获取活动列表
     *
     * @param acType 活动类型
     * @param side   端
     * @param title 标题
     *@param deleteFlag @return page
     */
    Page<ActivityDTO> findActivityList(@Param("acType") Integer acType,
                                       @Param("side") Integer side,
                                       @Param("title") String title,
                                       @Param("deleteFlag") Integer deleteFlag);

}
