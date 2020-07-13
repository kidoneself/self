package com.yimao.cloud.user.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.user.DistributorProtocolDTO;
import com.yimao.cloud.pojo.query.user.DistributorProtocolQueryDTO;
import com.yimao.cloud.user.po.DistributorProtocol;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/1/2
 */
public interface DistributorProtocolMapper extends Mapper<DistributorProtocol> {

    DistributorProtocolDTO getDistributorProtocolByOrderId(Long distributorOrderId);

    Integer selectCount4Status();

    Page distributorProtocolList(DistributorProtocolQueryDTO dto);

    List<DistributorProtocolDTO> queryDistributorProtocolByDistIdAndSignStatus(@Param("distributorId") Integer distributorId);
}
