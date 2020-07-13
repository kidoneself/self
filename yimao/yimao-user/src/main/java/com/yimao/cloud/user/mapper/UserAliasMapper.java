package com.yimao.cloud.user.mapper;

import com.yimao.cloud.user.po.UserAlias;
import tk.mybatis.mapper.common.Mapper;

public interface UserAliasMapper extends Mapper<UserAlias> {
    Integer updateByUserAlias(UserAlias userAsterisk);
}
