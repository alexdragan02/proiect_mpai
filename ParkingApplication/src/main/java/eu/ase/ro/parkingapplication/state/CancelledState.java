package eu.ase.ro.parkingapplication.state;

import eu.ase.ro.parkingapplication.model.Reservation;

public class CancelledState implements ReservationState {

    @Override
    public void pay(Reservation reservation) {
        throw new IllegalStateException("Rezervarea anulata nu poate fi platita.");
    }

    @Override
    public void cancel(Reservation reservation) {
        throw new IllegalStateException("Rezervarea este deja anulata.");
    }

    @Override
    public void release(Reservation reservation) {
        throw new IllegalStateException("Rezervarea anulata nu poate fi eliberata.");
    }

    @Override
    public String name() {
        return "CANCELLED";
    }
}
