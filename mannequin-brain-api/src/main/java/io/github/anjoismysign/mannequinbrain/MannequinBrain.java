package io.github.anjoismysign.mannequinbrain;

import org.jetbrains.annotations.NotNull;

public interface MannequinBrain {

    void moveForward(double speed);

    void moveTo(double speed,
                @NotNull WorldPosition position);

}
