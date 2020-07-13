package com.yimao.cloud.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.order.mapper.DeliveryAddressMapper;
import com.yimao.cloud.order.po.DeliveryAddress;
import com.yimao.cloud.order.service.DeliveryAddressService;
import com.yimao.cloud.pojo.dto.order.DeliveryAddressDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/1/28
 */
@Service
@Slf4j
public class DeliveryAddressServiceImpl implements DeliveryAddressService {

    @Resource
    private DeliveryAddressMapper deliveryAddressMapper;

    @Resource
    private UserCache userCache;

    /**
     * 新增地址
     *
     * @param deliveryAddress
     */
    @Override
    public void addDeliveryAddress(DeliveryAddress deliveryAddress) {
        Page<DeliveryAddressDTO> deliveryAddressDTOS = deliveryAddressMapper.selectAllAddress(userCache
                .getUserId());
        if(CollectionUtil.isEmpty(deliveryAddressDTOS.getResult())){
            deliveryAddress.setHasDelivery(1);
            deliveryAddress.setHasRefund(1);
        }
        deliveryAddress.setOperatorId(userCache.getUserId());
        deliveryAddress.setUpdateTime(new Date());
        deliveryAddress.setCreateTime(new Date());
        deliveryAddress.setCreator(userCache.getCurrentAdminRealName());
        deliveryAddress.setUpdater(userCache.getCurrentAdminRealName());
        int i = deliveryAddressMapper.insertSelective(deliveryAddress);
        if (i < 1) {
            throw new YimaoException("新增地址失败");
        }
    }

    /**
     * 设置为默认发货地址
     *
     * @param id
     */
    @Override
    public void delivery(Integer id) {
        //只能有一个默认的，设置一个则其他的设置为0
        DeliveryAddress deliveryAddress = deliveryAddressMapper.selectByPrimaryKey(id);
        if (deliveryAddress == null) {
            throw new NotFoundException("未找到地址信息");
        }

        if(deliveryAddress.getHasDelivery() == 1){
            throw new YimaoException("当前地址为默认发货地址");
        }
        
        DeliveryAddress address = new DeliveryAddress();
        address.setOperatorId(deliveryAddress.getOperatorId());
        address.setHasDelivery(1);
        DeliveryAddress defaultDeliveryAddress = deliveryAddressMapper.selectOne(address);
        if(null != defaultDeliveryAddress){
            defaultDeliveryAddress.setHasDelivery(0);
            defaultDeliveryAddress.setUpdateTime(new Date());
            defaultDeliveryAddress.setUpdater(userCache.getCurrentAdminRealName());
            deliveryAddressMapper.updateByPrimaryKeySelective(defaultDeliveryAddress);
        }

        deliveryAddress.setHasDelivery(1);
        deliveryAddress.setUpdateTime(new Date());
        deliveryAddress.setUpdater(userCache.getCurrentAdminRealName());
        deliveryAddressMapper.updateByPrimaryKeySelective(deliveryAddress);
    }


    /**
     * 设置为默认退货地址
     *
     * @param id
     */
    @Override
    public void refund(Integer id) {
        //只能有一个默认的，设置一个则其他的设置为0
        DeliveryAddress deliveryAddress = deliveryAddressMapper.selectByPrimaryKey(id);
        if (deliveryAddress == null) {
            throw new NotFoundException("未找到地址信息");
        }
        if(deliveryAddress.getHasRefund() == 1){
            throw new YimaoException("当前地址为默认收货地址");
        }

        DeliveryAddress address = new DeliveryAddress();
        address.setOperatorId(deliveryAddress.getOperatorId());
        address.setHasRefund(1);
        DeliveryAddress defaultRefundAddress = deliveryAddressMapper.selectOne(address);
        if(null != defaultRefundAddress){
            defaultRefundAddress.setHasRefund(0);
            defaultRefundAddress.setUpdateTime(new Date());
            defaultRefundAddress.setUpdater(userCache.getCurrentAdminRealName());
            deliveryAddressMapper.updateByPrimaryKeySelective(defaultRefundAddress);
        }

        deliveryAddress.setHasRefund(1);
        deliveryAddress.setUpdateTime(new Date());
        deliveryAddress.setUpdater(userCache.getCurrentAdminRealName());
        deliveryAddressMapper.updateByPrimaryKeySelective(deliveryAddress);
    }

    /**
     * 删除地址
     *
     * @param id
     */
    @Override
    public void deleteDeliveryAddress(Integer id) {
        DeliveryAddress deliveryAddress = deliveryAddressMapper.selectByPrimaryKey(id);
        if (deliveryAddress == null) {
            throw new YimaoException("未找到相应地址");
        }
        if (deliveryAddress.getHasDelivery() == 1 || deliveryAddress.getHasRefund() == 1) {
            throw new YimaoException("该地址为默认地址，不能删除");
        }
        deliveryAddressMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void editorDeliveryAddress(DeliveryAddress deliveryAddress) {
        deliveryAddress.setUpdateTime(new Date());
        deliveryAddress.setUpdater(userCache.getCurrentAdminRealName());
        deliveryAddressMapper.updateByPrimaryKeySelective(deliveryAddress);
        DeliveryAddressDTO dto = new DeliveryAddressDTO();
        deliveryAddress.convert(dto);
    }

    @Override
    public PageVO<DeliveryAddressDTO> addressPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<DeliveryAddressDTO> page = deliveryAddressMapper.selectAllAddress(userCache.getUserId());
        return new PageVO<>(pageNum, page);
    }

    @Override
    public DeliveryAddressDTO getDeliveryAddress(Integer id) {
        Example example = new Example(DeliveryAddress.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("operatorId",id);
        criteria.andEqualTo("hasDelivery",1);
        List<DeliveryAddress> deliveryAddresses = deliveryAddressMapper.selectByExample(example);
        if(CollectionUtil.isNotEmpty(deliveryAddresses)){
            DeliveryAddressDTO dto = new DeliveryAddressDTO();
            DeliveryAddress address = deliveryAddresses.get(0);
            address.convert(dto);
            return dto;
        }
        return null;
    }
}
