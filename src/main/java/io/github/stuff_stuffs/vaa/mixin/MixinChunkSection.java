package io.github.stuff_stuffs.vaa.mixin;

import io.github.stuff_stuffs.vaa.common.entity.path.PathingChunkSection;
import io.github.stuff_stuffs.vaa.common.entity.path.open.OpenSet;
import io.github.stuff_stuffs.vaa.common.entity.path.open.OpenSetType;
import io.github.stuff_stuffs.vaa.common.entity.path.valid.ValidLocationSet;
import io.github.stuff_stuffs.vaa.common.entity.path.valid.ValidLocationSetType;
import io.github.stuff_stuffs.vaa.common.util.WorldCache;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ChunkSection.class)
public class MixinChunkSection implements PathingChunkSection {
    private boolean cleared = true;
    @Unique
    private final Map<OpenSetType, OpenSet> openSetCache = new Reference2ReferenceOpenHashMap<>();
    @Unique
    private final Map<ValidLocationSetType<?>, ValidLocationSet<?>> validLocationSetCache = new Reference2ReferenceOpenHashMap<>();

    @Inject(at = @At("RETURN"), method = "setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;")
    private void clearCache(final int x, final int y, final int z, final BlockState state, final boolean lock, final CallbackInfoReturnable<BlockState> cir) {
        if (!cleared) {
            if (cir.getReturnValue() != state) {
                openSetCache.clear();
                validLocationSetCache.clear();
                cleared = true;
            }
        }
    }

    @Override
    public OpenSet vaa$getOpenSet(final OpenSetType type, final ChunkSectionPos pos, final WorldCache world) {
        OpenSet openSet = openSetCache.get(type);
        if (openSet == null) {
            openSet = new OpenSet((ChunkSection) (Object) this, pos, world, type);
            openSetCache.put(type, openSet);
            cleared = false;
        }
        return openSet;
    }

    @Override
    public <T> ValidLocationSet<T> vaa$getValidLocationSet(final ValidLocationSetType<T> type, final ChunkSectionPos pos, final WorldCache world) {
        ValidLocationSet<T> locationSet = (ValidLocationSet<T>) validLocationSetCache.get(type);
        if (locationSet == null) {
            locationSet = new ValidLocationSet<>(pos, world, vaa$getOpenSet(type.getOpenSetType(), pos, world), type);
            validLocationSetCache.put(type, locationSet);
            cleared = false;
        }
        return locationSet;
    }

    @Override
    public <T> ValidLocationSet<T> vaa$getValidLocationSet(final ValidLocationSetType<T> type, final int x, final int y, final int z, final WorldCache world) {
        ValidLocationSet<T> locationSet = (ValidLocationSet<T>) validLocationSetCache.get(type);
        if (locationSet == null) {
            final ChunkSectionPos pos = ChunkSectionPos.from(new BlockPos(x, y, z));
            locationSet = new ValidLocationSet<>(pos, world, vaa$getOpenSet(type.getOpenSetType(), pos, world), type);
            validLocationSetCache.put(type, locationSet);
            cleared = false;
        }
        return locationSet;
    }
}
