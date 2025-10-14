package io.github.anjoismysign.mannequinbrain;

import org.bukkit.Location;
import org.bukkit.entity.Mannequin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record BukkitMannequinBrain(Mannequin mannequin) implements MannequinBrain{
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
        if (direction.lengthSquared() == 0) {
            // same position
            return;
        }

        Vector look = direction.clone();
        // Optional: face horizontally only
        // look.setY(0);

        Location lookLoc = from.clone();
        lookLoc.setDirection(look);
        mannequin.teleport(lookLoc);

        Vector velocity = direction.normalize().multiply(speed);
        mannequin.setVelocity(velocity);
    }
}
