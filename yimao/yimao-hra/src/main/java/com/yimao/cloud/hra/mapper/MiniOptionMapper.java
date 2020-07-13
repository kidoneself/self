package com.yimao.cloud.hra.mapper;

import com.yimao.cloud.hra.po.MiniOption;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MiniOptionMapper extends Mapper<MiniOption> {

    //获取选项
    List<MiniOption> selectOption(Integer choiceId);

}
