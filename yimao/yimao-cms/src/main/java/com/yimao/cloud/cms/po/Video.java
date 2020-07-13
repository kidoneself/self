package com.yimao.cloud.cms.po;

import com.yimao.cloud.pojo.dto.cms.VideoDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 视频
 *
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/21
 */
@Getter
@Setter
@Table(name = "t_video")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer liveId;
    private String name;    //频道（视频）名称
    private Integer liveStatus; //状态：1-直播中，2-回放，3-视频，4-预告
    private Boolean recommend;//是否为推荐视频（0-否，1-是）
    private Integer liveTypeId; //一级视频分类ID
    private Integer liveTypeSubId;//二级视频分类ID
    private Integer watchTimes;//观看次数（虚拟）
    private Integer realWatchTimes;//观看次数（真实）
    private String mobileUrl;//移动端观看地址
    private String coverImage;//频道图标地址
    private Boolean deleteFlag;//删除标识（0-未删除，1-已删除）
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private Integer sorts;//排序
    private Integer likeNum;//点赞数
    private Integer shareNum;//分享数
    private String shareContent;//分享标题
    private String detailImg;//详情图
    private Integer status; //是否发布 0-未发布 1-已发布
    private Date releaseTime; //最近一次发布时间


    public Video() {
    }

    /**
     * 用业务对象VideoDTO初始化数据库对象Video。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Video(VideoDTO dto) {
        this.liveId = dto.getLiveId();
        this.name = dto.getName();
        this.liveStatus = dto.getLiveStatus();
        this.recommend = dto.getRecommend();
        this.liveTypeId = dto.getLiveTypeId();
        this.liveTypeSubId = dto.getLiveTypeSubId();
        this.watchTimes = dto.getWatchTimes();
        this.realWatchTimes = dto.getRealWatchTimes();
        this.mobileUrl = dto.getMobileUrl();
        this.coverImage = dto.getCoverImage();
        this.deleteFlag = dto.getDeleteFlag();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.sorts = dto.getSorts();
        this.likeNum = dto.getLikeNum();
        this.shareNum = dto.getShareNum();
        this.shareContent = dto.getShareContent();
        this.detailImg = dto.getDetailImg();
        this.status = dto.getStatus();
        this.releaseTime = dto.getReleaseTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象VideoDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(VideoDTO dto) {
        dto.setLiveId(this.liveId);
        dto.setName(this.name);
        dto.setLiveStatus(this.liveStatus);
        dto.setRecommend(this.recommend);
        dto.setLiveTypeId(this.liveTypeId);
        dto.setLiveTypeSubId(this.liveTypeSubId);
        dto.setWatchTimes(this.watchTimes);
        dto.setRealWatchTimes(this.realWatchTimes);
        dto.setMobileUrl(this.mobileUrl);
        dto.setCoverImage(this.coverImage);
        dto.setDeleteFlag(this.deleteFlag);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setSorts(this.sorts);
        dto.setLikeNum(this.likeNum);
        dto.setShareNum(this.shareNum);
        dto.setShareContent(this.shareContent);
        dto.setDetailImg(this.detailImg);
        dto.setStatus(this.status);
        dto.setReleaseTime(this.releaseTime);
    }
}
