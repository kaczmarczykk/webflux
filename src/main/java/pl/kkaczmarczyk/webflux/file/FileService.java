package pl.kkaczmarczyk.webflux.file;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public Mono<File> save(File file) {
        return fileRepository.save(file);
    }
}
