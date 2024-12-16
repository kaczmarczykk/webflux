package pl.kkaczmarczyk.webflux.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
@AllArgsConstructor
public class File {

    @Id
    private Long id;
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String digest;

    public File(String name, String digest) {
        this.name = name;
        this.digest = digest;
    }
}