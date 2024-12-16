package pl.kkaczmarczyk.webflux.file;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends R2dbcRepository<File, Long> {
}
