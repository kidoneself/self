package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.StockOperationDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zhilin.he
 * @description 云平台库存操作
 * @date 2019/4/30 16:01
 **/
@Table(name = "stock_operation")
@Getter
@Setter
public class StockOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;              //库存操作id
    private String admin;            //管理员
    private Integer operation;       //操作类型
    private String originalProvince; //原库存省
    private String originalCity;     //原库存市
    private String originalRegion;   //原库存区
    private String operateProvince;  //操作库存省
    private String operateCity;      //操作库存市
    private String operateRegion;    //操作库存区
    private String deviceName;       //设备名称
    private Integer count;           //数量
    private Integer special;         //是否特殊库存：0-否，1-是
    private Date createTime;         //创建时间


    public StockOperation() {
    }

    /**
     * 用业务对象StockOperationDTO初始化数据库对象StockOperation。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StockOperation(StockOperationDTO dto) {
        this.admin = dto.getAdmin();
        this.operation = dto.getOperation();
        this.originalProvince = dto.getOriginalProvince();
        this.originalCity = dto.getOriginalCity();
        this.originalRegion = dto.getOriginalRegion();
        this.operateProvince = dto.getOperateProvince();
        this.operateCity = dto.getOperateCity();
        this.operateRegion = dto.getOperateRegion();
        this.deviceName = dto.getDeviceName();
        this.count = dto.getCount();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StockOperationDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StockOperationDTO dto) {
        dto.setAdmin(this.admin);
        dto.setOperation(this.operation);
        dto.setOriginalProvince(this.originalProvince);
        dto.setOriginalCity(this.originalCity);
        dto.setOriginalRegion(this.originalRegion);
        dto.setOperateProvince(this.operateProvince);
        dto.setOperateCity(this.operateCity);
        dto.setOperateRegion(this.operateRegion);
        dto.setDeviceName(this.deviceName);
        dto.setCount(this.count);
        dto.setCreateTime(this.createTime);
    }
}
