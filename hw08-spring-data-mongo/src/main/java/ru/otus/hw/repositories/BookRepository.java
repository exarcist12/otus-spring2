package ru.otus.hw.repositories;


import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Book;


public interface BookRepository extends MongoRepository<Book, String> {
    // Все методы уже есть в MongoRepository
    // findAll(), findById(), save(), deleteById() - всё готово!
}