package com.yimao.cloud.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.baideApi.utils.StringUtil;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.ProductActivityStatusEnum;
import com.yimao.cloud.base.enums.ProductActivityType;
import com.yimao.cloud.base.enums.ProductStatus;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.BeanHelper;
import com.yimao.cloud.base.utils.CalcActivityStartTimeUtil;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.product.ProductActivityDTO;
import com.yimao.cloud.pojo.query.product.ProductActivityQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductActivityVO;
import com.yimao.cloud.product.mapper.ProductActivityMapper;
import com.yimao.cloud.product.mapper.ProductMapper;
import com.yimao.cloud.product.po.Product;
import com.yimao.cloud.product.po.ProductActivity;
import com.yimao.cloud.product.service.ProductActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/***
 * 产品活动业务类
 * @author zhangbaobao
 * @date 2020/3/12
 */
@Service
@Slf4j
public class ProductActivityServiceImpl implements ProductActivityService {

    @Resource
    private ProductActivityMapper productActivityMapper;

    @Resource
    private UserCache userCache;
    @Resource
    private RedisCache redisCache;

    @Resource
    private ProductMapper productMapper;

    /***
     * 更新活动信息
     */
    @Override
    public int updateProductActivity(ProductActivityDTO productActivity) {
        ProductActivity activity = BeanHelper.copyProperties(productActivity, ProductActivity.class);
        Date now = new Date();
        setCycleTime(activity);
        String userName = userCache.getCurrentAdminRealName();
        if (activity.getId() != null && activity.getId() > 0) {
            //更新操作
            if (productActivityMapper.existsWithPrimaryKey(activity.getId())) {
                activity.setUpdateTime(now);
                activity.setUpdater(userName);
                int count = productActivityMapper.updateByPrimaryKeySelective(activity);
                redisCache.delete(Constant.PRODUCT_ACTIVITY_CACHE + activity.getId());
                return count;
            }
        }
        //校验同一产品同一活动 同一时间段是否已存在,不重复的校验活动开始时间和结束时间,重复的校验具体的开始时间和结束时间、重复时间
        checkData(activity);
        activity.setActivityType(ProductActivityType.PANIC_BUYING.value);
        //默认是未激活
        activity.setOpening(false);
        activity.setStatus(ProductActivityStatusEnum.NOT_STARTED.value);
        activity.setUpdateTime(now);
        activity.setUpdater(userName);
        activity.setCreator(userName);
        activity.setCreateTime(now);
        //设置剩余库存
        activity.setRemainingStock(activity.getActivityStock());
        return productActivityMapper.insertSelective(activity);
    }

    /****
     * //校验同一产品同一活动 同一时间段是否已存在
     * @param activity
     */
    private void checkData(ProductActivity activity) {
        // ProductActivity existActivity = new ProductActivity();
        // existActivity.setActivityType(ProductActivityType.PANIC_BUYING.value);
        // List<ProductActivity> list = new ArrayList<ProductActivity>();
        // if (null != activity.getCycle()) {
        //     if (activity.getCycle()) {
        //         //重复的校验具体的开始时间和结束时间、重复时间
        //         existActivity.setStartTime(activity.getStartTime());
        //         existActivity.setEndTime(activity.getEndTime());
        //         existActivity.setProductId(activity.getProductId());
        //         existActivity.setCycle(existActivity.getCycle());
        //         existActivity.setCycleType(activity.getCycleType());
        //         existActivity.setCycleTime(activity.getCycleTime());
        //     } else {
        //         //不重复 则只校验开始时间和结束时间
        //         existActivity.setStartTime(activity.getStartTime());
        //         existActivity.setEndTime(activity.getEndTime());
        //         existActivity.setProductId(activity.getProductId());
        //         existActivity.setCycle(existActivity.getCycle());
        //     }
        // }
        // list = productActivityMapper.select(existActivity);
        // if (!list.isEmpty()) {
        //     //则说明活动数据已经存在提示: 则提示“该商品已参加X--X的限时购，请重新选择活动时间”
        //     throw new YimaoException("该商品已参加" + list.get(0).getStartTime() + "-" + list.get(0).getEndTime() + "的限时购，请重新选择活动时间");
        //
        // }

        //TODO

    }

    /**
     * 处理周期时间
     *
     * @param activity
     */
    private void setCycleTime(ProductActivity activity) {
        if (null != activity.getCycle() && activity.getCycle() && !StringUtil.isEmpty(activity.getCycleTime())) {
            JSONObject json = new JSONObject();
            String[] data = activity.getCycleTime().split("-");
            for (String str : data) {
                json.put(str.split("_")[0], str.split("_")[1]);
            }
            activity.setCycleTime(json.toJSONString());
        }
    }

    @Override
    public ProductActivityVO getProductActivity(ProductActivityQuery query) {
        ProductActivityVO activity = productActivityMapper.getProductActivity(query);
        CalcActivityStartTimeUtil.calcActivityStartTime(activity);
        return activity;
    }


