package com.yimao.cloud.station.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YunPianMsgRes {
	@JsonProperty(value = "http_status_code")
	private Integer httpStatusCode;
	private Integer code;
	private String msg;
	private String detail;

}
