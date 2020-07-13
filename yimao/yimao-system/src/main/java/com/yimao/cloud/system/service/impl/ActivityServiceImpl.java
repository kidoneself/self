package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.system.ActivityDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.ActivityMapper;
import com.yimao.cloud.system.mapper.DictionaryMapper;
import com.yimao.cloud.system.po.Activity;
import com.yimao.cloud.system.po.Dictionary;
import com.yimao.cloud.system.service.ActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author by liuhao@yimaokeji.com
 * 2018042018/4/28
 */
@Service
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private DictionaryMapper dictionaryMapper;

    @Override
    public PageVO<ActivityDTO> listActivity(Integer side, Integer acType, String title, Integer deleteFlag, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ActivityDTO> page = activityMapper.findActivityList(acType, side, title, deleteFlag);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public Activity activityById(Integer id) {
        return activityMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增活动
     *
     * @param activity 活动
     * @param userName 操作人
     * @return
     */
    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public void saveActivity(Activity activity, String userName) {
        if (StringUtil.isEmpty(activity.getTitle())) {
            throw new BadRequestException("标题不能为空");
        }
        if (activity.getSide() == null) {
            throw new BadRequestException("端不能为空");
        }
        //如果是兑换活动
        if (activity.getAcType() != null && activity.getAcType() == 2) {
            if (activity.getChannel() == null) {
                throw new BadRequestException("兑换活动的渠道不能为空");
            }
            if (activity.getBackImg() == null) {
                throw new BadRequestException("兑换活动的背景图不能为空!");
            }
        }

        if (activity.getAcType() == null) {
            activity.setAcType(1);
        }
        Date cDate = new Date();
        activity.setCreator(userName);
        activity.setCreateTime(cDate);
        activity.setUpdater(userName);
        activity.setUpdateTime(cDate);
        activity.setDeleteFlag(false);
        activityMapper.insert(activity);

    }

    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public void updateActivity(Activity activity, String userName) {
        if (StringUtil.isEmpty(activity.getTitle())) {
            throw new BadRequestException("标题不能为空");
        }
        if (activity.getSide() == null) {
            throw new BadRequestException("端不能为空");
        }
        //如果是兑换活动
        if (activity.getAcType() != null && activity.getAcType() == 2) {
            if (activity.getChannel() == null) {
                throw new BadRequestException("兑换活动的渠道不能为空! ");
            }
            if (activity.getBackImg() == null) {
                throw new BadRequestException("兑换活动的背景图不能为空");
            }
        }

        activity.setUpdater(userName);
        activity.setUpdateTime(new Date());
        activityMapper.updateByPrimaryKeySelective(activity);
    }

    /**
     * 活动上下线
     *
     * @param id       活动
     * @param userName 操作人
     * @return
     */
    @Override
    public void outOffActivity(Integer id, String userName) {
        Activity activity = activityMapper.selectByPrimaryKey(id);
        ActivityDTO dto = null;
        if (activity != null) {
            if (activity.getDeleteFlag() == null) {
                activity.setDeleteFlag(true);
            } else {
                if (activity.getDeleteFlag()) {
                    activity.setDeleteFlag(false);
                } else {
                    activity.setDeleteFlag(true);
                }
            }
            activity.setUpdater(userName);
            activity.setUpdateTime(new Date());
            activityMapper.updateByPrimaryKeySelective(activity);
            dto = new ActivityDTO();
            activity.convert(dto);
        }
    }

    @Override
    public void deleteActivity(Integer id) {
        Activity activity = activityMapper.selectByPrimaryKey(id);
        if (activity != null) {
            activityMapper.deleteByPrimaryKey(activity.getId());
        }
    }

    @Override
    public void removeActivity(Integer id) {
        Activity activity = activityMapper.selectByPrimaryKey(id);
        if (activity != null) {
            //清除 下架的推薦產品
            //============業務編寫=============
        }
    }

    @Override
    public void updateDictionary(Dictionary dictionary) {
        Dictionary dic = dictionaryMapper.selectByPrimaryKey(dictionary.getId());
        if (dic != null) {
            if (!dic.getId().equals(dictionary.getId())) {
                if (existsDicKey(dic.getGroupCode(), dic.getCode())) {
                    throw new BadRequestException("字典代码不能重复");
                }
            }
            if (dictionary.getPid() == null) {
                dictionary.setPid(0);
            }
            dictionary.setUpdateTime(new Date());

            int i = dictionaryMapper.updateByPrimaryKeySelective(dictionary);
            if (i < 1) {
                throw new YimaoException("修改失败");

            }
        }


    }


    private boolean existsDicKey(String dicType, String dicCode) {

        Example exam = new Example(Dictionary.class);
        Example.Criteria criteria = exam.createCriteria();
        criteria.andEqualTo("DICTYPE", dicType);
        criteria.andEqualTo("DICCODE", dicCode);
        List<Dictionary> dic = dictionaryMapper.selectByExample(exam);
        if (CollectionUtil.isNotEmpty(dic)) {
            return true;
        }
        throw new NotFoundException("相关字典不存在");

    }
}
