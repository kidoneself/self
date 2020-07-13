package com.yimao.cloud.pojo.dto.out;

import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/9
 */
@Data
public class OutOrderDTO implements Serializable {

    /**
     * 流水号
     */
    private String tradeNo;
    /**
     * 订单
     */
    private OrderSubDTO order;
    /**
     * 用户信息
     */
    private UserDTO userInfo;

    public OutOrderDTO() {
    }

    public OutOrderDTO(OrderSubDTO order, UserDTO userInfo,String tradeNo) {
        this.tradeNo = tradeNo;
        this.order = order;
        this.userInfo = userInfo;
    }
}
