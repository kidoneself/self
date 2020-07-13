package com.yimao.cloud.hra.mapper;


import com.yimao.cloud.hra.po.MiniSickness;
import tk.mybatis.mapper.common.Mapper;

public interface MiniSicknessMapper extends Mapper<MiniSickness> {

    MiniSickness selectSickness(Integer aLong);
}
