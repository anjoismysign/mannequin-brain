package io.github.anjoismysign.mannequinbrain;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Mannequin;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BukkitMannequinBrainTest {
    private static final double DELTA = 1.0E-6;
    private static final double GRAVITY_Y = -0.0784;

    private MockedStatic<Bukkit> bukkit;
    private World world;
    private Mannequin mannequin;
    private BukkitMannequinBrain brain;

    @BeforeEach
    void setUp(){
        world = mock(World.class);
        bukkit = mockStatic(Bukkit.class);
        bukkit.when(() -> Bukkit.getWorld("world")).thenReturn(world);

        mannequin = mock(Mannequin.class);
        when(mannequin.isValid()).thenReturn(true);
        when(mannequin.getLocation()).thenReturn(new Location(world, 0, 50, 0, 0, 0));
        when(mannequin.getEyeLocation()).thenReturn(new Location(world, 0, 51.62, 0, 0, 0));
        when(mannequin.getVelocity()).thenReturn(new Vector(0.3, GRAVITY_Y, 0.1));
        brain = new BukkitMannequinBrain(mannequin);
    }

    @AfterEach
    void tearDown(){
        bukkit.close();
    }

    private static WorldPosition position(double x, double y, double z){
        return new WorldPosition("world", x, y, z, 0, 0);
    }

    private Vector capturedVelocity(){
        ArgumentCaptor<Vector> captor = ArgumentCaptor.forClass(Vector.class);
        verify(mannequin).setVelocity(captor.capture());
        return captor.getValue();
    }

    @Test
    void walkToIgnoresTargetYAndKeepsVerticalVelocity(){
        brain.walkTo(0.2, position(3, 52, 4));

        Vector velocity = capturedVelocity();
        assertEquals(0.2 * 3 / 5, velocity.getX(), DELTA);
        assertEquals(GRAVITY_Y, velocity.getY(), DELTA);
        assertEquals(0.2 * 4 / 5, velocity.getZ(), DELTA);
        verify(mannequin, never()).teleport(any(Location.class));
    }

    @Test
    void walkToFacesWalkingDirection(){
        // +X is yaw 270, -X is yaw 90, +Z is yaw 0
        brain.walkTo(0.2, position(10, 52, 0));

        verify(mannequin).setRotation(270F, 0F);
    }

    @Test
    void walkToDoesNothingWhenTargetIsStraightAbove(){
        brain.walkTo(0.2, position(0, 52, 0));

        verify(mannequin, never()).setVelocity(any());
        verify(mannequin, never()).setRotation(anyFloat(), anyFloat());
    }

    @Test
    void walkForwardUsesYawIgnoresPitchAndKeepsVerticalVelocity(){
        // yaw 90 faces -X, pitch -45 looks up
        when(mannequin.getLocation()).thenReturn(new Location(world, 0, 50, 0, 90, -45));

        brain.walkForward(0.2);

        Vector velocity = capturedVelocity();
        assertEquals(-0.2, velocity.getX(), DELTA);
        assertEquals(GRAVITY_Y, velocity.getY(), DELTA);
        assertEquals(0, velocity.getZ(), DELTA);
        verify(mannequin, never()).teleport(any(Location.class));
        verify(mannequin, never()).setRotation(anyFloat(), anyFloat());
    }

    @Test
    void faceLooksAtPositionFromEyes(){
        // 1 block forward (+Z), 1 block below the eyes: pitch 45 (down)
        brain.face(position(0, 50.62, 1));

        verify(mannequin).setRotation(0F, 45F);
        verify(mannequin, never()).teleport(any(Location.class));
    }

    @Test
    void newMethodsAreNoOpsWhenInvalid(){
        when(mannequin.isValid()).thenReturn(false);

        brain.walkTo(0.2, position(3, 52, 4));
        brain.walkForward(0.2);
        brain.face(position(3, 52, 4));
        brain.face(10F, 10F);
        brain.setStepHeight(1.0);

        verify(mannequin, never()).setVelocity(any());
        verify(mannequin, never()).setRotation(anyFloat(), anyFloat());
        verify(mannequin, never()).getAttribute(any());
        assertEquals(0, brain.getStepHeight(), DELTA);
        assertFalse(brain.isOnGround());
    }

    @Test
    void moveToStillFliesInAStraightLine(){
        brain.moveTo(0.5, position(3, 54, 0));

        verify(mannequin).teleport(any(Location.class));
        Vector velocity = capturedVelocity();
        assertEquals(0.3, velocity.getX(), DELTA);
        assertEquals(0.4, velocity.getY(), DELTA);
        assertEquals(0, velocity.getZ(), DELTA);
    }
}
