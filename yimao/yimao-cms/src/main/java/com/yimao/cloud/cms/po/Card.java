package com.yimao.cloud.cms.po;

import com.yimao.cloud.pojo.dto.cms.CardDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * 代言卡和宣传卡
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@Table(name = "t_card")
public class Card {
    //id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //卡类型 1 代言卡 2 宣传卡
    private Integer cardType;
    //卡类型的code 比如：代言卡的健康食品code
    private String typeCode;
    //标题
    private String title;
    //标语（分享出去的标题），多个以逗号分隔。
    private String tag;
    //背景图url
    private String backgroundImg;
    //卡图片url
    private String cardImg;
    //文案
    private String content;
    //文字颜色code
    private String textColor;
    //按钮颜色code
    private String button;
    //排序
    private Integer sorts;
    //状态1已发布 2 未发布以保存，3 已删除
    private Integer cardStatus;

    //H5链接地址
    private String h5Url;


    public Card() {
    }

    /**
     * 用业务对象CardDTO初始化数据库对象Card。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Card(CardDTO dto) {
        this.id = dto.getId();
        this.cardType = dto.getCardType();
        this.typeCode = dto.getTypeCode();
        this.title = dto.getTitle();
        this.tag = dto.getTag();
        this.backgroundImg = dto.getBackgroundImg();
        this.cardImg = dto.getCardImg();
        this.content = dto.getContent();
        this.textColor = dto.getTextColor();
        this.button = dto.getButton();
        this.sorts = dto.getSorts();
        this.cardStatus = dto.getCardStatus();
        this.h5Url = dto.getH5Url();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象CardDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(CardDTO dto) {
        dto.setId(this.id);
        dto.setCardType(this.cardType);
        dto.setTypeCode(this.typeCode);
        dto.setTitle(this.title);
        dto.setTag(this.tag);
        dto.setBackgroundImg(this.backgroundImg);
        dto.setCardImg(this.cardImg);
        dto.setContent(this.content);
        dto.setTextColor(this.textColor);
        dto.setButton(this.button);
        dto.setSorts(this.sorts);
        dto.setCardStatus(this.cardStatus);
        dto.setH5Url(this.h5Url);
    }
}