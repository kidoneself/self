package com.yimao.cloud.framework.aop.parser;

import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;

import java.util.Map;

public interface ContentParser {
    Object getResult(Map<String, Object> feildValues, EnableOperationLog enableOperateLog);
}

