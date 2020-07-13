package com.yimao.cloud.pojo.dto.cms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * 附件表，用于保存用户上传的附件内容。(AttachmentDTO)实体类
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@ApiModel(description = "附件信息")
public class AttachmentDTO implements Serializable {
    private static final long serialVersionUID = 936879621654430877L;
    //ID主键
    @ApiModelProperty(value ="ID主键")
    private Object id;
    //文件名
    @ApiModelProperty(value ="文件名")
    private String fileName;
    //上传附件的用户ID
    @ApiModelProperty(value ="上传附件的用户ID")
    private String userName;
    //路径
    @ApiModelProperty(value ="路径")
    private String path;
    //文件类型：jpg，png...
    @ApiModelProperty(value ="文件类型：jpg，png...")
    private String fileType;
    //上传时间
    @ApiModelProperty(value ="上传时间")
    private Date createTime;

}