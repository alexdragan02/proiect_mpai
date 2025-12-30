package eu.ase.ro.parkingapplication.dto;

import eu.ase.ro.parkingapplication.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PayReservationRequest {

    @NotNull
    private PaymentMethod method;

    private String cardNumber;
    private String exp;
    private String cvv;
    private String holderName;
}
