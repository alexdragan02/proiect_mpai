package eu.ase.ro.parkingapplication.service.payment;

import eu.ase.ro.parkingapplication.model.*;

public interface PaymentStrategy {
    PaymentMethod supports();
    PaymentResult pay(Reservation reservation, PayCommand command);
}
