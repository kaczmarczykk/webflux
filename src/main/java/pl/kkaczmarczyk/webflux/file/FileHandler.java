package pl.kkaczmarczyk.webflux.file;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class FileHandler {

    private static final String MD5_CALCULATION_EXCEPTION = "MD5_CALCULATION_EXCEPTION";

    private final StorageService storageService;
    private final FileService fileService;

    public FileHandler(StorageService storageService, FileService fileService) {
        this.storageService = storageService;
        this.fileService = fileService;
    }

    public Mono<ServerResponse> upload(ServerRequest request) {
        Mono<List<File>> list = request.body(BodyExtractors.toParts())
                                       .cast(FilePart.class)
                                       .flatMap(filePart -> filePart.content()
                                                                    .map(DataBuffer::asInputStream)
                                                                    .doOnNext(is -> storageService.store(filePart.name(), is))
                                                                    .map(this::calculateDigest)
                                                                    .map(digest -> new File(filePart.filename(), digest)))
                                       .flatMap(fileService::save)
                                       .collectList();

        return ServerResponse.ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(list, File.class);
    }

    private String calculateDigest(InputStream inputStream) {
        try {
            return DigestUtils.md5DigestAsHex(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(MD5_CALCULATION_EXCEPTION, e);
        }
    }
}
