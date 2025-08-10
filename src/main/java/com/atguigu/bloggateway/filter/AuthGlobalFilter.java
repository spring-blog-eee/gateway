package com.atguigu.bloggateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.atguigu.bloggateway.util.JwtUtil.validateToken;

//@Component
//public class AuthGlobalFilter implements GlobalFilter, Ordered
//{
//
//    // 假设你的鉴权服务URL，或者直接在网关内部实现鉴权逻辑
//    // private final WebClient webClient; // 如果需要远程调用鉴权服务
//
//    // public AuthGlobalFilter(WebClient.Builder webClientBuilder) {
//    //     this.webClient = webClientBuilder.build();
//    // }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        ServerHttpResponse response = exchange.getResponse();
//
//        // 1. 获取Token
//        HttpHeaders headers = request.getHeaders();
//        String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
//
//        // 假设Token以 "Bearer " 开头
//        String token = null;
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            token = authorizationHeader.substring(7); // 提取Token字符串
//        }
//
//        // 2. 验证Token (这里是简化逻辑，实际项目中会更复杂)
//        if (token == null || token.isEmpty()) {
//            // 没有Token，返回未授权
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
//        }
//
//        // --- 实际的Token验证逻辑 ---
//        // 2.1. 验证Token是否有效（例如：JWT解析验证签名、过期时间）
//        // 这里可以调用一个内部的工具类或鉴权服务
//        boolean isValidToken = validateToken(token); // 假设这个方法实现了验证
//
//        if (!isValidToken) {
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
//        }
//
//        // 2.2. 如果需要，可以从Token中解析出用户信息并传递给下游服务
//        // 例如：将用户ID放入请求头，方便后端服务获取
//        // String userId = parseUserIdFromToken(token);
//        // ServerHttpRequest authenticatedRequest = request.mutate()
//        //         .header("X-User-ID", userId)
//        //         .build();
//        // return chain.filter(exchange.mutate().request(authenticatedRequest).build());
//
//        // Token 有效，继续执行过滤器链
//        return chain.filter(exchange);
//    }
//
//    @Override
//    public int getOrder() {
//        // 越小优先级越高，确保在路由之前进行鉴权
//        return -100;
//    }
//}