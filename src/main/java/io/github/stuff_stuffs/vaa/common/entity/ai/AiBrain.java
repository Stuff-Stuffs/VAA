package io.github.stuff_stuffs.vaa.common.entity.ai;

import io.github.stuff_stuffs.vaa.common.entity.AIEntity;
import io.github.stuff_stuffs.vaa.common.entity.ai.tree.BehaviourTree;

import java.util.function.Function;

public class AiBrain<T extends AIEntity, Context> {
    private final T entity;
    private final BehaviourTree<Context> tree;
    private final Function<? super T, ? extends Context> contextExtractor;

    public AiBrain(final T entity, final BehaviourTree<Context> tree, final Function<? super T, ? extends Context> contextExtractor) {
        this.entity = entity;
        this.tree = tree;
        this.contextExtractor = contextExtractor;
    }

    public void tick() {
        tree.tick(contextExtractor.apply(entity));
    }
}
