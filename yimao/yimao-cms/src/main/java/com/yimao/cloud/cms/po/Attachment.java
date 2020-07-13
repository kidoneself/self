package com.yimao.cloud.cms.po;

import java.util.Date;
import java.io.Serializable;
import javax.persistence.Table;
import lombok.Data;

/**
 * 附件表，用于保存用户上传的附件内容。(AttachmentDTO)实体类
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@Table(name = "t_attachment")
public class Attachment {
    //ID主键
    private Object id;
    //文件名
    private String fileName;
    //上传附件的用户ID
    private String userName;
    //路径
    private String path;
    //文件类型：jpg，png...
    private String fileType;
    //上传时间
    private Date createTime;

}