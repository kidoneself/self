package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.OrderMain;
import com.yimao.cloud.pojo.dto.order.OrderMainDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/8.
 */
public interface OrderMainMapper extends Mapper<OrderMain> {

	Page<OrderMainDTO> selectPayCheckList(OrderMainDTO query);

	Page<OrderMainDTO> selectPayCheckListExport(OrderMainDTO query);

    Integer ProductCompanyIdByOutTradeNo(@Param("id") Long id);

    boolean existsWithUserId(@Param("userId") Integer userId);

	int selectPayCheckCount();
}
