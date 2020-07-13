package com.yimao.cloud.cms.service.impl;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.cms.mapper.ContentMapper;
import com.yimao.cloud.cms.mapper.ContentUserMapper;
import com.yimao.cloud.cms.po.UserRead;
import com.yimao.cloud.cms.service.ContentUserService;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019-08-26
 */
@Service
public class ContentUserServiceImpl implements ContentUserService {

    @Resource
    private ContentUserMapper contentUserMapper;

    @Resource
    private UserCache userCache;


    @Resource
    private ContentMapper contentMapper;


    @Override
    public void userRead(Integer contentId, Integer type) {
        UserRead userRead = new UserRead();
        Integer userId = userCache.getUserId();
        userRead.setUserId(userId);
        if (type == 1) {
            userRead.setHasRead(1);
            userRead.setDeleted(0);
        } else {
            userRead.setDeleted(1);
        }
        userRead.setContentId(contentId);
        contentUserMapper.insert(userRead);
    }


    @Override
    public Integer userReadCount() {
        Integer userId = userCache.getUserId();
        Integer count = contentMapper.countAllNotice();
        Integer count2 = contentUserMapper.countHasRead(userId);
        Integer count3 = count - count2;
        return count3;
    }


    @Override
    public void updateContentToRead(Integer id) {
        if (id == null) {
            throw new BadRequestException("参数id不能为空！");
        }

        Example example = new Example(UserRead.class);
        example.createCriteria().andEqualTo("content_id", id);
        UserRead userRead = new UserRead();
        userRead.setHasRead(1);
        contentUserMapper.updateByExampleSelective(userRead,example);
    }


    @Override
    public void deleteNotice(String ids) {
        if (StringUtil.isBlank(ids)) {
            throw new BadRequestException("id不能为空！");
        }

        String[] tempIds = ids.split(",");
        Integer[] array = (Integer[]) ConvertUtils.convert(tempIds, Integer.class);
        List<Integer> list = Arrays.asList(array);

        Example example = new Example(UserRead.class);
        example.createCriteria().andIn("contentId", list);
        UserRead userRead = new UserRead();
        userRead.setDeleted(1);
        contentUserMapper.updateByExampleSelective(userRead,example);
    }
}
