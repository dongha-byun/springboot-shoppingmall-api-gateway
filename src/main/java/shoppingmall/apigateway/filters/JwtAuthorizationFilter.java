package shoppingmall.apigateway.filters;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthorizationFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> authorizations = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

        log.info("authorizations = {}", authorizations);
        if(authorizations == null || authorizations.isEmpty()) {
            throw new IllegalArgumentException("인증 정보가 존재하지 않습니다.");
        }

        for (String header : authorizations) {

        }

        return chain.filter(exchange);
    }
}
