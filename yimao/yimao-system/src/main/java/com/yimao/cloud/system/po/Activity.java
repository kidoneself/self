package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.ActivityDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 活动
 *
 * @author Lizhqiang
 * @date 2019/4/10
 */
@Table(name = "t_activity")
@Data
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;//标题
    private String content;//内容
    private String acUrl;//url
    private String acImg;//图片
    private Integer acType;//类型  1：普通活动  2：京东兑换活动
    private Date beginTime;//活动开始时间
    private Date endTime;//活动结束时间
    private Integer sorts;//排序
    private Boolean deleteFlag;//是否删除 1：删除  0 ：未删除
    private Integer side;//端 1-公众号 2-小程序
    private Integer layoutType;//布局类型： 1-单图模式 2-双图模式 3-三图模式
    private String backImg;//背景图片 如果是兑换活动-用背景图
    private String backColor;//背景颜色
    private String channel;//渠道 只有兑换活动才在后台设置渠道，通过渠道-生成对应渠道的体检卡样式，多个以逗号分隔
    private String creator;
    private Date createTime;
    private Date updateTime;
    private String updater;

    public Activity() {
    }

    /**
     * 用业务对象ActivityDTO初始化数据库对象Activity。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Activity(ActivityDTO dto) {
        this.id = dto.getId();
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.acUrl = dto.getAcUrl();
        this.acImg = dto.getAcImg();
        this.acType = dto.getAcType();
        this.beginTime = dto.getBeginTime();
        this.endTime = dto.getEndTime();
        this.sorts = dto.getSorts();
        this.deleteFlag = dto.getDeleteFlag();
        this.side = dto.getSide();
        this.layoutType = dto.getLayoutType();
        this.backImg = dto.getBackImg();
        this.backColor = dto.getBackColor();
        this.channel = dto.getChannel();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ActivityDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(ActivityDTO dto) {
        dto.setId(this.id);
        dto.setTitle(this.title);
        dto.setContent(this.content);
        dto.setAcUrl(this.acUrl);
        dto.setAcImg(this.acImg);
        dto.setAcType(this.acType);
        dto.setBeginTime(this.beginTime);
        dto.setEndTime(this.endTime);
        dto.setSorts(this.sorts);
        dto.setDeleteFlag(this.deleteFlag);
        dto.setSide(this.side);
        dto.setLayoutType(this.layoutType);
        dto.setBackImg(this.backImg);
        dto.setBackColor(this.backColor);
        dto.setChannel(this.channel);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
