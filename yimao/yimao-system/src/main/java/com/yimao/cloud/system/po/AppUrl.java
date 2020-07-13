package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.AppUrlDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 描述：翼猫APP客户端获取动态url
 *
 * @Author Zhang Bo
 * @Date 2019/11/12
 */
@Table(name = "app_url")
@Getter
@Setter
public class AppUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String apiUrl;    //接口访问域名
    private String imgUrl;    //图片域名
    private String shareUrl;  //分享域名
    private String env;       //环境：test-测试；pro-生产
    private Integer version;  //版本号

    public AppUrl() {
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象AppUrlDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(AppUrlDTO dto) {
        dto.setApiUrl(this.apiUrl);
        dto.setImgUrl(this.imgUrl);
        dto.setShareUrl(this.shareUrl);
        dto.setEnv(this.env);
    }
}
