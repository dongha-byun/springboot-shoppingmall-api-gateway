package shoppingmall.apigateway.filters;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class JwtAuthorizationFilter implements GatewayFilter {

    private final SecretKey secretKey =
            Keys.hmacShaKeyFor("secret_key_of_dong_ha_do_not_snap_this".getBytes(StandardCharsets.UTF_8));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> authorizations = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

        if(authorizations == null || authorizations.isEmpty()) {
            throw new IllegalArgumentException("인증 정보가 존재하지 않습니다.");
        }

        for (String authorization : authorizations) {
            if(authorization.startsWith("Bearer")) {
                String jwtToken = authorization.replace("Bearer", "").trim();
                try{
                    Date expiration = Jwts.parser().verifyWith(secretKey)
                            .build()
                            .parseSignedClaims(jwtToken)
                            .getPayload()
                            .getExpiration();

                    if(expiration.before(new Date())) {
                        throw new IllegalStateException("인증 정보가 만료됐습니다.");
                    }

                    String subject = Jwts.parser().verifyWith(secretKey)
                            .build()
                            .parseSignedClaims(jwtToken)
                            .getPayload()
                            .getSubject();
                    exchange.getRequest().mutate().header("X-GATEWAY-EMAIL", subject);

                }catch(Exception e1){
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
        }

        return chain.filter(exchange);
    }

}
