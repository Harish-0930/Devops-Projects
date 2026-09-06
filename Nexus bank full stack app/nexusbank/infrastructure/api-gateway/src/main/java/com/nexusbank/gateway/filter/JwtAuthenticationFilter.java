package com.nexusbank.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> PUBLIC_PATHS = List.of(
        "/api/customers/register",
        "/api/customers/login",
        "/api/admin/login",
        "/h2-console",
        "/actuator"
    );

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            if (isPublicPath(path)) {
                return chain.filter(exchange);
            }

            if (!hasAuthHeader(request)) {
                return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String token = getToken(request);
            if (!jwtUtil.validateToken(token)) {
                return onError(exchange, "Invalid or Expired Token", HttpStatus.UNAUTHORIZED);
            }

            String role = jwtUtil.getRoleFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);

            if (path.startsWith("/api/admin") && !"ADMIN".equals(role)) {
                return onError(exchange, "Admin access required", HttpStatus.FORBIDDEN);
            }

            ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Name", username)
                .header("X-User-Role", role)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::contains);
    }

    private boolean hasAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
    }

    private String getToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    public static class Config {
    }
}