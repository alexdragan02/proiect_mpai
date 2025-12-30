package eu.ase.ro.parkingapplication.event;

import eu.ase.ro.parkingapplication.model.Reservation;
import lombok.Getter;

@Getter
public class ReservationPaidEvent {
    private final Reservation reservation;

    public ReservationPaidEvent(Reservation reservation) {
        this.reservation = reservation;
    }
}
