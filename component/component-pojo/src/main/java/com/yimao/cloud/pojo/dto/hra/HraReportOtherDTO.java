package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-11-21 17:19:34
 **/
@Data
public class HraReportOtherDTO implements Serializable {

    private static final long serialVersionUID = -1278305290422308552L;
    private Integer id;
    private Integer userId;
    private Integer shareUserId;//被分享用户ID
    private String ticketNo;
    private String mobile;
    private Date createTime;
}
