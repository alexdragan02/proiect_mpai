package eu.ase.ro.parkingapplication.state;

import eu.ase.ro.parkingapplication.model.Reservation;
import eu.ase.ro.parkingapplication.model.ReservationStatus;

public class PaidState implements ReservationState {

    @Override
    public void pay(Reservation reservation) {
        throw new IllegalStateException("Rezervarea este deja platita.");
    }

    @Override
    public void cancel(Reservation reservation) {
        throw new IllegalStateException("Nu poti anula o rezervare platita. Foloseste Elibereaza.");
    }

    @Override
    public void release(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    @Override
    public String name() {
        return "PAID";
    }
}
