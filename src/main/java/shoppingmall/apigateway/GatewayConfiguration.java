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

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service-1", predicateSpec -> predicateSpec
                        .path("/user-service/sign-up",
                                "/user-service/find-email",
                                "/user-service/find-pw",
                                "/user-service/login"
                        )
                        .and().method(HttpMethod.POST)
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .rewritePath("/user-service/(?<segment>.*)", "/$\\{segment}"))
                        .uri("lb://USER-SERVICE")
                )
                .route("user-service-2", predicateSpec -> predicateSpec
                        .path("/user-service/health-check", "/user-service/test-cookie")
                        .and().method(HttpMethod.GET)
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .rewritePath("/user-service/(?<segment>.*)", "/$\\{segment}"))
                        .uri("lb://USER-SERVICE")
                )
                .route("user-service-3", predicateSpec -> predicateSpec
                        .path("/user-service/users/**")
                        .and().method(HttpMethod.PUT, HttpMethod.GET)
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .rewritePath("/user-service/(?<segment>.*)", "/$\\{segment}")
                                .filter(jwtAuthorizationFilter))
                        .uri("lb://USER-SERVICE")
                )
                .route("user-service-4", predicateSpec -> predicateSpec
                        .path("/user-service/refresh")
                        .and().method(HttpMethod.GET)
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .rewritePath("/user-service/(?<segment>.*)", "/$\\{segment}"))
                        .uri("lb://USER-SERVICE")
                )
                .route("main-service-1", predicateSpec -> predicateSpec
                        .path("/main-service/partners/login")
                        .and().method(HttpMethod.POST)
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .rewritePath("/main-service/(?<segment>.*)", "/$\\{segment}")
                        )
                        .uri("lb://MAIN-SERVICE")
                )
                .route("main-service-2", predicateSpec -> predicateSpec
                        .path("/main-service/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .rewritePath("/main-service/(?<segment>.*)", "/$\\{segment}")
                                .filter(jwtAuthorizationFilter)
                        )
                        .uri("lb://MAIN-SERVICE")
                )
                .build();
    }
}
