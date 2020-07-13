package com.yimao.cloud.hra.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.hra.ReportDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ReportMapper extends Mapper<ReportDTO> {
    Page<ReportDTO> listReport(@Param("phone") String phone);

    Page<ReportDTO> listReportRecord(@Param("userId") Integer userId);

    List<ReportDTO> listByNoSaveRecord(@Param("userId") Integer userId);
}
