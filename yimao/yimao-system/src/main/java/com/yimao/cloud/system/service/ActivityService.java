package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.ActivityDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.Activity;
import com.yimao.cloud.system.po.Dictionary;

/**
 * @author liuhao@yimaokeji.com
 * 2018042018/4/27
 */
public interface ActivityService {
    /**
     * 分页查询全部活动
     *
     * @param side       端
     * @param acType     类型
     * @param pageNum    当前页
     * @param pageSize   每页显示条数
     * @param title      标题
     * @param deleteFlag 是否删除
     * @return 分页对象
     */
    PageVO<ActivityDTO> listActivity(Integer side, Integer acType, String title, Integer deleteFlag, Integer pageNum, Integer pageSize);


    /**
     * 根据id查询活动
     *
     * @param id 活动id
     * @return 活动
     */
    Activity activityById(Integer id);

    /**
     * 新增活动
     *
     * @param activity 活动
     * @param userName 操作人
     * @return Boolean
     */
    void saveActivity(Activity activity, String userName);

    /**
     * 修改活动
     *
     * @param activity 活动
     * @param userName 操作人
     * @return Boolean
     */
    void updateActivity(Activity activity, String userName);

    /**
     * 上下线活动
     *
     * @param id       活动
     * @param userName 操作人
     * @return dto
     */
    void outOffActivity(Integer id, String userName);

    /**
     * 删除活动
     *
     * @param id 活动
     */
    void deleteActivity(Integer id);

    /**
     * 移除活动
     *
     * @param id 活动
     */
    void removeActivity(Integer id);

    /**
     * 更新活动字典
     *
     * @param dictionary
     * @return
     */
    void updateDictionary(Dictionary dictionary);

}
