package io.github.stuff_stuffs.vaa.common.entity.path.valid;

import io.github.stuff_stuffs.vaa.common.entity.path.open.OpenSet;
import io.github.stuff_stuffs.vaa.common.util.WorldCache;
import net.minecraft.util.math.ChunkSectionPos;

public final class ValidLocationSet<T> {
    private final int mask;
    private final int bitCount;
    private final ValidLocationSetType.UniverseInfo<T> universeInfo;
    private final long[] data;

    public ValidLocationSet(final ChunkSectionPos sectionPos, final WorldCache cache, final OpenSet openSet, final ValidLocationSetType<T> setType) {
        final Validator<T> validator = setType.getValidator();
        universeInfo = setType.getUniverseInfo();
        int universeSize = universeInfo.getUniverseSize();
        if ((universeSize & (universeSize - 1)) != 0) {
            universeSize = universeSize | universeSize >> 1;
            universeSize = universeSize | universeSize >> 4;
            universeSize = universeSize | universeSize >> 8;
            universeSize = universeSize | universeSize >> 16;
            universeSize = universeSize + 1;
        }
        mask = universeSize - 1;
        bitCount = Integer.highestOneBit(mask);

        data = new long[(16 * 16 * 16 * bitCount + 63) / 64];

        final int baseX = sectionPos.getMinX();
        final int baseY = sectionPos.getMinY();
        final int baseZ = sectionPos.getMinZ();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    final T val = validator.validate(baseX + x, baseY + y, baseZ + z, cache, openSet);
                    final int index = byteIndex(x & 15, y & 15, z & 15);
                    final int subIndex = subIndex(x & 15, y & 15, z & 15);
                    long datum = data[index];
                    datum = datum | (long) (universeInfo.toInt(val) & mask) <<subIndex;
                    data[index] = datum;
                }
            }
        }
    }

    public T get(final int x, final int y, final int z) {
        final int index = byteIndex(x & 15, y & 15, z & 15);
        final int subIndex = subIndex(x & 15, y & 15, z & 15);
        final long datum = data[index];
        return universeInfo.fromInt((int) ((datum >> subIndex) & mask));
    }

    private int subIndex(final int x, final int y, final int z) {
        return (x * 16 * 16 + y * 16 + z) * bitCount % 64;
    }

    private int byteIndex(final int x, final int y, final int z) {
        return (x * 16 * 16 + y * 16 + z) * bitCount / 64;
    }

    public interface Validator<T> {
        T validate(int x, int y, int z, WorldCache cache, OpenSet openSet);
    }
}
