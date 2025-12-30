package eu.ase.ro.parkingapplication.repository;

import eu.ase.ro.parkingapplication.model.ParkingSpot;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long>, JpaSpecificationExecutor<ParkingSpot> {
    Optional<ParkingSpot> findByNumber(String number);
}
