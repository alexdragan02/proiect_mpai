package eu.ase.ro.parkingapplication.specification;

import eu.ase.ro.parkingapplication.model.*;
import org.springframework.data.jpa.domain.Specification;

public final class ParkingSpotSpecifications {

    private ParkingSpotSpecifications() {}

    public static Specification<ParkingSpot> hasZone(String zone) {
        return (root, query, cb) -> zone == null || zone.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("zone"), zone);
    }

    public static Specification<ParkingSpot> hasStatus(ParkingSpotStatus status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }

    public static Specification<ParkingSpot> maxPrice(Integer maxPrice) {
        return (root, query, cb) -> maxPrice == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("pricePerHour"), maxPrice);
    }

    public static Specification<ParkingSpot> minPrice(Integer minPrice) {
        return (root, query, cb) -> minPrice == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("pricePerHour"), minPrice);
    }
}
