package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.DeliveryAddress;
import com.yimao.cloud.pojo.dto.order.DeliveryAddressDTO;
import com.yimao.cloud.pojo.dto.order.ExpressExportDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;
import java.util.Map;

/**
 * @author Lizhqiang
 * @date 2019/1/28
 */
public interface DeliveryAddressMapper extends Mapper<DeliveryAddress> {
    List<Integer> getOtherId(Integer id);

    Page<DeliveryAddressDTO> selectAllAddress(@Param("operatorId") Integer operatorId);

    List<ExpressExportDTO> queryDeliveryAddressList(@Param("id") Integer id);

    Integer batchUpdateDevilieryAddress(Map map);
}
