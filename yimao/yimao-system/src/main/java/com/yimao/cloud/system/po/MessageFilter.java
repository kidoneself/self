//package com.yimao.cloud.system.po;
//
//import com.yimao.cloud.pojo.dto.system.MessageFilterDTO;
//import io.swagger.annotations.ApiModel;
//import lombok.Data;
//
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import java.util.Date;
//
///***
// * 功能描述:消息过滤
// *
// * @auther: liu yi
// * @date: 2019/4/29 10:23
// */
//
//@ApiModel(description = "消息过滤")
//@Table(name = "message_filter")
//@Data
//public class MessageFilter{
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//    private String province;
//    private String city;
//    private String region;
//    private Integer categoryId;
//    private String categoryName;
//    private Integer hours;
//    private Integer type;
//    private Date createTime;
//    private Date updateTime;
//
//    public MessageFilter() {
//    }
//
//    /**
//     * 用业务对象MessageFilterDTO初始化数据库对象MessageFilter。
//     * plugin author ylfjm.
//     *
//     * @param dto 业务对象
//     */
//    public MessageFilter(MessageFilterDTO dto) {
//        this.id = dto.getId();
//        this.province = dto.getProvince();
//        this.city = dto.getCity();
//        this.region = dto.getRegion();
//        this.categoryId = dto.getCategoryId();
//        this.categoryName = dto.getCategoryName();
//        this.hours = dto.getHours();
//        this.type = dto.getType();
//        this.createTime = dto.getCreateTime();
//        this.updateTime = dto.getUpdateTime();
//    }
//
//    /**
//     * 将数据库实体对象信息拷贝到业务对象MessageFilterDTO上。
//     * plugin author ylfjm.
//     *
//     * @param dto 业务对象
//     */
//    public void convert(MessageFilterDTO dto) {
//        dto.setId(this.id);
//        dto.setProvince(this.province);
//        dto.setCity(this.city);
//        dto.setRegion(this.region);
//        dto.setCategoryId(this.categoryId);
//        dto.setCategoryName(this.categoryName);
//        dto.setHours(this.hours);
//        dto.setType(this.type);
//        dto.setCreateTime(this.createTime);
//        dto.setUpdateTime(this.updateTime);
//    }
//}
