package io.github.stuff_stuffs.vaa.common.path.valid;

import io.github.stuff_stuffs.vaa.common.path.open.OpenSet;
import io.github.stuff_stuffs.vaa.common.util.WorldCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public final class ValidLocationSet {
    private static final Vec3i PADDING = new Vec3i(8,8,8);
    private static final int DATA_SIZE = 16 * 16 * 16;
    private final byte[] data = new byte[DATA_SIZE];

    public ValidLocationSet(final ChunkSectionPos sectionPos, final World world, final OpenSet openSet, final ValidLocationSetType setType) {
        final Validator validator = setType.getValidator();
        final int baseX = sectionPos.getMinX();
        final int baseY = sectionPos.getMinY();
        final int baseZ = sectionPos.getMinZ();
        WorldCache cache = new WorldCache(world, sectionPos.getMinPos().subtract(PADDING), new BlockPos(sectionPos.getMaxX() + 8, sectionPos.getMaxY() + 8, sectionPos.getMaxZ() + 8));
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    final int byteIndex = byteIndex(x, y, z);
                    final int subIndex = subIndex(x, y, z);
                    byte datum = data[byteIndex];
                    if ((validator.isValidPos(baseX + x, baseY + y, baseZ + z, cache, openSet))) {
                        datum = (byte) (datum | (1 << subIndex));
                        data[byteIndex] = datum;
                    }
                }
            }
        }
    }

    public boolean get(final int x, final int y, final int z) {
        final int byteIndex = byteIndex(x & 15, y & 15, z & 15);
        final int subIndex = subIndex(x & 15, y & 15, z & 15);
        final byte datum = data[byteIndex];
        return ((datum >> subIndex) & 1) == 1;
    }

    private static int subIndex(final int x, final int y, final int z) {
        return z & 0b111;
    }

    private static int byteIndex(final int x, final int y, final int z) {
        return (x * 16 * 16 + y * 16 + z) / 8;
    }

    public interface Validator {
        boolean isValidPos(int x, int y, int z, WorldCache cache, OpenSet openSet);
    }
}
