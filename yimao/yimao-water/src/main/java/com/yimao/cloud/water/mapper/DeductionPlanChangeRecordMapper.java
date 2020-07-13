package com.yimao.cloud.water.mapper;

import com.yimao.cloud.water.po.DeductionPlanChangeRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DeductionPlanChangeRecordMapper extends Mapper<DeductionPlanChangeRecord> {

    List<DeductionPlanChangeRecord> listAll(@Param("deviceId") Integer deviceId);

}
