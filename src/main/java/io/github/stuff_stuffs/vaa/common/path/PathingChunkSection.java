package io.github.stuff_stuffs.vaa.common.path;

import io.github.stuff_stuffs.vaa.common.path.open.OpenSet;
import io.github.stuff_stuffs.vaa.common.path.open.OpenSetType;
import io.github.stuff_stuffs.vaa.common.path.valid.ValidLocationSet;
import io.github.stuff_stuffs.vaa.common.path.valid.ValidLocationSetType;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public interface PathingChunkSection {
    OpenSet vaa$getOpenSet(OpenSetType type, ChunkSectionPos pos, World world);

    ValidLocationSet vaa$getValidLocationSet(ValidLocationSetType type, ChunkSectionPos pos, World world);
}
