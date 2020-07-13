package com.yimao.cloud.water.mapper;

import com.yimao.cloud.pojo.dto.water.SimCardAccountDTO;
import com.yimao.cloud.water.po.SimCardAccount;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SimCardAccountMapper extends Mapper<SimCardAccount> {
    List<SimCardAccountDTO> listAll();

    SimCardAccount selectByUsername(@Param("username") String username);
}
