package ru.otus.hw.hotel.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.hotel.enums.RoomType;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RoomType type;

    @Column(name = "price_per_night", nullable = false)
    private BigDecimal pricePerNight;

    @Column(nullable = false)
    private Integer capacity;

    @Column(columnDefinition = "TEXT")
    private String description;
}