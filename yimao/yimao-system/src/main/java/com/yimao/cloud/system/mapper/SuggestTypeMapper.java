package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.SuggestTypeDTO;
import com.yimao.cloud.system.po.SuggestType;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SuggestTypeMapper extends Mapper<SuggestType> {

    Page<SuggestTypeDTO> pageSuggestType(@Param("terminal") Integer terminal);

    List<SuggestTypeDTO> querySuggestTypeGroupByTerminal();

    List<SuggestTypeDTO> querySuggestTypeByTerminal(@Param("terminal") Integer terminal);
}
