package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 封装数据
 * @author: yu chunlei
 * @create: 2018-05-04 10:10:12
 **/
@Data
public class EvaluatingDTO implements Serializable {

    private static final long serialVersionUID = 3882526468954034041L;

    private Integer id;
    private Integer pid;
    private Integer classifyId;
    private String secondName;
    private List<MiniEvaluatingDTO> evaluatingList;

}
