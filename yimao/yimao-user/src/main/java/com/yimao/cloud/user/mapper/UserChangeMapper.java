package com.yimao.cloud.user.mapper;


import java.util.List;

import com.yimao.cloud.pojo.dto.user.UserChangeRecordDTO;
import com.yimao.cloud.user.po.UserChangeRecord;
import tk.mybatis.mapper.common.Mapper;

/**
 * 取消关注记录表
 *
 * @author Zhang Bo
 * @date 2018/3/23.
 */
public interface UserChangeMapper extends Mapper<UserChangeRecord> {

	List<UserChangeRecordDTO> selectChangeRecordByDistributorId(Integer userId);

	UserChangeRecordDTO selectDestTransferRecord(Integer userId);

	UserChangeRecordDTO queryChangeRecord(Integer id, Integer distributorId);
}
