package com.yimao.cloud.system.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 描述：文件
 *
 * @Author Zhang Bo
 * @Date 2019/3/13
 */
@Table(name = "system_file")
@Getter
@Setter
public class SystemFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;            //文件名
    private String path;            //文件路径
    private Integer size;           //文件大小
    private String folder;          //文件夹名称
    private String remark;          //备注
}
