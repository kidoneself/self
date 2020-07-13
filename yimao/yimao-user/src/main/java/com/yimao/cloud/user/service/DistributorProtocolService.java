package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.DistributorProtocolDTO;
import com.yimao.cloud.pojo.query.user.DistributorProtocolQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.DistributorProtocol;

import java.util.List;
import java.util.Map;

/**
 * @author Lizhqiang
 * @date 2019/1/2
 */
public interface DistributorProtocolService {

    /**
     * 获取单个合同详情
     *
     * @param distributorOrderId
     * @return
     */
    DistributorProtocolDTO getDistributorProtocolByOrderId(Long distributorOrderId);

    /**
     * 查询合同列表
     *
     * @param dto
     * @return
     */
    PageVO<DistributorProtocolDTO>  getDistributorProtocolList(DistributorProtocolQueryDTO dto,Integer pageNum, Integer pageSize);
    /**
     * 查询合同列表根据经销商账号
     *
     * @param distributorAccount
     * @return
     */
    List<DistributorProtocol> getDistributorProtocolListByDistributorAccount(String distributorAccount);

    /**
     * 创建合同信息
     *
     * @param distributorProtocol
     */
    void create(DistributorProtocol distributorProtocol);

    /**
     * 签署合同
     *
     * @param orderId
     */
    void sign(Long orderId);

    /**
     * 服务站复查合同
     *
     * @param id
     */
    void renew(Integer id);

    /**
     * 预览合同页参数获取
     * @param distributorOrderId
     * @return
     */
    String previewDistributorProtocol(Long distributorOrderId);

    void backCallYunSignOrderUpdate(String userId, String orderId, String signer, String updateTime, String syncOrderId, String status, String appSecretKey);

    String signDistributorProtocol(Long distributorOrderId);

    List<DistributorProtocolDTO> queryDistributorProtocolByDistIdAndSignStatus(Integer distributorId);

    Map<String,String> checkUserSignStatus(Long orderId);
}
