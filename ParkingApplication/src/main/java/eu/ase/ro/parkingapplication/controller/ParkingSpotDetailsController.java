package eu.ase.ro.parkingapplication.controller;

import eu.ase.ro.parkingapplication.dto.*;
import eu.ase.ro.parkingapplication.mapper.ApiMapper;
import eu.ase.ro.parkingapplication.model.ParkingSpot;
import eu.ase.ro.parkingapplication.service.ParkingSpotService;
import eu.ase.ro.parkingapplication.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class ParkingSpotDetailsController {

    private final ParkingSpotService spotService;
    private final ReservationService reservationService;
    private final ApiMapper mapper;

    @GetMapping("/{id}")
    public ParkingSpotDetailsResponse details(@PathVariable Long id) {
        ParkingSpot spot = spotService.getOrThrow(id);

        var active = reservationService.activeForSpot(id).map(mapper::toReservation).orElse(null);

        return ParkingSpotDetailsResponse.builder()
                .id(spot.getId())
                .number(spot.getNumber())
                .zone(spot.getZone())
                .status(spot.getStatus())
                .pricePerHour(spot.getPricePerHour())
                .activeReservation(active)
                .build();
    }
}
