package io.github.anjoismysign.mannequinbrain;

import org.jetbrains.annotations.NotNull;

public interface MannequinBrain {

    /**
     * Sets the mannequin's velocity to its full 3D look direction (yaw and pitch)
     * multiplied by {@code speed}.
     * <p>
     * This ignores gravity: the Y part of the velocity is overwritten, so the mannequin
     * flies/glides along its pitch. To walk on the ground, use {@link #walkForward(double)}.
     * <p>
     * Must be called every tick to keep moving.
     *
     * @param speed the speed, in blocks per tick
     */
    void moveForward(double speed);

    /**
     * Turns the mannequin (by teleporting it in place) towards {@code position} and sets
     * its velocity along the full 3D vector to it, multiplied by {@code speed}.
     * <p>
     * This ignores gravity: the Y part of the velocity is overwritten and the teleport
     * resets falling, so the mannequin moves in a straight line through the air.
     * To walk on the ground, use {@link #walkTo(double, WorldPosition)}.
     * <p>
     * Must be called every tick to keep moving.
     *
     * @param speed    the speed, in blocks per tick
     * @param position the target position
     */
    void moveTo(double speed,
                @NotNull WorldPosition position);

    /**
     * Walks the mannequin horizontally towards {@code position}, respecting gravity.
     * <p>
     * The target's Y is ignored: the horizontal (X/Z) direction is normalized and
     * multiplied by {@code speed}, while the mannequin's current Y velocity is kept,
     * so vanilla gravity and falling apply. The mannequin is turned to face the
     * walking direction using {@link #face(float, float)} (never a teleport).
     * Combine with {@link #setStepHeight(double)} to walk up blocks.
     * <p>
     * Does nothing if the horizontal distance to {@code position} is (almost) 0,
     * or if the mannequin is no longer valid.
     * <p>
     * Must be called every tick to keep walking.
     *
     * @param speed    the horizontal speed, in blocks per tick
     * @param position the target position
     * @throws UnsupportedOperationException if the implementation doesn't support walking
     */
    default void walkTo(double speed,
                        @NotNull WorldPosition position) {
        throw new UnsupportedOperationException("walkTo is not supported by " + getClass().getName());
    }

    /**
     * Walks the mannequin horizontally along its current yaw, ignoring its pitch,
     * respecting gravity.
     * <p>
     * The horizontal velocity is set to the yaw direction multiplied by {@code speed},
     * while the mannequin's current Y velocity is kept, so vanilla gravity and falling
     * apply. The mannequin already faces the walking direction, so it isn't rotated.
     * Combine with {@link #setStepHeight(double)} to walk up blocks.
     * <p>
     * Does nothing if the mannequin is no longer valid.
     * <p>
     * Must be called every tick to keep walking.
     *
     * @param speed the horizontal speed, in blocks per tick
     * @throws UnsupportedOperationException if the implementation doesn't support walking
     */
    default void walkForward(double speed) {
        throw new UnsupportedOperationException("walkForward is not supported by " + getClass().getName());
    }

    /**
     * Rotates the mannequin to look at {@code position}, from its eye location.
     * <p>
     * Unlike a teleport, this doesn't reset falling or velocity.
     * Does nothing if the mannequin is no longer valid, or if {@code position}
     * is (almost) at the mannequin's eyes.
     * <p>
     * Doesn't need to be called every tick: the rotation stays until something changes it.
     *
     * @param position the position to look at
     * @throws UnsupportedOperationException if the implementation doesn't support rotating
     */
    default void face(@NotNull WorldPosition position) {
        throw new UnsupportedOperationException("face is not supported by " + getClass().getName());
    }

    /**
     * Rotates the mannequin to the given yaw and pitch.
     * <p>
     * Unlike a teleport, this doesn't reset falling or velocity.
     * Does nothing if the mannequin is no longer valid.
     * <p>
     * Doesn't need to be called every tick: the rotation stays until something changes it.
     *
     * @param yaw   the yaw, in degrees
     * @param pitch the pitch, in degrees
     * @throws UnsupportedOperationException if the implementation doesn't support rotating
     */
    default void face(float yaw,
                      float pitch) {
        throw new UnsupportedOperationException("face is not supported by " + getClass().getName());
    }

    /**
     * Sets the maximum height the mannequin can step up while moving
     * (the vanilla step height attribute), for example {@code 1.0} to step up
     * full blocks like a horse. Stepping happens through vanilla movement,
     * so it applies while walking with {@link #walkTo(double, WorldPosition)}
     * or {@link #walkForward(double)}.
     * <p>
     * Does nothing if the mannequin is no longer valid.
     * <p>
     * Doesn't need to be called every tick: call it once, for example when the mannequin spawns.
     *
     * @param height the step height, in blocks
     * @throws UnsupportedOperationException if the implementation doesn't support step height
     */
    default void setStepHeight(double height) {
        throw new UnsupportedOperationException("setStepHeight is not supported by " + getClass().getName());
    }

    /**
     * Gets the maximum height the mannequin can step up while moving
     * (the vanilla step height attribute).
     *
     * @return the step height, in blocks, or {@code 0} if the mannequin is no longer valid
     * @throws UnsupportedOperationException if the implementation doesn't support step height
     */
    default double getStepHeight() {
        throw new UnsupportedOperationException("getStepHeight is not supported by " + getClass().getName());
    }

    /**
     * Checks whether the mannequin is standing on the ground.
     *
     * @return whether the mannequin is on the ground, or {@code false} if it's no longer valid
     * @throws UnsupportedOperationException if the implementation doesn't support this check
     */
    default boolean isOnGround() {
        throw new UnsupportedOperationException("isOnGround is not supported by " + getClass().getName());
    }

}
