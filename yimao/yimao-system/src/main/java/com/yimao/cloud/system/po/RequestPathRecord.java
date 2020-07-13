package com.yimao.cloud.system.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 描述：请求接口路径日志
 *
 * @Author Zhang Bo
 * @Date 2019/10/22
 */
@Table(name = "request_path_record")
@Getter
@Setter
public class RequestPathRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String path;

}
