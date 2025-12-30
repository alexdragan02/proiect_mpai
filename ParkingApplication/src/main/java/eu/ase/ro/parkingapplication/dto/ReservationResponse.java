package eu.ase.ro.parkingapplication.dto;

import eu.ase.ro.parkingapplication.model.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ReservationResponse {
    private Long id;
    private String username;

    private Long spotId;
    private String spotNumber;
    private String zone;

    private Integer hours;
    private Integer totalPrice;

    private ReservationStatus status;
    private Instant createdAt;
}
