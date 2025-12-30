package eu.ase.ro.parkingapplication.state;

import eu.ase.ro.parkingapplication.model.Reservation;

public interface ReservationState {
    void pay(Reservation reservation);
    void cancel(Reservation reservation);
    void release(Reservation reservation);
    String name();
}
