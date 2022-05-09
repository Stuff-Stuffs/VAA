package io.github.stuff_stuffs.vaa.client.render.entity;

import io.github.stuff_stuffs.vaa.common.VAA;
import io.github.stuff_stuffs.vaa.common.entity.TestEntity;
import io.github.stuff_stuffs.vaa.common.entity.path.AIPath;
import io.github.stuff_stuffs.vaa.common.entity.path.AIPathNode;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class TestRenderer extends EntityRenderer<TestEntity> {
    public TestRenderer(final EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(final TestEntity entity, final float yaw, final float tickDelta, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        final Box bounds = entity.getBoundingBox();
        final Vec3d pos = entity.getPos();
        matrices.translate(-pos.x, -pos.y, -pos.z);
        WorldRenderer.drawBox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), bounds.expand(0, 1 - bounds.getYLength(), 0).offset(0, pos.y - bounds.getYLength(), 0), 0.5f, 0.5f, 0.5f, 1.f);
        matrices.translate(pos.x, pos.y, pos.z);
        final DustParticleEffect dustParticleEffect = new DustParticleEffect(new Vec3f(1, 1, 1), 1);
        if (entity.getNavigation() != null && entity.getNavigation().getCurrentPath() != null) {
            final AIPath path = entity.getNavigation().getCurrentPath();
            for (final AIPathNode node : path.getNodes()) {
                if (node != null) {
                    entity.world.addParticle(dustParticleEffect, node.x+0.5, node.y, node.z+0.5, 0, 0, 0);
                }
            }
        }
    }

    @Override
    public Identifier getTexture(final TestEntity entity) {
        return VAA.createId("test");
    }
}
