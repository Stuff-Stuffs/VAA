package io.github.stuff_stuffs.vaa.client;

import io.github.stuff_stuffs.vaa.common.VAA;
import io.github.stuff_stuffs.vaa.common.path.open.OpenSet;
import io.github.stuff_stuffs.vaa.common.path.valid.ValidLocationSet;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import org.lwjgl.glfw.GLFW;

public class VAAClient implements ClientModInitializer {
    private static final KeyBinding DEBUG_RENDERER_KEYBIND = new KeyBinding("vaa_debug_renderer", GLFW.GLFW_KEY_F12, KeyBinding.MISC_CATEGORY);
    private static boolean DEBUG_RENDERER_ENABLED = false;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(DEBUG_RENDERER_KEYBIND);
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            if (DEBUG_RENDERER_KEYBIND.wasPressed()) {
                DEBUG_RENDERER_ENABLED = !DEBUG_RENDERER_ENABLED;
            }
            if (DEBUG_RENDERER_ENABLED) {
                final BlockPos cameraPos = context.camera().getBlockPos();
                final Chunk chunk = context.world().getChunk(cameraPos);
                final ChunkSection section = chunk.getSection(chunk.getSectionIndex(cameraPos.getY()));
                if (section != null) {
                    final ChunkSectionPos sectionPos = ChunkSectionPos.from(cameraPos);
                    final OpenSet openSet = new OpenSet(section, sectionPos, context.world(), VAA.BASIC_OPEN_SET_TYPE);
                    final ValidLocationSet validLocationSet = new ValidLocationSet(sectionPos, context.world(), openSet, VAA.ONE_X_TWO_LOCATION_SET_TYPE);
                    final DustParticleEffect effect = new DustParticleEffect(new Vec3f(1, 1, 1), 1);
                    final int minX = sectionPos.getMinX();
                    final int minY = sectionPos.getMinY();
                    final int minZ = sectionPos.getMinZ();
                    for (int i = 0; i < 16; i++) {
                        for (int j = 0; j < 16; j++) {
                            for (int k = 0; k < 16; k++) {
                                if (validLocationSet.get(i, j, k)) {
                                    context.world().addParticle(effect, minX + i + 0.5, minY + j + 0.5, minZ + k + 0.5, 0, 0, 0);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
