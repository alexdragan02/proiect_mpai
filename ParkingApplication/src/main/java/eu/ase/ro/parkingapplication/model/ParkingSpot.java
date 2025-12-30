package eu.ase.ro.parkingapplication.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parking_spots")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private String zone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParkingSpotStatus status;

    @Column(nullable = false)
    private Integer pricePerHour;
}
