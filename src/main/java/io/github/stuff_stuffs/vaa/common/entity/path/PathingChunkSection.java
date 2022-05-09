package io.github.stuff_stuffs.vaa.common.entity.path;

import io.github.stuff_stuffs.vaa.common.entity.path.open.OpenSet;
import io.github.stuff_stuffs.vaa.common.entity.path.open.OpenSetType;
import io.github.stuff_stuffs.vaa.common.entity.path.valid.ValidLocationSet;
import io.github.stuff_stuffs.vaa.common.entity.path.valid.ValidLocationSetType;
import io.github.stuff_stuffs.vaa.common.util.WorldCache;
import net.minecraft.util.math.ChunkSectionPos;

public interface PathingChunkSection {
    OpenSet vaa$getOpenSet(OpenSetType type, ChunkSectionPos pos, WorldCache world);

    <T> ValidLocationSet<T> vaa$getValidLocationSet(ValidLocationSetType<T> type, ChunkSectionPos pos, WorldCache world);

    <T> ValidLocationSet<T> vaa$getValidLocationSet(ValidLocationSetType<T> type, int x, int y, int z, WorldCache world);
}
