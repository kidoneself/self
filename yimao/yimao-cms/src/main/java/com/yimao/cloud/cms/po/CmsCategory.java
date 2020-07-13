package com.yimao.cloud.cms.po;

import com.yimao.cloud.pojo.dto.cms.CmsCategoryDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 内容和 视频分类
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Table(name = "t_category")
@Getter
@Setter
public class CmsCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //分类名称
    private String name;
    //父id
    private Integer parentId;
    //分类等级
    private Integer level;
    //类别类型：2 总部 文章分类，3 服务站视频分类，4 服务站文章分类
    private Integer type;
    //状态 1 有效 2 无效
    private Integer status;
    //端 1 经销商app 2 微信公众号  3 小程序 4 站务系统
    private Integer platform;
    //前端展示的位置：1.资讯 2.公告 3.协议
    private Integer location;
    //排序
    private Integer sorts;
    //创建时间
    private Date createTime;
    //修改时间
    private Date updateTime;

    public CmsCategory() {
    }

    /**
     * 用业务对象CmsCategoryDTO初始化数据库对象CmsCategory。
     *
     * @param dto 业务对象
     */
    public CmsCategory(CmsCategoryDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.parentId = dto.getParentId();
        this.level = dto.getLevel();
        this.type = dto.getType();
        this.status = dto.getStatus();
        this.platform = dto.getPlatform();
        this.location = dto.getLocation();
        this.sorts = dto.getSorts();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象CmsCategoryDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(CmsCategoryDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setParentId(this.parentId);
        dto.setLevel(this.level);
        dto.setType(this.type);
        dto.setStatus(this.status);
        dto.setPlatform(this.platform);
        dto.setLocation(this.location);
        dto.setSorts(this.sorts);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}
