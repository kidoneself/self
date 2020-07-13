package com.yimao.cloud.base.enums;

/**
 * @Description: 父分类编号枚举
 * @author ycl
*/
public enum ClassifyEnum {

    CLASSIFY_ZERO("父分类编号为0",0),
    CLASSIFY_ONE("父分类编号为1",1),
    CLASSIFY_TWO("父分类编号为2",2),
    CLASSIFY_THREE("父分类编号为3",3),
    CLASSIFY_FOUR("父分类编号为4",4),
    CLASSIFY_FIVE("父分类编号为5",5),
    CLASSIFY_SIX("父分类编号为6",6),
    CLASSIFY_SEVEN("父分类编号为7",7),
    CLASSIFY_EIGHT("父分类编号为8",8);


    public final String name;
    public final int value;

    ClassifyEnum(String name,int value){
        this.name = name;
        this.value = value;
    }
}
