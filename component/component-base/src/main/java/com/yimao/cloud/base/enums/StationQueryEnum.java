package com.yimao.cloud.base.enums;

public enum StationQueryEnum {
	ListQuery("list"),InfoQuery("info");
	
	private String queryType;
	StationQueryEnum(String queryType){
		this.queryType=queryType;
	}
	
	public String value() {
        return this.queryType;
    }
}
