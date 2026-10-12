package ru.otus.hw.hotel.controllers.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UiHomeController {

    @GetMapping("/")
    public String index() {
        return "redirect:/ui/hotels";
    }
}