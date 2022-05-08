package io.github.stuff_stuffs.vaa.common;

import io.github.stuff_stuffs.vaa.common.path.open.OpenSet;
import io.github.stuff_stuffs.vaa.common.path.open.OpenSetType;
import io.github.stuff_stuffs.vaa.common.path.valid.ValidLocationSet;
import io.github.stuff_stuffs.vaa.common.path.valid.ValidLocationSetType;
import io.github.stuff_stuffs.vaa.common.util.CollisionUtil;
import io.github.stuff_stuffs.vaa.common.util.WorldCache;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;

public class VAA implements ModInitializer {
    public static final String MOD_ID = "vaa";
    public static final OpenSetType BASIC_OPEN_SET_TYPE = OpenSetType.register(createId("basic"), new OpenSet.Classifier() {
        private final BlockPos.Mutable mutable = new BlockPos.Mutable();

        @Override
        public OpenSet.Type classify(final int x, final int y, final int z, final ChunkSection section, final World world) {
            final BlockState blockState = section.getBlockState(x & 15, y & 15, z & 15);
            mutable.set(x, y, z);
            final VoxelShape shape = blockState.getCollisionShape(world, mutable);
            if (shape.isEmpty()) {
                return blockState.getFluidState().isEmpty() ? OpenSet.Type.OPEN : OpenSet.Type.LIQUID;
            } else if (VoxelShapes.fullCube() == shape) {
                return OpenSet.Type.CLOSED;
            } else if (VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), shape, BooleanBiFunction.NOT_SAME)) {
                return OpenSet.Type.CONTEXT_SENSITIVE;
            } else {
                return OpenSet.Type.CLOSED;
            }
        }
    });
    public static final ValidLocationSetType ONE_X_TWO_LOCATION_SET_TYPE = ValidLocationSetType.register(createId("1x2"), new ValidLocationSet.Validator() {
        private static final Box CENTERED = new Box(0, 0, 0, 1, 2, 1);
        private static final Box FLOOR = new Box(0, -1, 0, 1, 0, 1);

        @Override
        public boolean isValidPos(final int x, final int y, final int z, final WorldCache cache, final OpenSet openSet) {
            if (openSet.get(x, y, z) == OpenSet.Type.CLOSED) {
                return false;
            }
            return CollisionUtil.doesCollide(FLOOR.offset(x, y, z), cache) && !CollisionUtil.doesCollide(CENTERED.offset(x, y, z), cache);
        }
    });

    @Override
    public void onInitialize() {


    }

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }
}
