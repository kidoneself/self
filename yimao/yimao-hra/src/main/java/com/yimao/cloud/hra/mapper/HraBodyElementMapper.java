package com.yimao.cloud.hra.mapper;

import com.yimao.cloud.hra.po.HraBodyElement;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;

public interface HraBodyElementMapper extends Mapper<HraBodyElement> {

    List<HraBodyElement> selectBodyElements();

}
