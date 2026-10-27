package io.github.anjoismysign.mannequinbrain;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Mannequin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record BukkitMannequinBrain(Mannequin mannequin) implements MannequinBrain{
    private static final double EPSILON = 0.00001;

    private boolean isValid(){
        return mannequin.isValid();
    }

    @Override
    public void moveForward(double speed){
        if (!isValid()){
            return;
        }

        Vector direction = mannequin.getLocation().getDirection();
        Vector velocity = direction.normalize().multiply(speed);
        mannequin.setVelocity(velocity);
    }

    @Override
    public void moveTo(double speed,
                       @NotNull WorldPosition position){
        Objects.requireNonNull(position, "'position' cannot be null");
        if (!isValid()){
            return;
        }

        Location location = BukkitAdapter.INSTANCE.adapt(position);

        Location from = mannequin.getLocation();
        Vector target = location.toVector();
        Vector source = from.toVector();

        Vector direction = target.clone().subtract(source);
        if (direction.lengthSquared() <= 0.00001) {
            // same position
            return;
        }

        Vector look = direction.clone();

        Location lookLoc = from.clone();
        lookLoc.setDirection(look);
        mannequin.teleport(lookLoc);

        Vector velocity = direction.normalize().multiply(speed);
        mannequin.setVelocity(velocity);
    }

    @Override
    public void walkTo(double speed,
                       @NotNull WorldPosition position){
        Objects.requireNonNull(position, "'position' cannot be null");
        if (!isValid()){
            return;
        }

        Location location = BukkitAdapter.INSTANCE.adapt(position);
        Location from = mannequin.getLocation();

        Vector horizontal = new Vector(location.getX() - from.getX(), 0, location.getZ() - from.getZ());
        if (horizontal.lengthSquared() <= EPSILON) {
            // same horizontal position
            return;
        }

        face(yaw(horizontal), 0F);
        walk(horizontal, speed);
    }

    @Override
    public void walkForward(double speed){
        if (!isValid()){
            return;
        }

        double yaw = Math.toRadians(mannequin.getLocation().getYaw());
        Vector horizontal = new Vector(-Math.sin(yaw), 0, Math.cos(yaw));
        walk(horizontal, speed);
    }

    @Override
    public void face(@NotNull WorldPosition position){
        Objects.requireNonNull(position, "'position' cannot be null");
        if (!isValid()){
            return;
        }

        Location location = BukkitAdapter.INSTANCE.adapt(position);
        Vector direction = location.toVector().subtract(mannequin.getEyeLocation().toVector());
        if (direction.lengthSquared() <= EPSILON) {
            // same position
            return;
        }

        face(yaw(direction), pitch(direction));
    }

    @Override
    public void face(float yaw,
                     float pitch){
        if (!isValid()){
            return;
        }

        mannequin.setRotation(yaw, pitch);
    }

    @Override
    public void setStepHeight(double height){
        if (!isValid()){
            return;
        }

        AttributeInstance attribute = stepHeight();
        if (attribute == null){
            return;
        }
        attribute.setBaseValue(height);
    }

    @Override
    public double getStepHeight(){
        if (!isValid()){
            return 0;
        }

        AttributeInstance attribute = stepHeight();
        return attribute == null ? 0 : attribute.getValue();
    }

    @Override
    public boolean isOnGround(){
        if (!isValid()){
            return false;
        }

        return mannequin.isOnGround();
    }

    /**
     * Sets the horizontal velocity along {@code horizontal}, keeping the current Y velocity
     * so gravity applies.
     */
    private void walk(@NotNull Vector horizontal,
                      double speed){
        Vector velocity = horizontal.normalize().multiply(speed);
        velocity.setY(mannequin.getVelocity().getY());
        mannequin.setVelocity(velocity);
    }

    @Nullable
    private AttributeInstance stepHeight(){
        AttributeInstance attribute = mannequin.getAttribute(Attribute.STEP_HEIGHT);
        if (attribute != null){
            return attribute;
        }
        mannequin.registerAttribute(Attribute.STEP_HEIGHT);
        return mannequin.getAttribute(Attribute.STEP_HEIGHT);
    }

    // Same math as Location#setDirection
    static float yaw(@NotNull Vector direction){
        double twoPi = 2 * Math.PI;
        double theta = Math.atan2(-direction.getX(), direction.getZ());
        return (float) Math.toDegrees((theta + twoPi) % twoPi);
    }

    // Same math as Location#setDirection
    static float pitch(@NotNull Vector direction){
        double x = direction.getX();
        double z = direction.getZ();
        if (x == 0 && z == 0){
            return direction.getY() > 0 ? -90F : 90F;
        }
        double xz = Math.sqrt(x * x + z * z);
        return (float) Math.toDegrees(Math.atan(-direction.getY() / xz));
    }
}
