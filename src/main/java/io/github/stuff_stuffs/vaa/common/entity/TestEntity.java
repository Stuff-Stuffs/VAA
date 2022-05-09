package io.github.stuff_stuffs.vaa.common.entity;

import io.github.stuff_stuffs.vaa.common.VAA;
import io.github.stuff_stuffs.vaa.common.entity.control.AIMoveControl;
import io.github.stuff_stuffs.vaa.common.entity.path.AINavigator;
import io.github.stuff_stuffs.vaa.common.entity.path.AIPather;
import io.github.stuff_stuffs.vaa.common.entity.path.PathTarget;
import io.github.stuff_stuffs.vaa.common.entity.path.TestNodeProducer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;

public class TestEntity extends AIEntity {
    private boolean init = false;

    protected TestEntity(final EntityType<? extends LivingEntity> entityType, final World world) {
        super(entityType, world);
    }

    @Override
    public void baseTick() {
        if (getNavigation().idle() && getNavigation().getCurrentPath()==null) {
            getNavigation().start(PathTarget.createBlockTarget(1, new BlockPos(0, 1, 0)));
        }
        super.baseTick();
    }

    @Override
    protected AIMoveControl createMoveControl() {
        return new AIMoveControl(this);
    }

    @Override
    protected AINavigator createNavigator() {
        return new AINavigator(this, world, 40, new AIPather(this, world, new TestNodeProducer(this, world, VAA.ONE_X_TWO_LOCATION_SET_TYPE)));
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getEquippedStack(final EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(final EquipmentSlot slot, final ItemStack stack) {

    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }
}
