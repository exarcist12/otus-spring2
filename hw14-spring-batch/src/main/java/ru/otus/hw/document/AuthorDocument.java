package ru.otus.hw.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "authors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDocument {
    @Id
    private String id;

    @Field("full_name")
    private String fullName;

    public AuthorDocument(String fullName) {
        this.fullName = fullName;
    }
}