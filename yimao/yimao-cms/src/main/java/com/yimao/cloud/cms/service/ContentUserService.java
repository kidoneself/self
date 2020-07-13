package com.yimao.cloud.cms.service;

/**
 * @author Lizhqiang
 * @date 2019-08-26
 */
public interface ContentUserService {

    void userRead(Integer contentId, Integer type);


    Integer userReadCount();

    void updateContentToRead(Integer id);

    void deleteNotice(String ids);
}
