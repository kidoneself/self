package com.yimao.cloud.framework.configuration;

import com.yimao.cloud.base.exception.YimaoRemoteException;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * 描述:远程异常会被Feign Hystrix拦截，HystrixBadRequestException不会被Feign Hystrix拦截
 * <b/>
 * 将业务异常包装成HystrixBadRequestException类型异常，使其能被全局异常处理器捕获。
 *
 * @Author Zhang Bo
 * @Date 2019/1/10 13:41
 * @Version 1.0
 */
@Configuration
@Slf4j
public class YimaoFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String body = Util.toString(response.body().asReader());
            int status = response.status();
            if (400 <= status && status < 500) {
                return new YimaoRemoteException(status, body);
            }
            return FeignException.errorStatus(methodKey, response);
        } catch (Exception var4) {
            log.error(var4.getMessage());
            return new YimaoRemoteException("远程异常解析错误。");
        }
    }

}