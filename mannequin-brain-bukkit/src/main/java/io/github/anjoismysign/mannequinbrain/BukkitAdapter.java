package io.github.anjoismysign.mannequinbrain;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum BukkitAdapter {
    INSTANCE;

    @NotNull
    public Location adapt(WorldPosition position){
        String worldName = position.worldName();
        World world = Bukkit.getWorld(worldName);
        Objects.requireNonNull(world, "'"+worldName+"' is not a valid World");
        return new Location(world, position.x(), position.y(), position.z(), position.yaw(), position.pitch());
    }
}
