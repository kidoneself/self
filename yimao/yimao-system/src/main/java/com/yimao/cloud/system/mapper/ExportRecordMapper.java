package com.yimao.cloud.system.mapper;

import com.yimao.cloud.system.po.ExportRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface ExportRecordMapper extends Mapper<ExportRecord> {

    List<ExportRecord> selectLastByAdminIdAndUrl(@Param("adminId") Integer adminId, @Param("url") String url);

    int selectCountByStatusAndUrl(@Param("url") String url);

    List<ExportRecord> list2DaysAgoData(@Param("compareTime") Date compareTime);
}
