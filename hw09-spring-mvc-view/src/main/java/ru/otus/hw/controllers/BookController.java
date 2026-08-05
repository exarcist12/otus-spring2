package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;

    @GetMapping({"/", "/books"})
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("bookCreateDto", new BookCreateDto());
        return "index";
    }

    @PostMapping("/books")
    public String createBook(@Valid @ModelAttribute("bookCreateDto") BookCreateDto createDto,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("books", bookService.findAll());
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "index";
        }
        bookService.insert(createDto);
        return "redirect:/books";
    }

    @GetMapping("/books/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        BookDto book = bookService.findById(id);
        List<Comment> comments = commentService.findByBookId(id);
        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        return "book-details";
    }

    @PostMapping("/books/{id}")
    public String updateBook(@PathVariable Long id,
                             @Valid @ModelAttribute("bookUpdateDto") BookUpdateDto updateDto,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("books", bookService.findAll());
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "index";
        }
        bookService.update(id, updateDto);
        return "redirect:/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }
}