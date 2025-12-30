package eu.ase.ro.parkingapplication.dto;

import eu.ase.ro.parkingapplication.model.*;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class PaymentResponse {
    private Long id;
    private Long reservationId;
    private Integer amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private Instant createdAt;
    private String reference;
}
