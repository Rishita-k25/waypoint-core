package waypoint.model;

import java.util.Objects;

public final class Spot {
    private final String id;
    private final int level;
    private final int distanceFromGate;
    private final SpotType type;
    private Vehicle occupant;

    public Spot(String id, int level, int distanceFromGate, SpotType type) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.level = level;
        this.distanceFromGate = distanceFromGate;
        this.type = Objects.requireNonNull(type, "type must not be null");
    }

    public String id() { return id; }
    public int level() { return level; }
    public int distanceFromGate() { return distanceFromGate; }
    public SpotType type() { return type; }
    public boolean isOccupied() { return occupant != null; }
    public boolean isFree() { return occupant == null; }
    public Vehicle occupant() { return occupant; }

    public void occupy(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "vehicle must not be null");
        if (this.occupant != null) {
            throw new IllegalStateException("Spot is already occupied: " + id);
        }
        this.occupant = vehicle;
    }

    public void vacate() {
        this.occupant = null;
    }
}