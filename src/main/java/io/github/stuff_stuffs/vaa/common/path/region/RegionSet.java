package io.github.stuff_stuffs.vaa.common.path.region;

import io.github.stuff_stuffs.vaa.common.path.valid.ValidLocationSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;

public final class RegionSet {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final short[] regions;

    public RegionSet(final NeighbourVerifier verifier, final ChunkSectionPos sectionPos, final ValidLocationSet validLocationSet, final World world) {
        final int baseX = sectionPos.getMinX();
        final int baseY = sectionPos.getMinY();
        final int baseZ = sectionPos.getMinZ();
        short regionCount = 0;
        regions = new short[16*16*16];
        Arrays.fill(regions, (short) -1);
        final ShortArrayList stack = new ShortArrayList();
        final ShortSet waiting = new ShortOpenHashSet();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int idx = packLocal(x, y, z) & 0xFFF;
                    if (regions[idx] == -1) {
                        final short regionIdx = regionCount++;
                        regions[idx] = regionIdx;
                        stack.clear();
                        stack.push((short) idx);
                        waiting.clear();
                        waiting.add((short) idx);
                        while (!stack.isEmpty()) {
                            final short cursor = stack.popShort();
                            final int xP = ChunkSectionPos.unpackLocalX(cursor);
                            final int yP = ChunkSectionPos.unpackLocalY(cursor);
                            final int zP = ChunkSectionPos.unpackLocalZ(cursor);
                            for (final Direction direction : DIRECTIONS) {
                                final int offX = direction.getOffsetX();
                                final int offY = direction.getOffsetY();
                                final int offZ = direction.getOffsetZ();
                                if ((baseX + xP + offX) >> 4 != baseX >> 4 || (baseY + yP + offY) >> 4 != baseY >> 4 || (baseZ + zP + offZ) >> 4 != baseZ >> 4) {
                                    continue;
                                }
                                if (verifier.verify(baseX + xP + offX, baseY + yP + offY, baseZ + zP + offZ, validLocationSet, world)) {
                                    idx = packLocal(x, y, z) & 0xFFF;
                                    final short shortIdx = (short) idx;
                                    if (waiting.add(shortIdx)) {
                                        if (regions[idx] != -1) {
                                            throw new RuntimeException("Asymmetric region verifier!");
                                        }
                                        regions[idx] = regionIdx;
                                        stack.push(shortIdx);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    final int idx = packLocal(x, y, z) & 0xFFF;
                    final int region = regions[idx];
                    if (region != -1) {
                        throw new RuntimeException("Somehow missed a region while searching!");
                    }
                }
            }
        }
    }

    public short getRegion(final int x, final int y, final int z) {
        return regions[packLocal(x, y, z) & 0xFFF];
    }

    private static short packLocal(final int x, final int y, final int z) {
        final int i = ChunkSectionPos.getLocalCoord(x);
        final int j = ChunkSectionPos.getLocalCoord(y);
        final int k = ChunkSectionPos.getLocalCoord(z);
        return (short) ((i << 8 | k << 4 | j << 0));
    }

    public interface NeighbourVerifier {
        boolean verify(int x, int y, int z, ValidLocationSet locationSet, World world);
    }
}
