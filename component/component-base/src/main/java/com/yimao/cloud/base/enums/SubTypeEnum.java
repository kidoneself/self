package com.yimao.cloud.base.enums;

/**
 * @Author ycl
 * @Description 子审核类型
 * @Date 15:47 2019/10/14
 * @Param
**/
public enum SubTypeEnum {

    /**
     * 子审核类型  1-业务部门审核，2-400客服审核（租赁商品），3-400客服提交物流，4-物资审核，5-经销商审核；
    **/
    BUSINESS_UNIT_AUDIT("业务部门审核", 1),
    CUSTOMER_SERVICE_AUDIT("400客服审核（租赁商品）", 2),
    CUSTOMER_SERVICE_SUBMISSION_LOGISTICS("400客服提交物流", 3),
    MATERIAL_REVIEW("物资审核", 4),
    DISTRIBUTOR_AUDIT("经销商审核", 5);

    public final String name;
    public final int value;

    SubTypeEnum(String name,int value){
        this.name = name;
        this.value = value;
    }
}
