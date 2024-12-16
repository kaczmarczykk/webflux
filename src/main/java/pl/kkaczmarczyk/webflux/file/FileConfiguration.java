package pl.kkaczmarczyk.webflux.file;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@AllArgsConstructor
public class FileConfiguration {

    @Bean
    RouterFunction<ServerResponse> uploadFile(FileHandler fileHandler) {
        return route(POST("api/upload"), fileHandler::upload);
    }
}
