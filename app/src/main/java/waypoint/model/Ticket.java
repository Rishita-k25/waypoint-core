package waypoint.model;

import java.time.Instant;
import java.util.Objects;

/** ENTITY. Invariants:
-   I1  entry is always present
-   I2  exit is null, or not before entry
-   I3  fare is null, or not negative
-   I4  fare exists only if exit exists
-   I5  a closed ticket never reopens
 */
public final class Ticket {
    private final TicketId id;
    private final SpotId spotId;
    private final Instant entry;
    private Instant exit;
    private Money fare;

    private Ticket(TicketId id, SpotId spotId, Instant entry) {
        this.id = Objects.requireNonNull(id, "TicketId must not be null");
        this.spotId = Objects.requireNonNull(spotId, "SpotId must not be null");
        this.entry = Objects.requireNonNull(entry, "Entry instant must not be null");
    }

    public static Ticket open(TicketId id, SpotId spotId, Instant entry) {
        return new Ticket(id, spotId, entry);
    }

    public TicketId id() {
        return id;
    }

    public SpotId spotId() {
        return spotId;
    }

    public Instant entry() {
        return entry;
    }

    public Instant exit() {
        return exit;
    }

    public Money fare() {
        return fare;
    }

    public boolean isOpen() {
        return exit == null;
    }

    public Money close(Instant at, FarePolicy policy) {
        Objects.requireNonNull(at, "Exit instant must not be null");
        Objects.requireNonNull(policy, "FarePolicy must not be null");

        if (!isOpen()) {
            throw new TicketAlreadyClosed("Ticket is already closed: " + id);
        }
        if (at.isBefore(entry)) {
            throw new IllegalArgumentException("Exit time cannot be before entry time");
        }

        Money computed = policy.fareFor(entry, at, spotId);
        if (computed == null || computed.isNegative()) {
            throw new IllegalArgumentException("Computed fare must be non-null and non-negative");
        }

        this.exit = at;
        this.fare = computed;
        return computed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket other)) return false;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

class TicketAlreadyClosed extends RuntimeException {
    public TicketAlreadyClosed(String message) {
        super(message);
    }
}