    /****
     * APP端-查询产品列表
     */
    @Override
    public PageVO<ProductActivityVO> pageProductActivity(Integer pageNum, Integer pageSize, ProductActivityQuery query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ProductActivityVO> page = productActivityMapper.listProductActivity(query);
        //计算活动时间和状态
        List<ProductActivityVO> result = calcActivityTimeAndStatus(page.getResult());
        PageVO<ProductActivityVO> pageVO = new PageVO<>(pageNum, page);
        pageVO.setResult(result);
        return pageVO;
    }


    /***
     *计算活动时间和状态,如果是周期重复的需要计算最近一次的开始时间和结束时间,活动状态也随着变化
     * @param page
     */
    private List<ProductActivityVO> calcActivityTimeAndStatus(List<ProductActivityVO> list) {
        List<ProductActivityVO> all = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            List<ProductActivityVO> notStartList = new ArrayList<>();
            List<ProductActivityVO> processingList = new ArrayList<>();
            List<ProductActivityVO> overList = new ArrayList<>();
            for (ProductActivityVO act : list) {
                CalcActivityStartTimeUtil.calcActivityStartTime(act);
                if (act.getStatus() == ProductActivityStatusEnum.NOT_STARTED.value) {
                    notStartList.add(act);
                } else if (act.getStatus() == ProductActivityStatusEnum.PROCESSING.value) {
                    processingList.add(act);
                } else if (act.getStatus() == ProductActivityStatusEnum.OVER.value) {
                    overList.add(act);
                }
            }
            if (CollectionUtil.isNotEmpty(notStartList)) {
                Collections.sort(notStartList, new Comparator<ProductActivityVO>() {
                    @Override
                    public int compare(ProductActivityVO o1, ProductActivityVO o2) {
                        return o1.getCreateTime().after(o2.getCreateTime()) ? 1 : -1;
                    }
                });
            }
            if (CollectionUtil.isNotEmpty(processingList)) {
                Collections.sort(processingList, new Comparator<ProductActivityVO>() {
                    @Override
                    public int compare(ProductActivityVO o1, ProductActivityVO o2) {
                        return o1.getCreateTime().after(o2.getCreateTime()) ? 1 : -1;
                    }
                });
            }
            if (CollectionUtil.isNotEmpty(overList)) {
                Collections.sort(overList, new Comparator<ProductActivityVO>() {
                    @Override
                    public int compare(ProductActivityVO o1, ProductActivityVO o2) {
                        return o1.getEndTime().after(o2.getEndTime()) ? -1 : 1;
                    }
                });
            }
            all.addAll(processingList);
            all.addAll(notStartList);
            all.addAll(overList);
        }
        return all;
    }

    /****
     * 业务系统-查询产品列表
     */
    @Override
    public PageVO<ProductActivityVO> productActivityList(Integer pageNum, Integer pageSize, ProductActivityDTO query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ProductActivityVO> page = productActivityMapper.list(query);
        convertStatus(page.getResult());
        PageVO<ProductActivityVO> pageVO = new PageVO<>(pageNum, page);
        return pageVO;
    }

    /***
     * 活动状态转换
     * @param result
     */
    private void convertStatus(List<ProductActivityVO> result) {
        if (!result.isEmpty()) {
            for (ProductActivityVO pa : result) {
                if (null == pa.getOpening() || !pa.getOpening()) {
                    pa.setStatus(ProductActivityStatusEnum.NOT_OPEN.value);//已终止
                } else {
                    if (pa.getStatus() == ProductActivityStatusEnum.TERMINATED.value) {
                        continue;
                    } else {
                        CalcActivityStartTimeUtil.calcActivityStartTime(pa);
                    }
                }
            }
        }

    }

    /****
     * 激活活动,需要将商品设置为该活动的活动类型
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void openProductActivity(Integer id) {
        ProductActivity act = productActivityMapper.selectByPrimaryKey(id);
        if (act == null) {
            throw new YimaoException("无效活动");
        }
        if (null != act.getOpening() && act.getOpening()) {
            throw new YimaoException("提交失败，活动已被激活。");
        }
        Product prod = productMapper.selectByPrimaryKey(act.getProductId());
        if (null == prod) {
            throw new YimaoException("提交失败，活动关联的商品有误。");
        }
        if (null != prod.getStatus() && prod.getStatus() == ProductStatus.ONSHELF.value) {
            throw new YimaoException("提交失败，活动关联商品已被提前上架。");
        }

        String userName = userCache.getCurrentAdminRealName();
        Date now = new Date();
        ProductActivity updatate = new ProductActivity();
        updatate.setUpdateTime(now);
        updatate.setUpdater(userName);
        updatate.setOpening(true);
        updatate.setId(id);
        productActivityMapper.updateByPrimaryKeySelective(updatate);
        prod = new Product();
        prod.setId(act.getProductId());
        prod.setUpdater(userName);
        prod.setActivityType(act.getActivityType());
        prod.setUpdateTime(now);
        productMapper.updateByPrimaryKeySelective(prod);
    }

    @Override
    public void stopProductActivity(Integer productId) {
        productActivityMapper.stopProductActivity(productId);
    }

}
