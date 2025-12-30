package eu.ase.ro.parkingapplication.service;

import eu.ase.ro.parkingapplication.model.*;
import eu.ase.ro.parkingapplication.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repo;

    public Reservation create(String username, ParkingSpot spot, int hours, int totalPrice) {
        Reservation r = Reservation.builder()
                .username(username)
                .spot(spot)
                .hours(hours)
                .totalPrice(totalPrice)
                .createdAt(Instant.now())
                .status(ReservationStatus.CREATED)
                .build();
        return repo.save(r);
    }

    public Reservation getOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Rezervare inexistenta: " + id));
    }

    public List<Reservation> myReservations(String username) {
        return repo.findByUsernameOrderByCreatedAtDesc(username);
    }

    public Reservation save(Reservation r) {
        return repo.save(r);
    }


    public Optional<Reservation> activeForSpot(Long spotId) {
        List<Reservation> list = repo.findActiveForSpotFetchSpot(
                spotId,
                List.of(ReservationStatus.CREATED, ReservationStatus.PAID)
        );
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
