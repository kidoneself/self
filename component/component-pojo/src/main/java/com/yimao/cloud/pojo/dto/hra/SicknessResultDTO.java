package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 症状疾病数据封装
 * @author: yu chunlei
 * @create: 2018-05-24 10:42:33
 **/
@Data
public class SicknessResultDTO implements Serializable {

    private static final long serialVersionUID = 4623237288962176893L;

    private List<MiniSicknessDTO> sickDtoList;

}
