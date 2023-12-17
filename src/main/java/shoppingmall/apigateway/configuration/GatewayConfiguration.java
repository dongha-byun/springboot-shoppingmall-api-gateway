package shoppingmall.apigateway.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import shoppingmall.apigateway.filters.ExpireTokenFilter;
import shoppingmall.apigateway.filters.JwtAuthorizationFilter;

@RequiredArgsConstructor
@Configuration
public class GatewayConfiguration {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final ExpireTokenFilter expireTokenFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service-1", predicateSpec -> predicateSpec
                        .path("/find-email", "/find-pw",
                                "/sign-up","/login"
                        )
                        .and().method(HttpMethod.POST)
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE))
                        .uri("lb://USER-SERVICE")
                )
                .route("user-service-2", predicateSpec -> predicateSpec
                        .path("/user-service/health-check",
                                "/user-service/test-cookie")
                        .and().method(HttpMethod.GET)
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .rewritePath("/user-service/(?<segment>.*)", "/$\\{segment}"))
                        .uri("lb://USER-SERVICE")
                )
                .route("user-service-3", predicateSpec -> predicateSpec
                        .path("/users/**")
                        .and().method(HttpMethod.PUT, HttpMethod.GET)
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .filter(jwtAuthorizationFilter))
                        .uri("lb://USER-SERVICE")
                )
                .route("user-service-4", predicateSpec -> predicateSpec
                        .path("/refresh", "/logout")
                        .and().method(HttpMethod.POST)
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .filter(expireTokenFilter))
                        .uri("lb://USER-SERVICE")
                )
                .route("main-service-1", predicateSpec -> predicateSpec
                        .path("/partners/login")
                        .and().method(HttpMethod.POST)
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE)
                        )
                        .uri("lb://MAIN-SERVICE")
                )
                .route("main-service-2", predicateSpec -> predicateSpec
                        .order(Integer.MAX_VALUE)
                        .path("/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .filter(jwtAuthorizationFilter)
                        )
                        .uri("lb://MAIN-SERVICE")
                )
                .route("category-service-1", predicateSpec -> predicateSpec
                        .path("/category-service/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE)
                                .rewritePath("/category-service/(?<segment>.*)", "/$\\{segment}")
                        )
                        .uri("lb://MAIN-SERVICE")
                )
                .route("image-viewer", predicateSpec -> predicateSpec
                        .path("/image/**")
                        .uri("lb://MAIN-SERVICE")
                )
                .build();
    }
}
