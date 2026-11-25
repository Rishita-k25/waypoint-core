package waypoint.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Smallest spot that fits. A compact car may take a large bay
 * ONLY when no compact bay remains.
 */
public final class SizeFitAllocator implements SpotAllocator {

    private final List<Spot> spots;

    public SizeFitAllocator(List<Spot> spots) {
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

        Spot chosen = spots.stream()
                .filter(spot -> !spot.isOccupied())
                .filter(spot -> SpotType.fits(vehicle.size(), spot.type()))
                .min(Comparator
                        .comparingInt((Spot s) -> sizeRank(s.type()))
                        .thenComparingInt(Spot::distanceFromGate))
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

    private static int sizeRank(SpotType spotType) {
        return switch (spotType) {
            case SpotType.Compact()  -> 1;
            case SpotType.Ev(int kw) -> 2;
            case SpotType.Large()    -> 3;
        };
    }
}