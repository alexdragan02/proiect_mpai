package eu.ase.ro.parkingapplication.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateReservationRequest {
    @NotNull
    private Long spotId;

    @Min(1)
    @Max(24)
    private int hours;
}
