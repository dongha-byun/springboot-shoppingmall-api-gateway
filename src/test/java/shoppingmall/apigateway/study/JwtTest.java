package shoppingmall.apigateway.study;

import static org.assertj.core.api.Assertions.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.KEY;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JwtTest {

    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            "test_key_of_jwt_token_for_unit_test".getBytes(StandardCharsets.UTF_8)
    );

    @Test
    @DisplayName("JWT 인증 유효기간이 지나면, 토큰을 parsing 할 수 없다.")
    void jwt_parsing_fail_with_expire_date() {
        // 토큰의 유효기간을 현재시간 이전으로 설정해서 생성하고
        // 토큰을 parsing 했을 때,
        // 예외가 발생한다.
        // given
        Date current = new Date();
        String token = Jwts.builder()
                .subject("100")
                .issuedAt(new Date(current.getTime() - (5 * 60 * 1000L)))
                .expiration(new Date(current.getTime() - (60 * 1000L)))
                .signWith(secretKey)
                .compact();

        // when & then
        assertThatThrownBy(
                () -> Jwts.parser().verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)// 여기서 예외 발생 -> 아래 getSubject 까지 못 감
                        .getPayload()
                        .getSubject()
        );
    }
}
