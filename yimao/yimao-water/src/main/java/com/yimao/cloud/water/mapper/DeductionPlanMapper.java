package com.yimao.cloud.water.mapper;

import com.yimao.cloud.water.po.DeductionPlan;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface DeductionPlanMapper extends Mapper<DeductionPlan> {
    List<DeductionPlan> listAll(@Param("deviceId") Integer deviceId);

    DeductionPlan selectNextPlan(@Param("deviceId") Integer deviceId, @Param("sorts") Integer sorts, @Param("createTime") Date createTime);

    void updateSnForChangeDevice(@Param("deviceId") Integer deviceId, @Param("oldSn") String oldSn, @Param("newSn") String newSn);
}
