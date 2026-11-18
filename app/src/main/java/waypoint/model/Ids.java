package waypoint.model;

import java.util.Objects;

/** Distinct id types so they can never be swapped at a call site. */
record TicketId(String value) {
    TicketId {
        Objects.requireNonNull(value, "TicketId must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("TicketId must not be blank");
        }
    }
}

record SpotId(String value) {
    SpotId {
        Objects.requireNonNull(value, "SpotId must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("SpotId must not be blank");
        }
    }
}

record VehicleId(String value) {
    VehicleId {
        Objects.requireNonNull(value, "VehicleId must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("VehicleId must not be blank");
        }
    }
}