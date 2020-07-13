package com.yimao.cloud.pojo.dto.cat;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-11-17 09:46:03
 **/
@Data
public class RuleDetailInfoDTO implements Serializable {

    private static final long serialVersionUID = -1509633859982371752L;
    private Long ruleDetailId;
    private String ruleName;
    private String ruleInfo;//编辑规则信息
    //private List<RuleDetailDTO> list;
}
