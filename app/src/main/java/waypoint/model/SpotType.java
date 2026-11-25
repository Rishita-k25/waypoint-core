package waypoint.model;

import java.util.Objects;

/** Sealed SpotType with exhaustive pattern-matching switch and NO default branch. */
public sealed interface SpotType permits SpotType.Compact, SpotType.Large, SpotType.Ev {

    record Compact() implements SpotType {}
    record Large()   implements SpotType {}
    record Ev(int kw) implements SpotType {
        public Ev {
            if (kw <= 0) {
                throw new IllegalArgumentException("Charging power (kw) must be positive: " + kw);
            }
        }
    }

    static boolean fits(VehicleSize size, SpotType spot) {
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(spot, "spot must not be null");

        return switch (spot) {
            case Compact()  -> size == VehicleSize.COMPACT || size == VehicleSize.MOTORCYCLE;
            case Large()    -> true;
            case Ev(int kw) -> size != VehicleSize.TRUCK && size != VehicleSize.LARGE;
        };
    }
}