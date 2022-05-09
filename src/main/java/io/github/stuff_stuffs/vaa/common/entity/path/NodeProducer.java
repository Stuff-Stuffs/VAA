package io.github.stuff_stuffs.vaa.common.entity.path;

import io.github.stuff_stuffs.vaa.common.util.WorldCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public interface NodeProducer {
    AIPathNode getStart(WorldCache cache);

    int getNeighbours(AIPathNode root, AIPathNode[] successors);

    AIPathNode get(int x, int y, int z);

    void stats();

    static double getFeetY(final BlockView world, final BlockPos pos) {
        final BlockPos blockPos = pos.down();
        final VoxelShape voxelShape = world.getBlockState(blockPos).getCollisionShape(world, blockPos);
        return blockPos.getY() + (voxelShape.isEmpty() ? 0 : voxelShape.getMax(Direction.Axis.Y));
    }
}
