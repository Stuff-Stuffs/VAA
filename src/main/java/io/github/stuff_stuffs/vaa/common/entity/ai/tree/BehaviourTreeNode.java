package io.github.stuff_stuffs.vaa.common.entity.ai.tree;

public interface BehaviourTreeNode<Context> {
    BehaviourState tick(Context context);

    void start(Context context);

    void stop(Context context);
}
