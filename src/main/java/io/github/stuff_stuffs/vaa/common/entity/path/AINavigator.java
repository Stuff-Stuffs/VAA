package io.github.stuff_stuffs.vaa.common.entity.path;

import io.github.stuff_stuffs.vaa.common.entity.AIEntity;
import io.github.stuff_stuffs.vaa.common.util.MovingAverage;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;

import java.util.concurrent.CompletableFuture;

public class AINavigator {
    private final AIEntity entity;
    private final World world;
    private final MovingAverage realSpeed;
    private final MovingAverage targetSpeed;
    private final AIPather pather;
    //private final double speedErr;
    private PathTarget pathTarget = null;
    private long ticksSinceRePath = 0;
    private boolean shouldRePath = false;
    private AIPath currentPath;
    private CompletableFuture<AIPath> queuedPath;

    public AINavigator(final AIEntity entity, final World world, final int speedSamples,/* final double speedErr,*/ final AIPather pather) {
        this.entity = entity;
        this.world = world;
        realSpeed = new MovingAverage(speedSamples);
        targetSpeed = new MovingAverage(speedSamples);
        //this.speedErr = speedErr;

        this.pather = pather;
    }

    public AIPather getPather() {
        return pather;
    }

    public void tick() {
        if (queuedPath != null && queuedPath.isDone()) {
            currentPath = queuedPath.join();
        }
        if (!idle()) {
            if (currentPath.isFinished()) {
                stop();
                return;
            }
            if (ticksSinceRePath == 0) {
                targetSpeed.reset();
                realSpeed.reset();
            }
            ticksSinceRePath++;
            if (shouldRePath && ticksSinceRePath > 20) {
                calculatePath();
            }
            if (ticksSinceRePath > 100) {
                realSpeed.addSample(entity.getPos().squaredDistanceTo(entity.prevX, entity.prevY, entity.prevZ));
                targetSpeed.addSample(getTargetSpeed());
                if (targetSpeed.avg() * 1 > realSpeed.avg()) {
                    shouldRePath = true;
                }
            }
        }
    }

    private double getTargetSpeed() {
        return 0.01;
    }

    public boolean idle() {
        return currentPath == null || currentPath.isFinished();
    }

    public void start(final PathTarget pathTarget) {
        this.pathTarget = pathTarget;
        calculatePath();
    }

    private void calculatePath() {
        ticksSinceRePath = 0;
        if (pathTarget != null) {
            final double f = entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE) * 5;
            if (queuedPath != null) {
                queuedPath.cancel(true);
            }
            queuedPath = pather.calculatePath(pathTarget, 100, true);
            shouldRePath = false;
        }
    }

    public void stop() {
        ticksSinceRePath = 0;
        shouldRePath = false;
        currentPath = null;
        pathTarget = null;
    }


    public AIPath getCurrentPath() {
        return currentPath;
    }
}
