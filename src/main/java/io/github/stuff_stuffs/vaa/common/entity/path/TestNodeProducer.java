package io.github.stuff_stuffs.vaa.common.entity.path;

import io.github.stuff_stuffs.vaa.common.entity.AIEntity;
import io.github.stuff_stuffs.vaa.common.entity.path.valid.BasicLocationType;
import io.github.stuff_stuffs.vaa.common.entity.path.valid.ValidLocationSetType;
import io.github.stuff_stuffs.vaa.common.util.WorldCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestNodeProducer implements NodeProducer {
    private final AIEntity aiEntity;
    private final World world;
    private final ValidLocationSetType<BasicLocationType> locationSetType;
    private WorldCache worldCache;

    public TestNodeProducer(final AIEntity aiEntity, final World world, final ValidLocationSetType<BasicLocationType> locationSetType) {
        this.aiEntity = aiEntity;
        this.world = world;
        this.locationSetType = locationSetType;
    }


    @Override
    public AIPathNode getStart(final WorldCache cache) {
        final BlockPos pos = aiEntity.getBlockPos();
        worldCache = cache;
        final boolean walk = world.getBlockState(pos.down()).hasSolidTopSurface(world, pos.down(), aiEntity);
        return new AIPathNode(pos.getX(), pos.getY(), pos.getZ(), 0, walk ? AIPathNode.Type.LAND : AIPathNode.Type.AIR, null, walk);
    }

    @Override
    public int getNeighbours(final AIPathNode root, final AIPathNode[] successors) {
        int i = 0;
        AIPathNode node = createBasic(root.x + 1, root.y, root.z, root);
        if (node != null) {
            successors[i++] = node;
        }
        node = createBasic(root.x - 1, root.y, root.z, root);
        if (node != null) {
            successors[i++] = node;
        }
        node = createBasic(root.x, root.y, root.z + 1, root);
        if (node != null) {
            successors[i++] = node;
        }
        node = createBasic(root.x, root.y, root.z - 1, root);
        if (node != null) {
            successors[i++] = node;
        }

        //Jump
        if (root.type == AIPathNode.Type.LAND || root.type == AIPathNode.Type.LIQUID) {
            node = createAir(root.x, root.y + 1, root.z, root);
            if (node != null) {
                successors[i++] = node;
            }
        }
        ////down
        if (root.type == AIPathNode.Type.AIR || root.type == AIPathNode.Type.LIQUID) {
            node = createAuto(root.x, root.y - 1, root.z, root);
            if (node != null) {
                successors[i++] = node;
            }
        }
        return i;
    }

    private AIPathNode createAir(final int x, final int y, final int z, final AIPathNode prev) {
        if (isWalkable(x, y, z) == BasicLocationType.OPEN) {
            return new AIPathNode(x, y, z, prev.distance + 1.5, AIPathNode.Type.AIR, prev, true);
        }
        return null;
    }

    private AIPathNode createBasic(final int x, final int y, final int z, final AIPathNode prev) {
        if (isWalkable(x, y, z) == BasicLocationType.GROUND) {
            return new AIPathNode(x, y, z, prev.distance + 1, AIPathNode.Type.LAND, prev, true);
        }
        return null;
    }

    private AIPathNode createAuto(final int x, final int y, final int z, final AIPathNode prev) {
        final BasicLocationType type = isWalkable(x, y, z);
        if (type != BasicLocationType.CLOSED) {
            final boolean ground = type == BasicLocationType.GROUND;
            return new AIPathNode(x, y, z, prev.distance + (ground ? 1 : 1.5), ground ? AIPathNode.Type.LAND : AIPathNode.Type.AIR, prev, true);
        }
        return null;
    }

    private BasicLocationType isWalkable(final int x, final int y, final int z) {
        return worldCache.getLocationType(x, y, z, locationSetType);
    }

    @Override
    public AIPathNode get(final int x, final int y, final int z) {
        final BlockPos pos = new BlockPos(x, y, z);
        final boolean walk = worldCache.getBlockState(pos.down()).hasSolidTopSurface(worldCache, pos.down(), aiEntity);
        return new AIPathNode(x, y, z, 0, walk ? AIPathNode.Type.LAND : AIPathNode.Type.AIR, null, walk);
    }

    @Override
    public void stats() {
        worldCache.stats();
    }
}
