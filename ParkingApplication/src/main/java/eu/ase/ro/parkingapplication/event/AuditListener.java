package eu.ase.ro.parkingapplication.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditListener {

    @EventListener
    public void onPaid(ReservationPaidEvent e) {
        log.info("[AUDIT] Reservation {} PAID by user {} (spot {})",
                e.getReservation().getId(),
                e.getReservation().getUsername(),
                e.getReservation().getSpot().getNumber());
    }

    @EventListener
    public void onCancelled(ReservationCancelledEvent e) {
        log.info("[AUDIT] Reservation {} CANCELLED by user {} (spot {})",
                e.getReservation().getId(),
                e.getReservation().getUsername(),
                e.getReservation().getSpot().getNumber());
    }
}
