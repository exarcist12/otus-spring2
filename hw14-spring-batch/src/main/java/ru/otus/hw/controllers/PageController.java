package ru.otus.hw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String redirectToBooks() {
        return "redirect:/books";
    }

    @GetMapping("/books")
    public String listBooksPage() {
        return "index";
    }

    @GetMapping("/books/{id}")
    public String viewBookPage() {
        return "book-details";
    }

    @GetMapping("/authors")
    public String authorsPage() {
        return "authors";
    }

    @GetMapping("/genres")
    public String genresPage() {
        return "genres";
    }
}