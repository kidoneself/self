package com.yimao.cloud.hra.service;

import com.yimao.cloud.pojo.dto.hra.HraCardDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceOnlineDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2017/12/1.
 */
public interface HraCardService {

    PageVO<HraCardDTO> getHraCardByUser(Integer pageNum, Integer pageSize, Integer userId);

    PageVO<HraCardDTO> listCardByUserId(Integer pageNum, Integer pageSize, Integer userId, Long orderId);

    Map<String, Object> listFreeCounpons(String state, Integer userId, Integer pageSize, Integer pageNum);

    int deleteCard(String cardId);

    void freeCardHandsel(UserDTO userDTO);

    void countAndHandselFreeCard(UserDTO userDTO);

    void saveHraDevice(HraDeviceOnlineDTO hraDeviceOnlineDTO);

    HraTicketDTO getHraTicketByOrderId(Long id);

    List<HraTicketDTO> cardDetailByOrderId(Long orderId);
}
