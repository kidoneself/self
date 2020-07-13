package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.DistributorProtocolDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/1/2
 */
@Data
public class DistributorProtocol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long orderId;
    private Integer distributorId;
    private Integer orderType;
    private String distributorAccount;
    private String title; //合同标题名称
    private Integer roleId;
    private String province;
    private String city;
    private String region;
    private String linkMan;
    private String linkPhone;
    private Date createTime;
    private Integer state;//合同创建状态
    private String contract; //合同地址
    private Integer ymSignState;
    private Date ymSignTime;
    private Integer stationSignState;
    private Date stationSignTime;
    private Integer userSignState;//用户签署状态
    private Date userSignTime;//用户签署时间
    private Integer stationRenewState;//服务站复核状态
    private Date stationRenewTime;//服务站复核时间


    public DistributorProtocol() {
    }

    /**
     * 用业务对象DistributorProtocolDTO初始化数据库对象DistributorProtocol。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public DistributorProtocol(DistributorProtocolDTO dto) {
        this.id = dto.getId();
        this.orderId = dto.getOrderId();
        this.distributorId = dto.getDistributorId();
        this.orderType = dto.getOrderType();
        this.distributorAccount = dto.getDistributorAccount();
        this.title = dto.getTitle();
        this.roleId = dto.getRoleId();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.linkMan = dto.getLinkMan();
        this.linkPhone = dto.getLinkPhone();
        this.createTime = dto.getCreateTime();
        this.state = dto.getState();
        this.contract = dto.getContract();
        this.ymSignState = dto.getYmSignState();
        this.ymSignTime = dto.getYmSignTime();
        this.stationSignState = dto.getStationSignState();
        this.stationSignTime = dto.getStationSignTime();
        this.userSignState = dto.getUserSignState();
        this.userSignTime = dto.getUserSignTime();
        this.stationRenewState = dto.getStationRenewState();
        this.stationRenewTime = dto.getStationRenewTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象DistributorProtocolDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(DistributorProtocolDTO dto) {
        dto.setId(this.id);
        dto.setOrderId(this.orderId);
        dto.setDistributorId(this.distributorId);
        dto.setOrderType(this.orderType);
        dto.setDistributorAccount(this.distributorAccount);
        dto.setTitle(this.title);
        dto.setRoleId(this.roleId);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setLinkMan(this.linkMan);
        dto.setLinkPhone(this.linkPhone);
        dto.setCreateTime(this.createTime);
        dto.setState(this.state);
        dto.setContract(this.contract);
        dto.setYmSignState(this.ymSignState);
        dto.setYmSignTime(this.ymSignTime);
        dto.setStationSignState(this.stationSignState);
        dto.setStationSignTime(this.stationSignTime);
        dto.setUserSignState(this.userSignState);
        dto.setUserSignTime(this.userSignTime);
        dto.setStationRenewState(this.stationRenewState);
        dto.setStationRenewTime(this.stationRenewTime);
    }
}
