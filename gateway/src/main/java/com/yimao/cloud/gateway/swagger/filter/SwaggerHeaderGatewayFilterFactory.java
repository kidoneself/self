package com.yimao.cloud.gateway.swagger.filter;

import com.yimao.cloud.gateway.swagger.config.SwaggerProvider;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class SwaggerHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory {

    private static final String X_FORWARDED_PREFIX = "X-Forwarded-Prefix";

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            System.out.println("****************************************");
            System.out.println("path=" + path);
            if (path == null || !path.endsWith(SwaggerProvider.API_URI)) {
                return chain.filter(exchange);
            }

            String basePath = path.substring(0, path.lastIndexOf(SwaggerProvider.API_URI));
            System.out.println("basePath=" + basePath);
            System.out.println("****************************************");

            ServerHttpRequest newRequest = request.mutate().header(X_FORWARDED_PREFIX, basePath).build();
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
            return chain.filter(newExchange);
        };
    }
}
