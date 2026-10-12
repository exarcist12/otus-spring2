package ru.otus.hw.hotel.controllers.ui;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.otus.hw.hotel.dto.CreateHotelRequest;
import ru.otus.hw.hotel.dto.HotelDto;
import ru.otus.hw.hotel.dto.UpdateHotelRequest;
import ru.otus.hw.hotel.services.AmenityService;
import ru.otus.hw.hotel.services.CityService;
import ru.otus.hw.hotel.services.HotelService;

@Controller
@RequestMapping("/ui/admin/hotels")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UiAdminHotelController {

    private final HotelService hotelService;
    private final CityService cityService;
    private final AmenityService amenityService;

    /** Список отелей в админке */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("hotels", hotelService.findAll());
        return "admin/hotels-list";
    }

    /** Форма создания */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new CreateHotelRequest());
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("amenities", amenityService.findAll());
        model.addAttribute("editMode", false);
        return "admin/hotel-form";
    }

    /** Приём формы создания */
    @PostMapping
    public String create(@Valid @ModelAttribute("form") CreateHotelRequest form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {

        if (binding.hasErrors()) {
            model.addAttribute("cities", cityService.findAll());
            model.addAttribute("amenities", amenityService.findAll());
            model.addAttribute("editMode", false);
            model.addAttribute("error", formatErrors(binding));
            return "admin/hotel-form";
        }

        hotelService.create(form);
        ra.addFlashAttribute("success", "Отель создан");
        return "redirect:/ui/admin/hotels";
    }

    /** Форма редактирования */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        HotelDto hotel = hotelService.findById(id);

        UpdateHotelRequest form = new UpdateHotelRequest();
        form.setName(hotel.getName());
        form.setDescription(hotel.getDescription());
        form.setAddress(hotel.getAddress());
        form.setStars(hotel.getStars());
        form.setCityId(hotel.getCity() != null ? hotel.getCity().getId() : null);
        form.setAmenityIds(hotel.getAmenities().stream()
                .map(a -> a.getId())
                .collect(java.util.stream.Collectors.toSet()));

        model.addAttribute("form", form);
        model.addAttribute("hotelId", id);
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("amenities", amenityService.findAll());
        model.addAttribute("editMode", true);
        return "admin/hotel-form";
    }

    /** Приём формы редактирования */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") UpdateHotelRequest form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {

        if (binding.hasErrors()) {
            model.addAttribute("hotelId", id);
            model.addAttribute("cities", cityService.findAll());
            model.addAttribute("amenities", amenityService.findAll());
            model.addAttribute("editMode", true);
            model.addAttribute("error", formatErrors(binding));
            return "admin/hotel-form";
        }

        hotelService.update(id, form);
        ra.addFlashAttribute("success", "Отель обновлён");
        return "redirect:/ui/admin/hotels";
    }

    /** Удаление */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        hotelService.deleteById(id);
        ra.addFlashAttribute("success", "Отель удалён");
        return "redirect:/ui/admin/hotels";
    }

    /** Хелпер: собирает читаемое сообщение об ошибках валидации */
    private String formatErrors(BindingResult binding) {
        return binding.getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Ошибка валидации");
    }
}