package shoppingmall.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shoppingmall.apigateway.filters.JwtAuthorizationFilter;

@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("main-service", predicateSpec -> predicateSpec
                        .path("/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .filter(new JwtAuthorizationFilter())
                                .addResponseHeader("X-SHOP-HEADER", "DONG-HA")
                        )
                        .uri("lb://MAIN-SERVICE")
                )
                .build();
    }
}
