package eu.ase.ro.parkingapplication.dto;

import eu.ase.ro.parkingapplication.model.ParkingSpotStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParkingSpotResponse {
    private Long id;
    private String number;
    private String zone;
    private ParkingSpotStatus status;
    private Integer pricePerHour;

    private ReservationResponse activeReservation;
}
