package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.AreaSyncState;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.OnlineAreaMapper;
import com.yimao.cloud.system.po.OnlineArea;
import com.yimao.cloud.system.service.OnlineAreaService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 描述：服务站新流程
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
@Service
public class OnlineAreaServiceImpl implements OnlineAreaService {

    @Resource
    private UserCache userCache;
    @Resource
    private OnlineAreaMapper onlineAreaMapper;

    @Resource
    private AmqpTemplate rabbitTemplate;

    /**
     * 新增上线地区
     *
     * @param record 上线地区
     */
    @Override
    public void save(OnlineArea record) {
        if (StringUtil.isEmpty(record.getProvince())) {
            throw new BadRequestException("请选择省");
        }
        if (StringUtil.isEmpty(record.getCity())) {
            throw new BadRequestException("请选择市");
        }
        if (StringUtil.isEmpty(record.getRegion())) {
            throw new BadRequestException("请选择区");
        }
        OnlineArea query = new OnlineArea();
        query.setProvince(record.getProvince());
        query.setCity(record.getCity());
        query.setRegion(record.getRegion());
        OnlineArea dbRecord = onlineAreaMapper.selectOne(query);
        if (dbRecord == null) {
            record.setId(null);
            record.setLevel(3);
            record.setDeleted(false);
            record.setStatus(0);
            record.setSyncState("N");
            record.setCreator(userCache.getCurrentAdminRealName());
            record.setCreateTime(new Date());
            onlineAreaMapper.insert(record);
        } else {
            if (dbRecord.getDeleted()) {
                dbRecord.setLevel(3);
                dbRecord.setDeleted(false);
                dbRecord.setStatus(0);
                dbRecord.setSyncState("N");
                dbRecord.setUpdater(userCache.getCurrentAdminRealName());
                dbRecord.setUpdateTime(new Date());
                onlineAreaMapper.updateByPrimaryKeySelective(dbRecord);
            } else {
                throw new BadRequestException("上线地区记录已存在。");
            }
        }
        //TODO 同步百得系统
    }

    /**
     * 服务站是否升级新流程校验
     *
     * @param province 省
     * @param city     市
     * @param region   区
     * @param level    区域等级：1-省；2-省市；3-省市区
     */
    @Override
    public OnlineArea getOnlineAreaByPCR(String province, String city, String region, Integer level) {
        OnlineArea query = new OnlineArea();
        query.setProvince(province);
        query.setCity(city);
        query.setRegion(region);
        query.setLevel(level);
        query.setDeleted(false);
        List<OnlineArea> list = onlineAreaMapper.select(query);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public PageVO<OnlineArea> onlineAreaList(String province, String city, String region, Integer pageNum, Integer pageSize) {
        Example example = new Example(OnlineArea.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotBlank(province)) {
            criteria.andLike("province", "%" + province + "%");
        }
        if (StringUtil.isNotBlank(city)) {
            criteria.andLike("city", "%" + city + "%");
        }
        if (StringUtil.isNotBlank(region)) {
            criteria.andLike("region", "%" + region + "%");
        }
        criteria.andEqualTo("deleted", false);
        PageHelper.startPage(pageNum, pageSize);
        Page<OnlineArea> page = (Page<OnlineArea>) onlineAreaMapper.selectByExample(example);
        convetData(page);
        return new PageVO<>(pageNum, page);
    }

    /***
     * 数据转换
     * @param page
     */
    private void convetData(Page<OnlineArea> page) {
        if (page != null && !page.getResult().isEmpty()) {
            for (OnlineArea area : page.getResult()) {
                if (!StringUtil.isEmpty(area.getSyncState())) {
                    AreaSyncState.getAreaSyncStateName(area.getSyncState());
                } else {
                    area.setSyncStateText("未同步");
                }
            }
        }

    }

    @Override
    public void setAreaOnline(Integer id) {
        if (null == id) {
            throw new YimaoException("id参数必传！");
        }
        OnlineArea area = new OnlineArea();
        area.setId(id);
        area.setStatus(0);//未上线
        area.setUpdater(userCache.getCurrentAdminRealName());
        area.setUpdateTime(new Date());
        area.setSyncState("N");//待同步售后工单全部完成之后,在更新状态
        onlineAreaMapper.updateByPrimaryKeySelective(area);

        //地区上线同步百得工单该地区的工单,如果是省市级别则需要查找到区级数据
        OnlineArea onlineArea = onlineAreaMapper.selectByPrimaryKey(id);
        if (null != onlineArea) {
            OnlineAreaDTO onlineAreaDTO = new OnlineAreaDTO();
            onlineAreaDTO.setId(onlineArea.getId());
            onlineAreaDTO.setProvince(onlineArea.getProvince());
            onlineAreaDTO.setCity(onlineArea.getCity());
            onlineAreaDTO.setRegion(onlineArea.getRegion());
            onlineAreaDTO.setLevel(onlineArea.getLevel());
            rabbitTemplate.convertAndSend(RabbitConstant.AREA_ONLINE_SYNC_WORK_ORDER, onlineAreaDTO);
        }


    }

    @Override
    public void deleteOnlineAreaById(Integer id) {
        if (null == id) {
            throw new YimaoException("id参数必传！");
        }
        OnlineArea area = new OnlineArea();
        area.setId(id);
        area.setDeleted(true);
        area.setUpdater(userCache.getCurrentAdminRealName());
        area.setUpdateTime(new Date());
        onlineAreaMapper.updateByPrimaryKeySelective(area);
        //TODO 同步百得
    }
}
