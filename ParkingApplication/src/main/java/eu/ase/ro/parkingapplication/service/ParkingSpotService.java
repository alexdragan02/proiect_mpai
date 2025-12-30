package eu.ase.ro.parkingapplication.service;

import eu.ase.ro.parkingapplication.model.*;
import eu.ase.ro.parkingapplication.specification.ParkingSpotSpecifications;
import eu.ase.ro.parkingapplication.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository repo;

    public List<ParkingSpot> search(String zone, ParkingSpotStatus status, Integer minPrice, Integer maxPrice) {
        Specification<ParkingSpot> spec = Specification
                .where(ParkingSpotSpecifications.hasZone(zone))
                .and(ParkingSpotSpecifications.hasStatus(status))
                .and(ParkingSpotSpecifications.minPrice(minPrice))
                .and(ParkingSpotSpecifications.maxPrice(maxPrice));

        return repo.findAll(spec);
    }

    public ParkingSpot getOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Spot inexistent: " + id));
    }

    public ParkingSpot save(ParkingSpot spot) {
        return repo.save(spot);
    }
}
