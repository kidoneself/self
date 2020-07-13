package com.yimao.cloud.gateway.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author Zhang Bo
 * @date 2018/9/20.
 */
@RestController
@Slf4j
public class HystrixCommandController {

    @RequestMapping("/hystrixTimeout")
    public Mono<ServerResponse> hystrixTimeout() {
        log.error("调用服务触发了断路器。222");
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @HystrixCommand(commandKey = "hystrixCommand")
    public Mono<ServerResponse> hystrixCommand() {
        log.error("调用服务触发了断路器。111");
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
