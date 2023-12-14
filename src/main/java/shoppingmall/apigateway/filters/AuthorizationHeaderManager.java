package shoppingmall.apigateway.filters;

import static shoppingmall.apigateway.authorization.AuthorizationConstants.AUTH_TYPE_BEARER;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import shoppingmall.apigateway.exception.NotExistsAuthorization;

@Slf4j
@Component
public class AuthorizationHeaderManager {

    public boolean isBearerType(String authorization) {
        return authorization.startsWith(AUTH_TYPE_BEARER);
    }

    public List<String> getAuthorizations(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> authorizations = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

        if(isNotExistsAuthorizationHeader(authorizations)) {
            throw new NotExistsAuthorization();
        }

        return authorizations;
    }

    public String parseAuthorizationToken(String authorization, String authType) {
        return authorization.replace(authType, "").trim();
    }

    public boolean isNotExistsAuthorizationHeader(List<String> authorizations) {
        return authorizations == null || authorizations.isEmpty();
    }
}
