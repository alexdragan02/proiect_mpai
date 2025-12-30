package eu.ase.ro.parkingapplication.state;

import eu.ase.ro.parkingapplication.model.Reservation;
import eu.ase.ro.parkingapplication.model.ReservationStatus;

public class CreatedState implements ReservationState {

    @Override
    public void pay(Reservation reservation) {
        reservation.setStatus(ReservationStatus.PAID);
    }

    @Override
    public void cancel(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    @Override
    public void release(Reservation reservation) {
        throw new IllegalStateException("Nu poti elibera un loc neplatit.");
    }

    @Override
    public String name() {
        return "CREATED";
    }
}
