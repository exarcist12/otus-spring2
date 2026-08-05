package ru.otus.hw.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("comments")
public class Comment {
    @Id
    private Long id;

    private String text;

    private Long bookId;
}