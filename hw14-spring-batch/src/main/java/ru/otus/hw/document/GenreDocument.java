package ru.otus.hw.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "genres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreDocument {
    @Id
    private String id;

    private String name;

    public GenreDocument(String name) {
        this.name = name;
    }
}