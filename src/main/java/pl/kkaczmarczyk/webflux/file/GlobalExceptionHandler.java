package pl.kkaczmarczyk.webflux.file;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(throwable.getMessage()
                                                                             .getBytes(StandardCharsets.UTF_8));

        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        response.writeWith(Mono.just(dataBuffer));
        return response.setComplete();
    }

}