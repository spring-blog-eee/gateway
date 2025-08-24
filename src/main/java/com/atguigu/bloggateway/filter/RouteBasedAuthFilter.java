package com.atguigu.bloggateway.filter;

import com.atguigu.bloggateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class RouteBasedAuthFilter extends AbstractGatewayFilterFactory<RouteBasedAuthFilter.Config> {

    public RouteBasedAuthFilter()
    {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config)
    {
        return (exchange, chain) ->
        {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 如果配置为跳过认证，直接放行
            if (!config.isRequiredAuth())
                return chain.filter(exchange);

            // 获取Token
            String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String token;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
                token = authorizationHeader.substring(7);
            else
                token = authorizationHeader;

            // 验证Token
            if (token == null || token.trim().isEmpty())
            {
                log.warn("请求缺少JWT Token - 路径: {}", request.getPath());
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            if (!JwtUtil.validateToken(token))
            {
                log.warn("JWT Token验证失败 - 路径: {}", request.getPath());
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            // 从Token中提取用户信息并添加到请求头
            List<String> authorities = JwtUtil.getUserAuthorityFromToken(token);
            log.debug("接收到权限信息：{}", authorities);

            // 将用户信息添加到请求头，传递给下游服务
            if (authorities == null || !authorities.contains("ROLE_USER"))
            {
                log.warn("权限信息提取失败 - 路径: {}", request.getPath());
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    @Override
    public List<String> shortcutFieldOrder()
    {
        return Arrays.asList("requiredAuth");
    }

    public static class Config
    {
        // 判断用户角色，默认为所有角色都可以访问
        // 根据不同的角色，在getUserAuthorityFromToken()方法中进行不同的判断
//        private List<String> roles;

        private boolean requiredAuth = true;

        public boolean isRequiredAuth() {
            return requiredAuth;
        }

        public void setRequiredAuth(boolean requiredAuth) {
            this.requiredAuth = requiredAuth;
        }
    }
}
