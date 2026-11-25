package waypoint.model;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AllocatorContractTest {

    static Stream<SpotAllocator> implementations() {
        return Stream.of(new NearestFirstAllocator(threeFreeSpots()),
                         new LevelBalancingAllocator(threeFreeSpots()),
                         new SizeFitAllocator(threeFreeSpots()));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void neverReturnsNull(SpotAllocator a) throws NoSpotAvailable {
        assertNotNull(a.allocate(car()));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void allocatedSpotIsNoLongerFree(SpotAllocator a) throws NoSpotAvailable {
        assertFalse(a.allocate(car()).isFree());
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void acceptsEveryVehicleSize(SpotAllocator a) {
        assertDoesNotThrow(() -> a.allocate(motorcycle()));   // the contract accepts all sizes
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void throwsWhenNothingFits(SpotAllocator a) throws NoSpotAvailable {
        a.allocate(car());
        a.allocate(car());
        a.allocate(car());
        assertThrows(NoSpotAvailable.class, () -> a.allocate(car()));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void releaseIsIdempotent(SpotAllocator a) throws NoSpotAvailable {
        Spot s = a.allocate(car());
        a.release(s);
        assertTrue(s.isFree());
        assertDoesNotThrow(() -> a.release(s));
        assertTrue(s.isFree());
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void neverAllocatesTheSameSpotTwice(SpotAllocator a) throws NoSpotAvailable {
        Spot first = a.allocate(car());
        for (int i = 0; i < 5; i++) {
            try { assertNotEquals(first, a.allocate(car())); }
            catch (NoSpotAvailable expected) { return; }
        }
    }

    private static List<Spot> threeFreeSpots() {
        return List.of(
            new Spot("S1", 1, 10, new SpotType.Compact()),
            new Spot("S2", 1, 20, new SpotType.Large()),
            new Spot("S3", 2, 15, new SpotType.Ev(22))
        );
    }

    private static Vehicle car() {
        return new Vehicle("CAR-" + UUID.randomUUID(), VehicleSize.COMPACT);
    }

    private static Vehicle motorcycle() {
        return new Vehicle("MOTO-" + UUID.randomUUID(), VehicleSize.MOTORCYCLE);
    }
}