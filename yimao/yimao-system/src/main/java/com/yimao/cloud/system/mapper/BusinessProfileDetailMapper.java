package com.yimao.cloud.system.mapper;

import com.yimao.cloud.pojo.dto.system.BusinessProfileDetailDTO;
import com.yimao.cloud.system.po.BusinessProfileDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BusinessProfileDetailMapper extends Mapper<BusinessProfileDetail> {
    void batchInsert(List<BusinessProfileDetail> list);

    List<BusinessProfileDetailDTO> selectByDate(@Param("sign") Integer sign);
}
