package eu.ase.ro.parkingapplication.service.facade;

import eu.ase.ro.parkingapplication.event.*;
import eu.ase.ro.parkingapplication.model.*;
import eu.ase.ro.parkingapplication.state.ReservationState;
import eu.ase.ro.parkingapplication.state.ReservationStateFactory;
import eu.ase.ro.parkingapplication.repository.PaymentRepository;
import eu.ase.ro.parkingapplication.service.*;
import eu.ase.ro.parkingapplication.service.payment.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReservationFacade {

    private final ParkingSpotService spotService;
    private final PricingService pricingService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher publisher;
    private final ReceiptService receiptService;

    @Transactional
    public Reservation createReservation(String username, Long spotId, int hours) {
        ParkingSpot spot = spotService.getOrThrow(spotId);

        if (spot.getStatus() != ParkingSpotStatus.AVAILABLE) {
            throw new IllegalStateException("Locul nu este disponibil.");
        }

        int total = pricingService.calculateTotal(spot, hours);

        spot.setStatus(ParkingSpotStatus.RESERVED);
        spotService.save(spot);

        return reservationService.create(username, spot, hours, total);
    }

    @Transactional
    public Payment payReservation(String username, Long reservationId, PaymentMethod method, PayCommand command) {
        Reservation r = reservationService.getOrThrow(reservationId);

        if (!r.getUsername().equals(username)) {
            throw new IllegalStateException("Nu poti plati rezervarea altui utilizator.");
        }

        ReservationState state = ReservationStateFactory.fromStatus(r.getStatus());

        PaymentResult result = paymentService.pay(r, method, command);
        if (result.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Plata a esuat: " + result.getMessage());
        }

        state.pay(r);
        reservationService.save(r);

        ParkingSpot spot = r.getSpot();
        spot.setStatus(ParkingSpotStatus.OCCUPIED);
        spotService.save(spot);

        Payment payment = Payment.builder()
                .reservation(r)
                .amount(r.getTotalPrice())
                .method(method)
                .status(PaymentStatus.SUCCESS)
                .createdAt(Instant.now())
                .reference(result.getReference())
                .build();

        Payment saved = paymentRepository.save(payment);

        receiptService.writeReceipt(saved);

        publisher.publishEvent(new ReservationPaidEvent(r));
        return saved;
    }

    @Transactional
    public Reservation releaseSpot(String username, Long reservationId) {
        Reservation r = reservationService.getOrThrow(reservationId);

        if (!r.getUsername().equals(username)) {
            throw new IllegalStateException("Nu poti elibera locul altui utilizator.");
        }

        ReservationState state = ReservationStateFactory.fromStatus(r.getStatus());
        state.release(r);
        reservationService.save(r);

        ParkingSpot spot = r.getSpot();
        spot.setStatus(ParkingSpotStatus.AVAILABLE);
        spotService.save(spot);

        publisher.publishEvent(new ReservationCancelledEvent(r));
        return r;
    }

    @Transactional
    public Reservation cancelReservation(String username, Long reservationId) {
        Reservation r = reservationService.getOrThrow(reservationId);

        if (!r.getUsername().equals(username)) {
            throw new IllegalStateException("Nu poti anula rezervarea altui utilizator.");
        }

        ReservationState state = ReservationStateFactory.fromStatus(r.getStatus());
        state.cancel(r);
        reservationService.save(r);

        ParkingSpot spot = r.getSpot();
        spot.setStatus(ParkingSpotStatus.AVAILABLE);
        spotService.save(spot);

        publisher.publishEvent(new ReservationCancelledEvent(r));
        return r;
    }
}
