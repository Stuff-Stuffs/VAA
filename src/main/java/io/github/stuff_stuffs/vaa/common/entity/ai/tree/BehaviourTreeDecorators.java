package io.github.stuff_stuffs.vaa.common.entity.ai.tree;

import java.util.function.Function;
import java.util.function.Predicate;

public final class BehaviourTreeDecorators {
    public static <Context> BehaviourTreeNode<Context> onceCondition(final boolean propagateFailure, final BehaviourTreeNode<? super Context> node, final Predicate<? super Context> predicate, final Function<? super Context, ObjectStorage<ConditionData>> storageGetter) {
        return new BehaviourTreeNode<>() {
            @Override
            public BehaviourState tick(final Context context) {
                final ObjectStorage<ConditionData> storage = storageGetter.apply(context);
                final ConditionData data = storage.get();
                if (data.passed()) {
                    final BehaviourState state = node.tick(context);
                    if (propagateFailure && state == BehaviourState.FAILED) {
                        return BehaviourState.FAILED;
                    }
                    return state == BehaviourState.FAILED ? BehaviourState.FINISHED : state;
                }
                return BehaviourState.FAILED;
            }

            @Override
            public void start(final Context context) {
                final ObjectStorage<ConditionData> storage = storageGetter.apply(context);
                if (predicate.test(context)) {
                    storage.set(ConditionData.PASSED);
                    node.start(context);
                } else {
                    storage.set(ConditionData.FAILED);
                }
            }

            @Override
            public void stop(final Context context) {
                final ObjectStorage<ConditionData> storage = storageGetter.apply(context);
                final ConditionData data = storage.get();
                if (data.passed()) {
                    node.stop(context);
                }
                storage.set(ConditionData.FAILED);
            }
        };
    }

    public record ConditionData(boolean passed) {
        public static final ConditionData PASSED = new ConditionData(true);
        public static final ConditionData FAILED = new ConditionData(false);
    }

    public static <Context> BehaviourTreeNode<Context> repeat(final BehaviourTreeNode<? super Context> node) {
        return new BehaviourTreeNode<>() {
            @Override
            public BehaviourState tick(final Context context) {
                node.start(context);
                BehaviourState state;
                do {
                    state = node.tick(context);
                } while (state == BehaviourState.RUNNING);
                node.stop(context);
                return state;
            }

            @Override
            public void start(final Context context) {
            }

            @Override
            public void stop(final Context context) {
            }
        };
    }

    private BehaviourTreeDecorators() {
    }
}
