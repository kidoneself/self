package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.HraBodyElementDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @description: 身体元素 实体类
 * @author: yu chunlei
 * @create: 2018-05-15 18:10:43
 **/
@Table(name = "hra_body_element")
@Data
public class HraBodyElement{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String elementName;
    private String elementContext;
    private String elementImg;
    private Date createTime;
    private String creator;


    public HraBodyElement() {
    }

    /**
     * 用业务对象HraBodyElementDTO初始化数据库对象HraBodyElement。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public HraBodyElement(HraBodyElementDTO dto) {
        this.id = dto.getId();
        this.elementName = dto.getElementName();
        this.elementContext = dto.getElementContext();
        this.elementImg = dto.getElementImg();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HraBodyElementDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(HraBodyElementDTO dto) {
        dto.setId(this.id);
        dto.setElementName(this.elementName);
        dto.setElementContext(this.elementContext);
        dto.setElementImg(this.elementImg);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
    }
}
