package eu.ase.ro.parkingapplication.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "reservations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String username;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ParkingSpot spot;


    @Column(nullable = false)
    private Integer hours;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;
}
