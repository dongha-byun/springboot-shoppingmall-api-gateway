package shoppingmall.apigateway;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import shoppingmall.apigateway.filters.JwtAuthorizationFilter;

@RequiredArgsConstructor
@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service-1", predicate -> predicate
                        .path("/user-service/sign-up",
                                "/user-service/find-email",
                                "/user-service/find-pw",
                                "/user-service/login"
                        )
                        .and().method(HttpMethod.POST)
                        .filters(gatewayFilter -> gatewayFilter
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .rewritePath("/user-service/(?<segment>.*)", "/$\\{segment}"))
                        .uri("lb://USER-SERVICE")
                )
                .route("user-service-2", predicate -> predicate
                        .path("/user-service/health-check")
                        .and().method(HttpMethod.GET)
                        .filters(gatewayFilter -> gatewayFilter
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .rewritePath("/user-service/(?<segment>.*)", "/$\\{segment}"))
                        .uri("lb://USER-SERVICE")
                )
                .route("user-service-3", predicate -> predicate
                        .path("/user-service/users/**")
                        .and().method(HttpMethod.PUT, HttpMethod.GET)
                        .filters(gatewayFilter -> gatewayFilter
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .rewritePath("/user-service/(?<segment>.*)", "/$\\{segment}")
                                .filter(new JwtAuthorizationFilter()))
                        .uri("lb://USER-SERVICE")
                )
                .build();
    }
}
