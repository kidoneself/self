package com.yimao.cloud.user.mapper;

import com.yimao.cloud.user.po.UserAuths;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/5/9.
 */
public interface UserAuthsMapper extends Mapper<UserAuths> {

    List<Integer> listUserIdByUnionid(@Param("unionid") String unionid);

    UserAuths selectByOpenid(@Param("openid") String openid, @Param("identityType") Integer identityType);

    List<UserAuths> listByUserId(@Param("userId") Integer userId, @Param("identityType") Integer identityType);

    void updateUserId(@Param("oldUserId") Integer oldUserId, @Param("newUserId") Integer newUserId);

    String selectUnionidByUserId(@Param("userId") Integer userId);

    List<UserAuths> queryUserAuthsByUserId(@Param("userId") Integer userId);

    Integer selectIdByUserId(@Param("userId") Integer userId, @Param("identityType") Integer identityType);
}
