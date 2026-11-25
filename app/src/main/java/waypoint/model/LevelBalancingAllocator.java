package waypoint.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** Prefers the least-occupied level, then nearest within it. */
public final class LevelBalancingAllocator implements SpotAllocator {

    private final List<Spot> spots;

    public LevelBalancingAllocator(List<Spot> spots) {
        Objects.requireNonNull(spots, "spots must not be null");
        this.spots = new ArrayList<>(spots);
    }

    @Override
    public Spot allocate(Vehicle vehicle) throws NoSpotAvailable {
        Objects.requireNonNull(vehicle, "vehicle must not be null");

        for (Spot spot : spots) {
            if (spot.isOccupied() && vehicle.equals(spot.occupant())) {
                throw new IllegalStateException("Vehicle is already allocated a spot: " + vehicle);
            }
        }

        Map<Integer, Long> occupiedPerLevel = spots.stream()
                .filter(Spot::isOccupied)
                .collect(Collectors.groupingBy(Spot::level, Collectors.counting()));

        Spot chosen = spots.stream()
                .filter(spot -> !spot.isOccupied())
                .filter(spot -> SpotType.fits(vehicle.size(), spot.type()))
                .min(Comparator
                        .comparingLong((Spot s) -> occupiedPerLevel.getOrDefault(s.level(), 0L))
                        .thenComparingInt(Spot::distanceFromGate)
                        .thenComparingInt(Spot::level))
                .orElseThrow(() -> new NoSpotAvailable("No compatible spot available for: " + vehicle));

        chosen.occupy(vehicle);
        return chosen;
    }

    @Override
    public void release(Spot spot) {
        Objects.requireNonNull(spot, "spot must not be null");

        if (!spots.contains(spot)) {
            throw new IllegalArgumentException("Spot is not managed by this allocator: " + spot);
        }

        if (spot.isOccupied()) {
            spot.vacate();
        }
    }
}