package io.github.stuff_stuffs.vaa.common.entity.ai.tree;

public final class BehaviourTree<Context> {
    private final BehaviourTreeNode<Context> root;
    private boolean mustRestart = true;
    private boolean init = false;

    public BehaviourTree(final BehaviourTreeNode<Context> root) {
        this.root = root;
    }

    public void tick(final Context context) {
        if (mustRestart) {
            if (init) {
                root.stop(context);
                init = true;
            }
            root.start(context);
            mustRestart = false;
        }
        if (root.tick(context) != BehaviourState.RUNNING) {
            mustRestart = true;
        }
    }
}
