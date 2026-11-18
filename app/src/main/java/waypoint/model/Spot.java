package waypoint.model;

import java.util.Objects;

/** ENTITY. Identity = id. Invariant: a spot cannot be assigned while occupied. */
public final class Spot {
    private final SpotId id;
    private VehicleId occupant;   // the ONLY mutable field

    public Spot(SpotId id) {
        this.id = Objects.requireNonNull(id, "SpotId must not be null");
    }

    public SpotId id() {
        return id;
    }

    public boolean tryAssign(VehicleId v) {
        Objects.requireNonNull(v, "VehicleId must not be null");
        if (this.occupant != null) {
            return false;
        }
        this.occupant = v;
        return true;
    }

    public void release(VehicleId v) {
        Objects.requireNonNull(v, "VehicleId must not be null");
        if (v.equals(this.occupant)) {
            this.occupant = null;
        }
    }

    public boolean isFree() {
        return this.occupant == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Spot other)) return false;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}