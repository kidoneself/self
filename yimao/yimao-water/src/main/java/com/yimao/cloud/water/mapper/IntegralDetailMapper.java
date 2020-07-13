package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.water.IntegralDetailDTO;
import com.yimao.cloud.water.po.IntegralDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface IntegralDetailMapper extends Mapper<IntegralDetail> {

    Integer getCountBySn(@Param("sn") String sn, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    Integer getPadEffectiveTotalIntegralBySn(@Param("sn") String sn);
    Page<IntegralDetailDTO> getDetailListByRuleId(@Param("sn") String sn, @Param("integralRuleId") Integer integralRuleId, @Param("province") String province, @Param("city") String city, @Param("region") String region);

    void batchInsert(List<IntegralDetail> list);

}
