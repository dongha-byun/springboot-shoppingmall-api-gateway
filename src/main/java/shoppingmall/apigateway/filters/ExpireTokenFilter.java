package shoppingmall.apigateway.filters;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import shoppingmall.apigateway.exception.NotExistsAuthorization;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExpireTokenFilter implements GatewayFilter {

    private final AuthorizationHeaderManager authorizationHeaderManager;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> authorizations = authorizationHeaderManager.getAuthorizations(exchange);
        log.info("authorizations = {}", authorizations.toString());

        String authorization = authorizations.stream()
                .filter(authorizationHeaderManager::isBearerType)
                .findFirst()
                .orElseThrow(NotExistsAuthorization::new);

        exchange.getRequest().mutate().header("expire-token", authorization);
        return chain.filter(exchange);
    }
}
