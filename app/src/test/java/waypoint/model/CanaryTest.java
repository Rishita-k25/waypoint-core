package waypoint.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/** Proves the contract suite actually tests the contract. */
class CanaryTest {
    @Test
    void theSuiteRejectsADeliberatelyBrokenImplementation() {
        SpotAllocator broken = new BrokenAllocator();
        assertThrows(AssertionError.class,
            () -> assertNotNull(broken.allocate(new Vehicle("TEST-1", VehicleSize.COMPACT))),
            "the suite must fail on a null-returning allocator");

        Spot spot = new Spot("S1", 1, 10, new SpotType.Compact());
        assertThrows(AssertionError.class,
            () -> assertDoesNotThrow(() -> {
                broken.release(spot);
                broken.release(spot);
            }),
            "the suite must fail on a non-idempotent release()");
    }
}