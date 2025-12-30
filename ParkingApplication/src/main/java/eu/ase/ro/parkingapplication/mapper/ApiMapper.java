package eu.ase.ro.parkingapplication.mapper;

import eu.ase.ro.parkingapplication.model.*;
import eu.ase.ro.parkingapplication.dto.*;
import org.springframework.stereotype.Component;

@Component
public class ApiMapper {

    public ParkingSpotResponse toSpot(ParkingSpot s) {
        return ParkingSpotResponse.builder()
                .id(s.getId())
                .number(s.getNumber())
                .zone(s.getZone())
                .status(s.getStatus())
                .pricePerHour(s.getPricePerHour())
                .build();
    }

    public ReservationResponse toReservation(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .username(r.getUsername())
                .spotId(r.getSpot().getId())
                .spotNumber(r.getSpot().getNumber())
                .zone(r.getSpot().getZone())
                .hours(r.getHours())
                .totalPrice(r.getTotalPrice())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }

    public PaymentResponse toPayment(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .reservationId(p.getReservation().getId())
                .amount(p.getAmount())
                .method(p.getMethod())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .reference(p.getReference())
                .build();
    }
}
