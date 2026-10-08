package ru.otus.hw.hotel.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/ui/hotels")
    public String hotelsPage() {
        return "hotels";
    }

    @GetMapping("/ui/hotels/{id}")
    public String hotelPage() {
        return "hotel-details";
    }

    @GetMapping("/ui/login")
    public String loginPage() {
        return "login";
    }
}