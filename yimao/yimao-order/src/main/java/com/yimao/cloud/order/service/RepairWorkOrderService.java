package com.yimao.cloud.order.service;


import com.yimao.cloud.order.po.RepairWorkOrder;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.dto.order.RepairWorkOrderDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;

/**
 * @author liu yi
 * @description RepairWorkOrderService 维修工单服务
 * @date 2019/3/20
 */
public interface RepairWorkOrderService {
    /**
     * 根据工单id查找维修工单
     * @param id
     * @return
     */
    RepairWorkOrderDTO getRepairWorkOrderById(Integer id);

    /**
     * 根据工单workCode查找维修工单
     * @param workCode
     * @return
     */
    RepairWorkOrderDTO getRepairWorkOrderByWorkCode(String workCode);


    /***
     * 功能描述:创建维修工单
     *
     * @param: [dto]
     * @auther: liu yi
     * @date: 2019/3/29 14:27
     * @return: void
     */
    void create(RepairWorkOrderDTO dto);
    /**
     * 更新
     * @param entity
     * @param operationUserId
     */
    void update(RepairWorkOrder entity, Integer operationUserId);
    /***
     * 功能描述:维修工单统计
     *
     * @param: [engineerId, state]
     * @auther: liu yi
     * @date: 2019/4/10 14:24
     * @return: java.lang.Integer
     */
    Integer getWorkOrderRepairEngineerCount(String engineerId, Integer state);

    /***
     * 功能描述:功能描述:维修工单完成后评价
     *
     * @param: [workcode, appraiseContent]
     * @auther: liu yi
     * @date: 2019/4/10 14:25
     * @return: void
     */
    void userAppraise(String workcode, String appraiseContent);

   /* *//***
     * 功能描述:更新工单评论次数
     *
     * @param: [workCode, voteCount, goodVoteCount]
     * @auther: liu yi
     * @date: 2019/4/10 14:25
     * @return: void
     *//*
    void pushVoteIno(String workCode, Integer voteCount, Integer goodVoteCount);*/

    /**
     *
     * @param workCode
     * @param masterId
     */
    void updateMasterRed(String workCode, String masterId);


    /**
     * 分页查询维修工单
     *
     * @param pageNum
     * @param pageSize
     * @param isCompanyDistributor
     * @param distributorId
     * @param engineerId
     * @param state
     * @param orderStatus
     * @param search
     * @return
     */
    PageVO<RepairWorkOrderDTO> page(Integer pageNum, Integer pageSize, String isCompanyDistributor, String distributorId, Integer engineerId, Integer state, String orderStatus, String search);

    /**
     * 服务统计-工单服务-维修统计
     * @param completeTime
     * @param engineerId
     * @return
     */
    List<RenewDTO> statisticsRepairWorkOrder(String completeTime, Integer engineerId ,Integer timeType);
}
