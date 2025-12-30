package eu.ase.ro.parkingapplication.service.payment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayCommand {
    private String cardNumber;
    private String exp;
    private String cvv;
    private String holderName;
}
