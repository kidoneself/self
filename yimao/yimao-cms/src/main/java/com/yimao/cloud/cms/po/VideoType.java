package com.yimao.cloud.cms.po;

import com.yimao.cloud.pojo.dto.cms.VideoTypeDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 视频分类
 *
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/21
 */
@Getter
@Setter
@Table(name = "t_video_type")
public class VideoType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;    //视频分类名称
    private Integer parentId;
    private Integer level;
    private Integer platform;
    private Integer sorts;
    private String image;   //视频分类图片
    private String remark;
    private Boolean deleteFlag;
    private Date createTime;
    private Date updateTime;

    public VideoType() {
    }

    public VideoType(VideoTypeDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.parentId = dto.getParentId();
        this.level = dto.getLevel();
        this.platform = dto.getPlatform();
        this.sorts = dto.getSorts();
        this.image = dto.getImage();
        this.remark = dto.getRemark();
        this.deleteFlag = dto.getDeleteFlag();
        this.updateTime = dto.getUpdateTime();
        this.createTime = dto.getCreateTime();
    }

    public void convert(VideoTypeDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setParentId(this.parentId);
        dto.setLevel(this.level);
        dto.setPlatform(this.platform);
        dto.setSorts(this.sorts);
        dto.setImage(this.image);
        dto.setRemark(this.remark);
        dto.setUpdateTime(this.updateTime);
        dto.setCreateTime(this.createTime);
    }
}
