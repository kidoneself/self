package com.yimao.cloud.order.service;

import com.yimao.cloud.order.po.DeliveryAddress;
import com.yimao.cloud.pojo.dto.order.DeliveryAddressDTO;
import com.yimao.cloud.pojo.vo.PageVO;

/**
 * @author Lizhqiang
 * @date 2019/1/28
 */
public interface DeliveryAddressService {

    void addDeliveryAddress(DeliveryAddress deliveryAddress);

    void delivery(Integer id);

    void refund(Integer id);

    void deleteDeliveryAddress(Integer id);

    void editorDeliveryAddress(DeliveryAddress deliveryAddress);

    PageVO<DeliveryAddressDTO> addressPage(Integer pageNum, Integer pageSize);

    DeliveryAddressDTO getDeliveryAddress(Integer id);
}
