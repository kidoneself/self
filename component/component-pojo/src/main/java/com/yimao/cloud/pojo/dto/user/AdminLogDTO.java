package com.yimao.cloud.pojo.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/11/13.
 */
@Data
public class AdminLogDTO implements Serializable {

    private static final long serialVersionUID = -9087730402298893089L;
    private Integer id;
    private String userName;
    private String realName;
    private Integer type;//管理员类型：1-系统管理员，2-服务站管理员
    private String ip;//登录ip
    private Boolean isSuccess;//0-登录失败；1-登录成功
    private String cause;//登录失败原因
    private Date time;

}
