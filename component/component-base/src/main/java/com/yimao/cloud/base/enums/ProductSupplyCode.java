package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 产品销售栏目类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品
 *
 * @author Zhang Bo
 * @date 2018/10/23.
 */
public enum ProductSupplyCode {

    PJXCP("经销产品", "PJXCP"),
    PZZZG("站长专供", "PZZZG"),
    PTPSJ("特批水机", "PTPSJ"),
    PTGCP("特供产品", "PTGCP");

    public final String name;
    public final String code;

    ProductSupplyCode(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static ProductSupplyCode find(String code) {
        return Arrays.stream(values()).filter(item -> item.code.equals(code)).findFirst().orElse(null);
    }

}
