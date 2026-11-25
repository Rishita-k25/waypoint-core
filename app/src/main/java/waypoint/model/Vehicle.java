package waypoint.model;

import java.util.Objects;

public record Vehicle(String licensePlate, VehicleSize size) {
    public Vehicle {
        Objects.requireNonNull(licensePlate, "licensePlate must not be null");
        Objects.requireNonNull(size, "size must not be null");
    }
}