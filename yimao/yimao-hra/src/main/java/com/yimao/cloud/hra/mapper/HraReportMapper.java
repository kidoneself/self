package com.yimao.cloud.hra.mapper;

import com.yimao.cloud.hra.po.HraReport;
import com.yimao.cloud.pojo.dto.hra.ReportDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface HraReportMapper extends Mapper<HraReport> {
    HraReport findHraReport(@Param("phone") String phone, @Param("ticketNo") String ticketNo, @Param("showFlag") Integer showFlag);

    /**
     * 查询没有在评估报告中显示的评估劵的评估劵号
     *
     * @return
     */
    List<String> findUsedTicketNoWithNotShow();

    HraReport findHraReportRecord(@Param("phone") String phone, @Param("ticketNo") String ticketNo, @Param("showFlag") Integer showFlag);

    /**
     * 根据体检卡号获取体检报告
     *
     * @param ticketNo
     * @return
     */
    ReportDTO findReportByTicketNo(String ticketNo);

    /**
     * 根据体检卡号查询
     *
     * @param ticketNo
     * @return
     */
    HraReport findCustomByTicketNo(@Param("ticketNo") String ticketNo);

    /**
     * 获取模板数据方法
     *
     * @param map
     * @return
     */
    ReportDTO findReportByMap(Map<String, String> map);

}