package eu.ase.ro.parkingapplication.service.payment;

import eu.ase.ro.parkingapplication.model.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MockPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod supports() {
        return PaymentMethod.MOCK;
    }

    @Override
    public PaymentResult pay(Reservation reservation, PayCommand command) {
        String ref = "MOCK-" + UUID.randomUUID();
        return new PaymentResult(PaymentStatus.SUCCESS, ref, "Plata mock OK.");
    }
}
