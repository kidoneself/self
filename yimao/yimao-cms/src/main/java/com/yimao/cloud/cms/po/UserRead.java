package com.yimao.cloud.cms.po;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;

/**
 * @author Lizhqiang
 * @date 2019-08-26
 */
@Data
@Table(name = "t_content_user")
public class UserRead {


    private Integer contentId;
    private Integer userId;
    private Integer hasRead;
    private Integer deleted;


}
