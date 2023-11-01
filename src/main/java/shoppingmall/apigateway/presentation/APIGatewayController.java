package shoppingmall.apigateway.presentation;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class APIGatewayController {

    private final Environment env;

    public APIGatewayController(Environment env) {
        this.env = env;
    }

    @GetMapping("/health-check")
    public String healthCheck() {
        return "It's Working in API Gateway of Shopping mall "
                + "\n - property(server.port) = " + env.getProperty("server.port");
    }
}
