package eu.ase.ro.parkingapplication.controller;

import eu.ase.ro.parkingapplication.model.Payment;
import eu.ase.ro.parkingapplication.dto.*;
import eu.ase.ro.parkingapplication.mapper.ApiMapper;
import eu.ase.ro.parkingapplication.repository.PaymentRepository;
import eu.ase.ro.parkingapplication.service.ReservationService;
import eu.ase.ro.parkingapplication.service.facade.ReservationFacade;
import eu.ase.ro.parkingapplication.service.payment.PayCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationFacade facade;
    private final ReservationService reservationService;
    private final ApiMapper mapper;

    private final PaymentRepository paymentRepository;

    @PostMapping
    public ReservationResponse create(@Valid @RequestBody CreateReservationRequest req, Authentication auth) {
        String username = auth.getName();
        return mapper.toReservation(facade.createReservation(username, req.getSpotId(), req.getHours()));
    }

    @GetMapping("/me")
    public List<ReservationResponse> mine(Authentication auth) {
        return reservationService.myReservations(auth.getName()).stream()
                .map(mapper::toReservation)
                .toList();
    }

    @GetMapping("/spot/{spotId}")
    public ReservationResponse activeBySpot(@PathVariable Long spotId) {
        return reservationService.activeForSpot(spotId)
                .map(mapper::toReservation)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nu exista rezervare activa pentru spotId=" + spotId));
    }

    @GetMapping("/{id}/payment")
    public PaymentResponse paymentForReservation(@PathVariable Long id) {
        Payment p = paymentRepository.findFirstByReservationIdOrderByCreatedAtDesc(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nu exista plata pentru reservationId=" + id));

        return mapper.toPayment(p);
    }

    @PostMapping("/{id}/pay")
    public PaymentResponse pay(@PathVariable Long id,
                               @Valid @RequestBody PayReservationRequest req,
                               Authentication auth) {

        PayCommand cmd = PayCommand.builder()
                .cardNumber(req.getCardNumber())
                .exp(req.getExp())
                .cvv(req.getCvv())
                .holderName(req.getHolderName())
                .build();

        Payment p = facade.payReservation(auth.getName(), id, req.getMethod(), cmd);
        return mapper.toPayment(p);
    }

    @PostMapping("/{id}/cancel")
    public ReservationResponse cancel(@PathVariable Long id, Authentication auth) {
        return mapper.toReservation(facade.cancelReservation(auth.getName(), id));
    }

    @PostMapping("/{id}/release")
    public ReservationResponse release(@PathVariable Long id, Authentication auth) {
        return mapper.toReservation(facade.releaseSpot(auth.getName(), id));
    }
}
