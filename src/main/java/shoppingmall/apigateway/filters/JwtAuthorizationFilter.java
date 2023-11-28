package shoppingmall.apigateway.filters;

import static shoppingmall.apigateway.AuthorizationConstants.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import shoppingmall.apigateway.authorization.jwt.JwtManager;
import shoppingmall.apigateway.exception.AccessTokenExpiredException;
import shoppingmall.apigateway.exception.NotExistsAuthorization;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter implements GatewayFilter {

    private final ObjectMapper objectMapper;
    private final JwtManager jwtManager;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("JwtAuthorizationFilter begin");
        try{
            List<String> authorizations = getAuthorizations(exchange);

            if(isNotExistsAuthorizationHeader(authorizations)) {
                throw new NotExistsAuthorization();
            }

            String authorization = authorizations.stream()
                    .filter(this::isBearerType)
                    .findFirst()
                    .orElseThrow(NotExistsAuthorization::new);

            String jwtToken = parseAuthorizationToken(authorization);
            if(jwtManager.isValidateExpire(jwtToken)) {
                throw new AccessTokenExpiredException();
            }

            exchange.getRequest().mutate().header(X_GATEWAY_HEADER, jwtManager.getSubjectOf(jwtToken));
            return chain.filter(exchange);
        } catch(NotExistsAuthorization e1) {
            return sendErrorResponse(exchange, 701, e1);
        } catch(AccessTokenExpiredException e2) {
            return sendErrorResponse(exchange, 702, e2);
        } catch(Exception e3){
            return sendErrorResponse(exchange, 999, e3);
        }
    }

    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, int errorCode, Exception e) {
        try {
            ErrorResponse errorResponse = new ErrorResponse(errorCode, e.getMessage());
            String errorBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorResponse);

            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBuffer buffer = response.bufferFactory().wrap(errorBody.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Flux.just(buffer));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean isBearerType(String authorization) {
        return authorization.startsWith(AUTH_TYPE);
    }

    private List<String> getAuthorizations(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        return request.getHeaders().get(HttpHeaders.AUTHORIZATION);
    }

    private String parseAuthorizationToken(String authorization) {
        return authorization.replace(AUTH_TYPE, "").trim();
    }

    private boolean isNotExistsAuthorizationHeader(List<String> authorizations) {
        return authorizations == null || authorizations.isEmpty();
    }

    record ErrorResponse(int code, String message){}
}
