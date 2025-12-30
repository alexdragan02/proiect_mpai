package eu.ase.ro.parkingapplication.service.payment;

import eu.ase.ro.parkingapplication.model.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod supports() {
        return PaymentMethod.CARD;
    }

    @Override
    public PaymentResult pay(Reservation reservation, PayCommand command) {
        if (command == null || command.getCardNumber() == null || command.getCardNumber().isBlank()) {
            return new PaymentResult(PaymentStatus.FAILED, "N/A", "Card number lipsa.");
        }
        String ref = "CARD-" + UUID.randomUUID();
        return new PaymentResult(PaymentStatus.SUCCESS, ref, "Plata card simulata cu succes.");
    }
}
