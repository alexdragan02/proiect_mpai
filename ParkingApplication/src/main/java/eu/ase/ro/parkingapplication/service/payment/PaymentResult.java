package eu.ase.ro.parkingapplication.service.payment;

import eu.ase.ro.parkingapplication.model.PaymentStatus;
import lombok.*;

@Getter
@AllArgsConstructor
public class PaymentResult {
    private final PaymentStatus status;
    private final String reference;
    private final String message;
}
