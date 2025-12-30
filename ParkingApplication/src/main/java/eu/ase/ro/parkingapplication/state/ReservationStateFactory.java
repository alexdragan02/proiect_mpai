package eu.ase.ro.parkingapplication.state;

import eu.ase.ro.parkingapplication.model.ReservationStatus;

public final class ReservationStateFactory {

    private ReservationStateFactory() {}

    public static ReservationState fromStatus(ReservationStatus status) {
        return switch (status) {
            case CREATED -> new CreatedState();
            case PAID -> new PaidState();
            case CANCELLED -> new CancelledState();
            case EXPIRED -> new CancelledState();
        };
    }
}
