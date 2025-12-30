package eu.ase.ro.parkingapplication.repository;

import eu.ase.ro.parkingapplication.model.Reservation;
import eu.ase.ro.parkingapplication.model.ReservationStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUsernameOrderByCreatedAtDesc(String username);


    @Query("""
        select r
        from Reservation r
        join fetch r.spot s
        where s.id = :spotId
          and r.status in :statuses
        order by r.createdAt desc
    """)
    List<Reservation> findActiveForSpotFetchSpot(
            @Param("spotId") Long spotId,
            @Param("statuses") List<ReservationStatus> statuses
    );
}
