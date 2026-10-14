package ru.otus.hw.hotel.controllers.ui;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.otus.hw.hotel.dto.CreateReviewRequest;
import ru.otus.hw.hotel.dto.HotelDto;
import ru.otus.hw.hotel.dto.HotelShortDto;
import ru.otus.hw.hotel.models.User;
import ru.otus.hw.hotel.services.CityService;
import ru.otus.hw.hotel.services.HotelService;
import ru.otus.hw.hotel.services.ReviewService;
import ru.otus.hw.hotel.services.RoomService;

import java.util.List;

@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class UiHotelController {

    private final HotelService hotelService;
    private final CityService cityService;
    private final RoomService roomService;
    private final ReviewService reviewService;

    /**
     * Список отелей с фильтром по городу.
     * GET /ui/hotels
     * GET /ui/hotels?cityId=1
     */
    @GetMapping("/hotels")
    public String list(@RequestParam(required = false) Long cityId, Model model) {
        List<HotelShortDto> hotels = (cityId != null)
                ? hotelService.findByCityId(cityId)
                : hotelService.findAll();

        model.addAttribute("hotels", hotels);
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("selectedCityId", cityId);
        return "hotels";
    }

    /**
     * Карточка отеля: описание, комнаты, отзывы, форма отзыва.
     * GET /ui/hotels/{id}
     */
    @GetMapping("/hotels/{id}")
    public String view(@PathVariable Long id, Model model, Authentication auth) {
        HotelDto hotel = hotelService.findById(id);

        CreateReviewRequest reviewForm = new CreateReviewRequest();
        reviewForm.setHotelId(id);
        model.addAttribute("reviewForm", reviewForm);

        model.addAttribute("hotel", hotel);
        model.addAttribute("rooms", roomService.findByHotelId(id));
        model.addAttribute("reviews", reviewService.findByHotelId(id));

        boolean canReview = false;
        if (auth != null && auth.getPrincipal() instanceof User u) {
            canReview = !reviewService.hasUserReviewedHotel(u.getId(), id);
        }
        model.addAttribute("canReview", canReview);

        return "hotel-details";
    }

    /**
     * Приём формы отзыва.
     * POST /ui/hotels/{id}/reviews
     */
    @PostMapping("/hotels/{id}/reviews")
    public String addReview(@PathVariable Long id,
                            @Valid @ModelAttribute("reviewForm") CreateReviewRequest form,
                            BindingResult binding,
                            Authentication auth,
                            RedirectAttributes ra) {

        if (binding.hasErrors()) {
            ra.addFlashAttribute("error", "Проверьте форму отзыва");
            return "redirect:/ui/hotels/" + id;
        }

        form.setHotelId(id);

        if (auth == null || !(auth.getPrincipal() instanceof User u)) {
            ra.addFlashAttribute("error", "Нужно авторизоваться");
            return "redirect:/ui/hotels/" + id;
        }

        try {
            reviewService.create(form, u.getId());
            ra.addFlashAttribute("success", "Спасибо за отзыв!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/ui/hotels/" + id;
    }
}