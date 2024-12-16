package pl.kkaczmarczyk.webflux.file;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {FileConfiguration.class, FileHandler.class, GlobalExceptionHandler.class})
@WebFluxTest
public class FileConfigurationTest {

    private final static String ENDPOINT_UPLOAD = "http://localhost:8080/api/upload";

    @Autowired
    private ApplicationContext context;

    @MockBean
    private FileService fileService;

    @MockBean
    private StorageService storageService;

    private WebTestClient webTestClient;

    @Test
    public void uploadFile() throws IOException {
        webTestClient = WebTestClient.bindToApplicationContext(context)
                                     .configureClient()
                                     .responseTimeout(Duration.ofMillis(3600000))
                                     .build();
        java.io.File textFile = new java.io.File("textFile.txt");
        textFile.createNewFile();

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("files", new FileSystemResource(textFile));

        File newFile = new File(1L, textFile.getName(), "d41d8cd98f00b204e9800998ecf8427e");
        when(this.fileService.save(any())).thenReturn(Mono.just(newFile));

        WebTestClient.ResponseSpec responseSpec = webTestClient.post()
                                                               .uri(URI.create(ENDPOINT_UPLOAD))
                                                               .contentType(MediaType.MULTIPART_FORM_DATA)
                                                               .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                                                               .exchange()
                                                               .expectStatus()
                                                               .is2xxSuccessful();

        assertEquals("[{\"name\":\"textFile.txt\",\"digest\":\"d41d8cd98f00b204e9800998ecf8427e\"}]",
                     responseSpec.expectBody(String.class)
                                 .returnResult()
                                 .getResponseBody());

        textFile.delete();
    }

    @Test
    public void notUploadFile() {
        webTestClient = WebTestClient.bindToApplicationContext(context)
                                     .configureClient()
                                     .responseTimeout(Duration.ofMillis(3600000))
                                     .build();

        webTestClient.post()
                     .uri(URI.create(ENDPOINT_UPLOAD))
                     .contentType(MediaType.MULTIPART_FORM_DATA)
                     .exchange()
                     .expectStatus()
                     .is5xxServerError();
    }
}