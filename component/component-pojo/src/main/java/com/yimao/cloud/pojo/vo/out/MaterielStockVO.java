package com.yimao.cloud.pojo.vo.out;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@ApiModel(description = "库存")
public class MaterielStockVO implements Serializable {
    private static final long serialVersionUID = 4751364039558144622L;
    private String status;
    private String id;
    private String materielId;
    private String batchCode;
    private String snCode;
    private String materielName;
    private String materielTypeName;
    private String materielType;
    private String engineerPuDate;
    private String fitTypeName;
    private String materielCode;
    private String productScope;
    private String engineerPutDate;

}
