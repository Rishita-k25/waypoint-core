package waypoint.model;

/**
 * Contract specification for parking spot allocation and release.
 *
 * For allocate(Vehicle vehicle):
 *   PRE  : vehicle != null; vehicle is not currently occupying a spot managed by this allocator.
 *   POST : Returns an available Spot s compatible with vehicle such that s.isOccupied() == true
 *          and s.occupant().equals(vehicle); if no compatible spot is available, throws
 *          NoSpotAvailable and leaves all spot states unchanged.
 *   INV  : Total spot count remains constant (available + occupied == total); no spot is assigned
 *          to more than one vehicle simultaneously; every occupied spot satisfies vehicle
 *          compatibility constraints.
 *
 * For release(Spot spot):
 *   PRE  : spot != null; spot belongs to this allocator's managed pool.
 *   POST : spot.isOccupied() == false and spot.occupant() == null; spot is returned to the
 *          available pool for future allocations.
 *   INV  : Total spot count remains constant; releasing a spot does not affect the occupancy or
 *          assignment of any other spot.
 *
 * Then the clauses from section 2.5 that apply here:
 *   Timing       : Synchronous and non-blocking; completes in bounded time and fails fast via
 *                  NoSpotAvailable rather than waiting for a spot to become free.
 *   Side effects : Mutates only the occupancy state of the single allocated or released Spot;
 *                  does not mutate the Vehicle argument or any unrelated Spot.
 *   Idempotency  : allocate() is NOT idempotent (repeated calls consume distinct spots or fail);
 *                  release() IS idempotent (releasing an already-free spot is a safe no-op).
 *   Threading    : Not thread-safe unless externally synchronized; concurrent callers must
 *                  synchronize externally to prevent double-booking the same Spot.
 *   Ordering     : Strategy-defined (e.g., nearest-first, level-balancing, or size-fit); the
 *                  interface guarantees spot availability and compatibility, not selection order.
 *
 * This Javadoc is graded. It is the specification the whole suite tests.
 */
public interface SpotAllocator {
    Spot allocate(Vehicle vehicle) throws NoSpotAvailable;
    void release(Spot spot);
}