package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.query.water.SimCardNumberQuery;
import com.yimao.cloud.pojo.vo.water.SimCardNumberVO;
import com.yimao.cloud.water.po.SimCardNumber;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface SimCardNumberMapper extends Mapper<SimCardNumber> {
    Page<SimCardNumberVO> selectPage(SimCardNumberQuery query);

    SimCardNumber selectByCardNumber(@Param("cardNumber") String cardNumber, @Param("maxNumber") String maxNumber, @Param("minNumber") String minNumber);
}
