package eu.ase.ro.parkingapplication.controller;

import eu.ase.ro.parkingapplication.model.ParkingSpotStatus;
import eu.ase.ro.parkingapplication.dto.ParkingSpotResponse;
import eu.ase.ro.parkingapplication.mapper.ApiMapper;
import eu.ase.ro.parkingapplication.service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotService service;
    private final ApiMapper mapper;


    @GetMapping
    public List<ParkingSpotResponse> search(
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) ParkingSpotStatus status,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice
    ) {
        return service.search(zone, status, minPrice, maxPrice).stream()
                .map(mapper::toSpot)
                .toList();
    }
}
