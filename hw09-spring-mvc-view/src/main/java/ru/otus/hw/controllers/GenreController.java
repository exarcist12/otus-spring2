package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;

@Controller
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public String listGenres(Model model) {
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("genre", new Genre());
        return "genres";
    }

    @PostMapping
    public String addGenre(@ModelAttribute Genre genre) {
        genreService.insert(genre);
        return "redirect:/genres";
    }

    @PostMapping("/{id}/delete")
    public String deleteGenre(@PathVariable Long id) {
        genreService.deleteById(id);
        return "redirect:/genres";
    }
}