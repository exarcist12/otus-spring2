package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "genres")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Genre {
    @Id
    private String id;

    private String name;

    @Override
    public String toString() {
        return "Genre{id='" + id + "', name='" + name + "'}";
    }
}