package com.yimao.cloud.hra.mapper;

import com.yimao.cloud.hra.po.HraReportRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * created by liuhao@yimaokeji.com
 * 2018012018/1/20
 */
public interface HraReportRecordMapper extends Mapper<HraReportRecord> {


    List<HraReportRecord> findHraReportRecord(@Param("reportId") Integer reportId,
                                              @Param("userId") Integer userId);
}
