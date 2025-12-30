package eu.ase.ro.parkingapplication.config;

import eu.ase.ro.parkingapplication.model.*;
import eu.ase.ro.parkingapplication.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ParkingSpotRepository spotRepo;

    @Override
    public void run(String... args) {
        if (spotRepo.count() > 0) {
            return;
        }

        IntStream.rangeClosed(1, 24).forEach(i -> {
            String number = "P" + String.format("%03d", i);
            String zone = (i <= 8) ? "Zona A" : (i <= 16) ? "Zona B" : "Zona C";
            int price = (i <= 8) ? 5 : (i <= 16) ? 4 : 3;

            ParkingSpot spot = ParkingSpot.builder()
                    .number(number)
                    .zone(zone)
                    .pricePerHour(price)
                    .status(ParkingSpotStatus.AVAILABLE)
                    .build();

            spotRepo.save(spot);
        });
    }
}
