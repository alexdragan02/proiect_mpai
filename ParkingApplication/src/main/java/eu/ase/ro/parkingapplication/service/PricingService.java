package eu.ase.ro.parkingapplication.service;

import eu.ase.ro.parkingapplication.model.ParkingSpot;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

    public int calculateTotal(ParkingSpot spot, int hours) {
        if (hours <= 0 || hours > 24) {
            throw new IllegalArgumentException("orele trebuie sa fie intre 1 si 24.");
        }
        return spot.getPricePerHour() * hours;
    }
}
