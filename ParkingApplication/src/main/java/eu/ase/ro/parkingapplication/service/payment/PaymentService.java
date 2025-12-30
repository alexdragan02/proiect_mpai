package eu.ase.ro.parkingapplication.service.payment;

import eu.ase.ro.parkingapplication.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final List<PaymentStrategy> strategies;

    public PaymentResult pay(Reservation reservation, PaymentMethod method, PayCommand command) {
        PaymentStrategy strategy = strategies.stream()
                .filter(s -> s.supports() == method)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Metoda de plata nesuportata: " + method));

        return strategy.pay(reservation, command);
    }
}
