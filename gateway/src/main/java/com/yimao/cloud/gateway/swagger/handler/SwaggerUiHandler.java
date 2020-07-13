package com.yimao.cloud.gateway.swagger.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

/**
 * @author Zhang Bo
 * @date 2018/9/30.
 */
@Component
public class SwaggerUiHandler implements HandlerFunction<ServerResponse> {

    // @Resource
    // private UiConfiguration uiConfiguration;
    //
    // @Override
    // public Mono<ServerResponse> handle(ServerRequest request) {
    //     return ServerResponse.status(HttpStatus.OK)
    //             .contentType(MediaType.APPLICATION_JSON_UTF8)
    //             .body(BodyInserters.fromObject(
    //                     Optional.ofNullable(uiConfiguration)
    //                             .orElse(UiConfigurationBuilder.builder().build())));
    // }
    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return ServerResponse.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(UiConfigurationBuilder.builder().build()));
    }

}
