package io.github.stuff_stuffs.vaa.common.entity.control;

import io.github.stuff_stuffs.vaa.common.entity.AIEntity;
import io.github.stuff_stuffs.vaa.common.entity.path.AINavigator;
import io.github.stuff_stuffs.vaa.common.entity.path.NodeProducer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

public class AIMoveControl {
    protected final AIEntity entity;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected double speed;
    protected float forwardMovement;
    protected float sidewaysMovement;
    protected State state;

    public AIMoveControl(final AIEntity entity) {
        state = State.WAIT;
        this.entity = entity;
    }

    public boolean isMoving() {
        return state == State.MOVE_TO;
    }

    public double getSpeed() {
        return speed;
    }

    public void moveTo(final double x, final double y, final double z, final double speed) {
        targetX = x + 1 / 2D;
        targetY = y;
        targetZ = z + 1 / 2D;
        this.speed = speed;
        if (state != State.JUMPING) {
            state = State.MOVE_TO;
        }

    }

    public void strafeTo(final float forward, final float sideways) {
        state = State.STRAFE;
        forwardMovement = forward;
        sidewaysMovement = sideways;
        speed = 0.25D;
    }

    public void tick() {
        final float q;
        if (state == State.STRAFE) {
            final float f = (float) entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            final float g = (float) speed * f;
            float h = forwardMovement;
            float i = sidewaysMovement;
            float j = MathHelper.sqrt(h * h + i * i);
            if (j < 1.0F) {
                j = 1.0F;
            }

            j = g / j;
            h *= j;
            i *= j;
            final float k = MathHelper.sin(entity.getYaw() * 0.017453292F);
            final float l = MathHelper.cos(entity.getYaw() * 0.017453292F);
            final float m = h * l - i * k;
            q = i * l + h * k;
            if (!method_25946(m, q)) {
                forwardMovement = 1.0F;
                sidewaysMovement = 0.0F;
            }

            entity.setMovementSpeed(g);
            entity.setForwardSpeed(forwardMovement);
            entity.setSidewaysSpeed(sidewaysMovement);
            state = State.WAIT;
        } else if (state == State.MOVE_TO) {
            state = State.WAIT;
            entity.setJumping(false);
            final double d = targetX - entity.getX();
            final double e = targetZ - entity.getZ();
            final double o = targetY - entity.getY();
            final double p = d * d + o * o + e * e;
            if (p < 2.500000277905201E-7D) {
                entity.setForwardSpeed(0.0F);
                return;
            }

            q = (float) (MathHelper.atan2(e, d) * 57.2957763671875D) - 90.0F;
            entity.setYaw(changeAngle(entity.getYaw(), q, 90.0F));
            entity.setMovementSpeed((float) (speed * entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
            final BlockPos blockPos = entity.getBlockPos().offset(Direction.fromRotation(entity.getYaw()));
            final BlockState blockState = entity.world.getBlockState(blockPos);
            final VoxelShape voxelShape = blockState.getCollisionShape(entity.world, blockPos);
            if (o > (double) entity.stepHeight && d * d + e * e < (double) Math.max(1.0F, entity.getWidth() * entity.getWidth()) || voxelShape.getMax(Direction.Axis.Y) > entity.stepHeight) {
                state = State.JUMPING;
                entity.setJumping(true);
            }
        } else if (state == State.JUMPING) {
            entity.setMovementSpeed((float) (speed * entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
            entity.setJumping(true);
            if (entity.isOnGround()) {
                state = State.WAIT;
            }
        } else {
            entity.setForwardSpeed(0.0F);
            entity.setJumping(false);
        }
    }

    private boolean method_25946(final float f, final float g) {
        final AINavigator entityNavigation = entity.getNavigation();
        if (entityNavigation != null) {
            final NodeProducer pathNodeMaker = entityNavigation.getPather().getNodeProducer();
            return pathNodeMaker == null || pathNodeMaker.get(MathHelper.floor(entity.getX() + (double) f), MathHelper.floor(entity.getY()), MathHelper.floor(entity.getZ() + (double) g)).walkable;
        }

        return true;
    }

    protected float changeAngle(final float from, final float to, final float max) {
        float f = MathHelper.wrapDegrees(to - from);
        if (f > max) {
            f = max;
        }

        if (f < -max) {
            f = -max;
        }

        float g = from + f;
        if (g < 0.0F) {
            g += 360.0F;
        } else if (g > 360.0F) {
            g -= 360.0F;
        }

        return g;
    }

    public double getTargetX() {
        return targetX;
    }

    public double getTargetY() {
        return targetY;
    }

    public double getTargetZ() {
        return targetZ;
    }

    protected static enum State {
        WAIT,
        MOVE_TO,
        STRAFE,
        JUMPING;
    }
}
