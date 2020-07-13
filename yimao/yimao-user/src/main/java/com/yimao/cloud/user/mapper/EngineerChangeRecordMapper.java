package com.yimao.cloud.user.mapper;

import com.yimao.cloud.pojo.dto.user.EngineerChangeRecordDTO;
import com.yimao.cloud.user.po.EngineerChangeRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface EngineerChangeRecordMapper extends Mapper<EngineerChangeRecord> {
    List<EngineerChangeRecordDTO> selectByEngineerId(@Param("engineerId") Integer engineerId);
}
