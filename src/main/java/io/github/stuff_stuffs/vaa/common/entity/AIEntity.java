package io.github.stuff_stuffs.vaa.common.entity;

import io.github.stuff_stuffs.vaa.common.entity.control.AIMoveControl;
import io.github.stuff_stuffs.vaa.common.entity.path.AINavigator;
import io.github.stuff_stuffs.vaa.common.entity.path.AIPathNode;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AIEntity extends LivingEntity {
    private final AINavigator navigator;
    private final AIMoveControl moveControl;

    protected AIEntity(final EntityType<? extends LivingEntity> entityType, final World world) {
        super(entityType, world);
        navigator = createNavigator();
        moveControl = createMoveControl();
    }

    protected abstract AIMoveControl createMoveControl();

    @Override
    public void baseTick() {
        super.baseTick();
        navigator.tick();
        moveControl.tick();
        if (!navigator.idle() && !navigator.getCurrentPath().isFinished()) {
            final AIPathNode node = navigator.getCurrentPath().getCurrent();
            moveControl.moveTo(node.x, node.y, node.z, 0.5);
            final Vec3d pos = getPos();
            if (Math.abs(node.x - pos.x + 1 / 2D) + Math.abs(node.y - pos.y) + Math.abs(node.z - pos.z + 1 / 2D) < 1.5) {
                navigator.getCurrentPath().next();
            }
        }
    }

    protected abstract AINavigator createNavigator();

    public void setForwardSpeed(final float forwardSpeed) {
        this.forwardSpeed = forwardSpeed;
    }

    public void setUpwardSpeed(final float upwardSpeed) {
        this.upwardSpeed = upwardSpeed;
    }

    public void setSidewaysSpeed(final float sidewaysMovement) {
        sidewaysSpeed = sidewaysMovement;
    }

    @Override
    public void setMovementSpeed(final float movementSpeed) {
        super.setMovementSpeed(movementSpeed);
        setForwardSpeed(movementSpeed);
    }

    public AINavigator getNavigation() {
        return navigator;
    }
}
