package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.ActivityExchangeDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/4/10
 */
@Table(name = "hra_exchange")
@Data
public class ActivityExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String exchangeCode;            //兑换码 6位
    private String batchNumber;             //批次号
    private Integer exchangeFrom;           //兑换来源  1-京东 2-其他
    private Integer userId;                 //兑换用户
    private Integer channel;                //渠道
    private Integer side;                   //端     1-公众号  2-小程序
    private String ticketNo;                //体检卡号
    private Integer num;                    //兑换数量
    private Integer exchangeStatus;         //兑换状态 1-未兑换 2-兑换成功  3-兑换失败 4-活动过期 5-禁止兑换
    private Date exchangeTime;              //兑换时间
    private Date beginTime;                 //活动开始时间
    private Date endTime;                   //活动截止时间
    private String creator;                 //生成人
    private Date createTime;                //生成时间


    public ActivityExchange() {
    }

    /**
     * 用业务对象ExchangeActivityDTO初始化数据库对象ExchangeActivity。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public ActivityExchange(ActivityExchangeDTO dto) {
        this.id = dto.getId();
        this.exchangeCode = dto.getExchangeCode();
        this.batchNumber = dto.getBatchNumber();
        this.exchangeFrom = dto.getExchangeFrom();
        this.userId = dto.getUserId();
        this.channel = dto.getChannel();
        this.side = dto.getSide();
        this.ticketNo = dto.getTicketNo();
        this.num = dto.getNum();
        this.exchangeStatus = dto.getExchangeStatus();
        this.exchangeTime = dto.getExchangeTime();
        this.beginTime = dto.getBeginTime();
        this.endTime = dto.getEndTime();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ExchangeActivityDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(ActivityExchangeDTO dto) {
        dto.setId(this.id);
        dto.setExchangeCode(this.exchangeCode);
        dto.setBatchNumber(this.batchNumber);
        dto.setExchangeFrom(this.exchangeFrom);
        dto.setUserId(this.userId);
        dto.setChannel(this.channel);
        dto.setSide(this.side);
        dto.setTicketNo(this.ticketNo);
        dto.setNum(this.num);
        dto.setExchangeStatus(this.exchangeStatus);
        dto.setExchangeTime(this.exchangeTime);
        dto.setBeginTime(this.beginTime);
        dto.setEndTime(this.endTime);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
    }
}
