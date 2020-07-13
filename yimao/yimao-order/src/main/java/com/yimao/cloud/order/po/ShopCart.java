package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.ShopCartDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhilin.he
 * @description 购物车
 * @date 2018/12/24 10:44
 **/
@Table(name = "shopping_cart")
@Getter
@Setter
public class ShopCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
//    private Integer type;                           //购物车类型：1-普通购物车；2-经销购物车；3-站长购物车；4-特批水机购物车；5-特供产品购物车；
    private Integer userId;                         //用户
    private Integer productId;                      //产品ID
    private String productName;                     //产品名称
    private String productImage;                    //产品图片
    private Integer productCategoryId;              //产品前台一级类目ID
    private String productCategoryName;             //产品前台一级类目名称
    private BigDecimal productAmountFee;            //商品总金额
    private Integer count;                          //购买数量
    private Integer terminal;                       //加入端：1-用户终端APP；2-微信公众号；3-经销商APP；4-小猫店小程序；
    private Integer costId;                         //水机产品计费方式ID
    private String costName;                        //水机产品计费方式名称
    private Date createTime;                        //创建时间
    private Date updateTime;                        //修改时间

    public ShopCart() {
    }

    /**
     * 用业务对象ShopCartDTO初始化数据库对象ShopCart。
     *
     * @param dto 业务对象
     */
    public ShopCart(ShopCartDTO dto) {
        this.id = dto.getId();
//        this.type = dto.getType();
        this.userId = dto.getUserId();
        this.productId = dto.getProductId();
        this.productName = dto.getProductName();
        this.productImage = dto.getProductImage();
        this.productCategoryId = dto.getProductCategoryId();
        this.productCategoryName = dto.getProductCategoryName();
        this.productAmountFee = dto.getProductAmountFee();
        this.count = dto.getCount();
        this.terminal = dto.getTerminal();
        this.costId = dto.getCostId();
        this.costName = dto.getCostName();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ShopCartDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(ShopCartDTO dto) {
        dto.setId(this.id);
//        dto.setType(this.type);
        dto.setUserId(this.userId);
        dto.setProductId(this.productId);
        dto.setProductName(this.productName);
        dto.setProductImage(this.productImage);
        dto.setProductCategoryId(this.productCategoryId);
        dto.setProductCategoryName(this.productCategoryName);
        dto.setProductAmountFee(this.productAmountFee);
        dto.setCount(this.count);
        dto.setTerminal(this.terminal);
        dto.setCostId(this.costId);
        dto.setCostName(this.costName);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}
