package com.yimao.cloud.user.mapper;

import com.yimao.cloud.pojo.dto.user.UserCompanyDTO;
import com.yimao.cloud.user.po.UserCompany;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;


/**
 *
 * @author hhf
 * @date 2018/12/20
 */
public interface UserCompanyMapper extends Mapper<UserCompany> {

    UserCompanyDTO getCompanyAuditById(@Param("orderId") Long orderId);

    UserCompany getCompanyInfoByDistId(@Param("distId") Integer distId);
}
