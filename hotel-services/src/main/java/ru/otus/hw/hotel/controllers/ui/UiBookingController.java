package ru.otus.hw.hotel.controllers.ui;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.otus.hw.hotel.dto.BookingDto;
import ru.otus.hw.hotel.dto.CreateBookingRequest;
import ru.otus.hw.hotel.enums.BookingStatus;
import ru.otus.hw.hotel.exceptions.RoomNotAvailableException;
import ru.otus.hw.hotel.models.User;
import ru.otus.hw.hotel.services.BookingService;
import ru.otus.hw.hotel.services.RoomService;

import java.util.List;

@Controller
@RequestMapping("/ui/bookings")
@RequiredArgsConstructor
public class UiBookingController {

    private final BookingService bookingService;
    private final RoomService roomService;

    /** Список моих броней */
    @GetMapping("/my")
    public String myBookings(Authentication auth, Model model) {
        User u = (User) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<BookingDto> bookings = isAdmin
                ? bookingService.findAll()
                : bookingService.findByUserId(u.getId());

        model.addAttribute("bookings", bookings);
        model.addAttribute("isAdminView", isAdmin);
        return "bookings/list";
    }

    /** Форма создания брони на конкретную комнату */
    @GetMapping("/new")
    public String createForm(@RequestParam Long roomId, Model model) {
        CreateBookingRequest form = new CreateBookingRequest();
        form.setRoomId(roomId);

        model.addAttribute("room", roomService.findById(roomId));
        model.addAttribute("bookingForm", form);
        return "bookings/form";
    }

    /** Приём формы: создаём бронь + сразу оплачиваем (paymentClient внутри) */
    @PostMapping
    public String create(@Valid @ModelAttribute("bookingForm") CreateBookingRequest form,
                         BindingResult binding,
                         Authentication auth,
                         Model model,
                         RedirectAttributes ra) {

        if (binding.hasErrors()) {
            String msg = binding.getFieldErrors().stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("Ошибка валидации");
            ra.addFlashAttribute("error", msg);
            return "redirect:/ui/bookings/new?roomId=" + form.getRoomId();
        }

        User u = (User) auth.getPrincipal();

        try {
            BookingDto result = bookingService.create(form, u.getId());

            if (result.getStatus() != BookingStatus.PAID) {
                ra.addFlashAttribute("error",
                        "Платёж не прошёл (" + result.getStatus() + "). Попробуйте позже.");
                return "redirect:/ui/bookings/new?roomId=" + form.getRoomId();
            }

            ra.addFlashAttribute("success", "Бронь создана и оплачена!");
            return "redirect:/ui/bookings/my";
        } catch (RoomNotAvailableException e) {
            ra.addFlashAttribute("error", "Номер недоступен на выбранные даты");
            return "redirect:/ui/bookings/new?roomId=" + form.getRoomId();
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/ui/bookings/new?roomId=" + form.getRoomId();
        }
    }

    /** Отмена своей брони */
    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        User u = (User) auth.getPrincipal();
        try {
            bookingService.cancel(id, u.getId());
            ra.addFlashAttribute("success", "Бронь отменена");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/bookings/my";
    }
}