package com.yimao.cloud.cms.mapper;

import com.yimao.cloud.cms.po.UserRead;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Lizhqiang
 * @date 2019-08-26
 */
public interface ContentUserMapper extends Mapper<UserRead> {
    Integer countHasRead(Integer userId);
}
