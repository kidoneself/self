package com.yimao.cloud.cms.po;

import com.yimao.cloud.pojo.dto.cms.BannerDTO;
import java.util.Date;
import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * 广告
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@Table(name = "t_banner")
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //banner名称
    private String name;
    //位置code
    private String positionCode;
    //标题
    private String title;
    //内容
    private String content;
    //banner跳转url
    private String url;
    //图片url
    private String adImg;
    //状态1可用 2删除
    private Integer status;
    //排序编号
    private Integer sorts;
    //创建日期
    private Date createTime;
    //创建人
    private String creator;
    //更新日期
    private Date updateTime;
    //更新人
    private String updater;
    //展示端 1-健康e家公众号；2-小猫店小程序；3-经销商APP；4-服务站站务系统；5-安装工app
    private Integer terminal;


    public Banner() {
    }

    /**
     * 用业务对象BannerDTO初始化数据库对象Banner。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Banner(BannerDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.positionCode = dto.getPositionCode();
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.url = dto.getUrl();
        this.adImg = dto.getAdImg();
        this.status = dto.getStatus();
        this.sorts = dto.getSorts();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
        this.terminal = dto.getTerminal();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象BannerDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(BannerDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setPositionCode(this.positionCode);
        dto.setTitle(this.title);
        dto.setContent(this.content);
        dto.setUrl(this.url);
        dto.setAdImg(this.adImg);
        dto.setStatus(this.status);
        dto.setSorts(this.sorts);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
        dto.setTerminal(this.terminal);
    }
}