package ru.otus.hw.hotel.controllers.ui;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.otus.hw.hotel.dto.CreateRoomRequest;
import ru.otus.hw.hotel.dto.RoomDto;
import ru.otus.hw.hotel.dto.UpdateRoomRequest;
import ru.otus.hw.hotel.enums.RoomType;
import ru.otus.hw.hotel.services.HotelService;
import ru.otus.hw.hotel.services.RoomService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/ui/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UiAdminRoomController {

    private final RoomService roomService;
    private final HotelService hotelService;

    /** Список комнат отеля */
    @GetMapping
    public String list(@PathVariable Long hotelId, Model model) {
        model.addAttribute("hotel", hotelService.findById(hotelId));
        model.addAttribute("rooms", roomService.findByHotelId(hotelId));
        model.addAttribute("hotelId", hotelId);
        return "admin/rooms-list";
    }

    /** Форма создания комнаты */
    @GetMapping("/new")
    public String createForm(@PathVariable Long hotelId, Model model) {
        CreateRoomRequest form = new CreateRoomRequest();
        form.setHotelId(hotelId);

        model.addAttribute("form", form);
        model.addAttribute("hotel", hotelService.findById(hotelId));
        model.addAttribute("hotelId", hotelId);
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("editMode", false);
        return "admin/room-form";
    }

    /** Приём формы создания */
    @PostMapping
    public String create(@PathVariable Long hotelId,
                         @Valid @ModelAttribute("form") CreateRoomRequest form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {

        if (binding.hasErrors()) {
            model.addAttribute("hotel", hotelService.findById(hotelId));
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("roomTypes", RoomType.values());
            model.addAttribute("editMode", false);
            model.addAttribute("error", formatErrors(binding));
            return "admin/room-form";
        }

        roomService.create(form);
        ra.addFlashAttribute("success", "Комната добавлена");
        return "redirect:/ui/admin/hotels/" + hotelId + "/rooms";
    }

    /** Форма редактирования */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long hotelId,
                           @PathVariable Long id,
                           Model model) {
        RoomDto room = roomService.findById(id);

        UpdateRoomRequest form = new UpdateRoomRequest();
        form.setType(room.getType());
        form.setPricePerNight(room.getPricePerNight());
        form.setCapacity(room.getCapacity());
        form.setDescription(room.getDescription());

        model.addAttribute("form", form);
        model.addAttribute("room", room);
        model.addAttribute("roomId", id);
        model.addAttribute("hotel", hotelService.findById(hotelId));
        model.addAttribute("hotelId", hotelId);
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("editMode", true);
        return "admin/room-form";
    }

    /** Приём формы редактирования */
    @PostMapping("/{id}")
    public String update(@PathVariable Long hotelId,
                         @PathVariable Long id,
                         @Valid @ModelAttribute("form") UpdateRoomRequest form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {

        if (binding.hasErrors()) {
            model.addAttribute("roomId", id);
            model.addAttribute("hotel", hotelService.findById(hotelId));
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("roomTypes", RoomType.values());
            model.addAttribute("editMode", true);
            model.addAttribute("error", formatErrors(binding));
            return "admin/room-form";
        }

        roomService.update(id, form);
        ra.addFlashAttribute("success", "Комната обновлена");
        return "redirect:/ui/admin/hotels/" + hotelId + "/rooms";
    }

    /** Удаление */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long hotelId,
                         @PathVariable Long id,
                         RedirectAttributes ra) {
        roomService.deleteById(id);
        ra.addFlashAttribute("success", "Комната удалена");
        return "redirect:/ui/admin/hotels/" + hotelId + "/rooms";
    }

    private String formatErrors(BindingResult binding) {
        return binding.getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Ошибка валидации");
    }
}