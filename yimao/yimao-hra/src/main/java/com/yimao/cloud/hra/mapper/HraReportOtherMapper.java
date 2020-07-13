package com.yimao.cloud.hra.mapper;


import com.yimao.cloud.hra.po.HraReportOther;
import com.yimao.cloud.pojo.dto.hra.HraReportOtherDTO;
import io.lettuce.core.dynamic.annotation.Param;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;
import java.util.Map;

public interface HraReportOtherMapper extends Mapper<HraReportOther> {

    /**
     * 获取他人评估报告列表
     * @param uid
     * @return
     */
    List<HraReportOtherDTO> getOtherReportList(@Param(value = "uid") Integer uid);

    /**
     *
     * @param ticketNo
     * @return
     */
    List<HraReportOther> getOtherReportByNo(@Param(value = "ticketNo") String ticketNo);

    //查询
    HraReportOther selectReportByNo(@Param(value = "ticketNo") String ticketNo);



    List<HraReportOther> getShareUserReportList(@Param(value = "id") Integer id);


    HraReportOther findReportByNoAndsharedUserId(Map<String, Object> map);

    //获取报告
    HraReportOther getOtherReportByNoAndUid(Map<String, Object> map);

}
