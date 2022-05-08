package io.github.stuff_stuffs.vaa.common.path.open;

import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;

public final class OpenSet {
    private static final Type[] TYPES = Type.values();
    private static final byte OPEN = 0b00;
    private static final byte CLOSED = 0b01;
    private static final byte CONTEXT_SENSITIVE = 0b10;
    private static final byte LIQUID = 0b11;
    private static final int DATA_SIZE = (16 * 16 * 16 * 2) / 8;
    private final byte[] data = new byte[DATA_SIZE];

    public OpenSet(final ChunkSection section, final ChunkSectionPos sectionPos, final World world, final OpenSetType setType) {
        final Classifier classifier = setType.getClassifier();
        final int baseX = sectionPos.getMinX();
        final int baseY = sectionPos.getMinY();
        final int baseZ = sectionPos.getMinZ();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    final int byteIndex = byteIndex(x, y, z);
                    final int subIndex = subIndex(x, y, z);
                    byte datum = data[byteIndex];
                    datum = (byte) (datum | (classifier.classify(baseX + x, baseY + y, baseZ + z, section, world).tag << (subIndex * 2)));
                    data[byteIndex] = datum;
                }
            }
        }
    }

    public Type get(final int x, final int y, final int z) {
        final int byteIndex = byteIndex(x & 15, y & 15, z & 15);
        final int subIndex = subIndex(x & 15, y & 15, z & 15);
        final byte datum = data[byteIndex];
        return TYPES[(datum >> (subIndex * 2)) & 0b11];
    }

    private static int subIndex(final int x, final int y, final int z) {
        return z & 0b11;
    }

    private static int byteIndex(final int x, final int y, final int z) {
        return (x * 16 * 16 + y * 16 + z) / 4;
    }

    public enum Type {
        OPEN(OpenSet.OPEN),
        CLOSED(OpenSet.CLOSED),
        CONTEXT_SENSITIVE(OpenSet.CONTEXT_SENSITIVE),
        LIQUID(OpenSet.LIQUID);
        private final byte tag;

        Type(final byte tag) {
            this.tag = tag;
        }
    }

    public interface Classifier {
        Type classify(int x, int y, int z, ChunkSection section, World world);
    }
}
