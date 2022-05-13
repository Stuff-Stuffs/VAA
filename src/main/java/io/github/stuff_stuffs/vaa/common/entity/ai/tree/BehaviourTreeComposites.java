package io.github.stuff_stuffs.vaa.common.entity.ai.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public final class BehaviourTreeComposites {
    public static <Context> BehaviourTreeNode<Context> sequence(final List<? extends BehaviourTreeNode<? super Context>> nodes, final Function<Context, ? extends ObjectStorage<SequenceData>> storageGetter) {
        final BehaviourTreeNode<? super Context>[] array = nodes.toArray(BehaviourTreeNode[]::new);
        return new BehaviourTreeNode<>() {
            @Override
            public BehaviourState tick(final Context context) {
                final ObjectStorage<SequenceData> storage = storageGetter.apply(context);
                final SequenceData data = storage.get();
                final int index = data.index();
                if (index < 0) {
                    return BehaviourState.FAILED;
                }
                if (index == array.length - 1) {
                    return BehaviourState.FINISHED;
                }
                if (!data.inProgress()) {
                    array[index].start(context);
                }
                final BehaviourState state = array[index].tick(context);
                if (state == BehaviourState.RUNNING) {
                    return BehaviourState.RUNNING;
                }
                if (state == BehaviourState.FINISHED) {
                    array[index].stop(context);
                    final int nextIndex = index + 1;
                    if (nextIndex == array.length) {
                        return BehaviourState.FINISHED;
                    } else {
                        storage.set(new SequenceData(nextIndex, false));
                        return BehaviourState.RUNNING;
                    }
                }
                storage.set(new SequenceData(-1, false));
                array[index].stop(context);
                return BehaviourState.FAILED;
            }

            @Override
            public void start(final Context context) {
                final ObjectStorage<SequenceData> storage = storageGetter.apply(context);
                storage.set(SequenceData.DEFAULT);
            }

            @Override
            public void stop(final Context context) {
                final ObjectStorage<SequenceData> storage = storageGetter.apply(context);
                final SequenceData data = storage.get();
                if (data.inProgress()) {
                    array[data.index()].stop(context);
                }
            }
        };
    }

    public record SequenceData(int index, boolean inProgress) {
        public static final SequenceData DEFAULT = new SequenceData(0, false);
    }

    public static <Context> BehaviourTreeNode<Context> selector(final boolean propagateFailure, final List<? extends BehaviourTreeNode<? super Context>> nodes, final Function<? super Context, ObjectStorage<SelectorData>> storageGetter) {
        final BehaviourTreeNode<? super Context>[] array = nodes.toArray(BehaviourTreeNode[]::new);
        return new BehaviourTreeNode<>() {
            @Override
            public BehaviourState tick(final Context context) {
                final ObjectStorage<SelectorData> storage = storageGetter.apply(context);
                final SelectorData data = storage.get();
                final int index = data.index();
                if (index >= 0) {
                    final BehaviourState state = array[index].tick(context);
                    if (propagateFailure && state == BehaviourState.FAILED) {
                        return BehaviourState.FAILED;
                    }
                    return state == BehaviourState.FAILED ? BehaviourState.FINISHED : state;
                } else if (index == SelectorData.FAILED.index()) {
                    return BehaviourState.FAILED;
                } else {
                    for (int i = 0; i < array.length; i++) {
                        array[i].start(context);
                        final BehaviourState state = array[i].tick(context);
                        if (state != BehaviourState.FAILED) {
                            storage.set(new SelectorData(i));
                            return state;
                        } else {
                            array[i].stop(context);
                        }
                    }
                }
                storage.set(SelectorData.FAILED);
                return BehaviourState.FAILED;
            }

            @Override
            public void start(final Context context) {
                final ObjectStorage<SelectorData> storage = storageGetter.apply(context);
                storage.set(SelectorData.DEFAULT);
            }

            @Override
            public void stop(final Context context) {
                final ObjectStorage<SelectorData> storage = storageGetter.apply(context);
                final SelectorData data = storage.get();
                final int index = data.index();
                if (index >= 0) {
                    array[index].stop(context);
                }
            }
        };
    }

    public record SelectorData(int index) {
        public static final SelectorData DEFAULT = new SelectorData(-1);
        public static final SelectorData FAILED = new SelectorData(-2);
    }

    public static <Context> UtilityNodeBuilder<Context> utilityBuilder() {
        return new UtilityNodeBuilder<>();
    }

    public static final class UtilityNodeBuilder<Context> {
        private final List<Entry<Context>> entries = new ArrayList<>();

        private UtilityNodeBuilder() {
        }

        public UtilityNodeBuilder<Context> add(final BehaviourTreeNode<? super Context> node, final ToDoubleFunction<? super Context> utilityScorer) {
            entries.add(new Entry<>(node, utilityScorer));
            return this;
        }

        public BehaviourTreeNode<Context> buildOnceUtility(final boolean propagateFailure, final Function<? super Context, ObjectStorage<UtilityData>> storageGetter) {
            final Entry<Context>[] arr = entries.toArray(Entry[]::new);
            return new BehaviourTreeNode<>() {
                @Override
                public BehaviourState tick(final Context context) {
                    final ObjectStorage<UtilityData> storage = storageGetter.apply(context);
                    final UtilityData data = storage.get();
                    if (data.index() != UtilityData.DEFAULT.index()) {
                        final BehaviourState state = arr[data.index()].node.tick(context);
                        if (propagateFailure && state == BehaviourState.FAILED) {
                            return BehaviourState.FAILED;
                        }
                        return state == BehaviourState.FAILED ? BehaviourState.FINISHED : state;
                    }
                    return BehaviourState.FAILED;
                }

                @Override
                public void start(final Context context) {
                    double maxScore = Double.NEGATIVE_INFINITY;
                    int bestIndex = UtilityData.DEFAULT.index();
                    for (int i = 0; i < arr.length; i++) {
                        final double score = arr[i].utilityScorer.applyAsDouble(context);
                        if (score > maxScore) {
                            maxScore = score;
                            bestIndex = i;
                        }
                    }
                    final ObjectStorage<UtilityData> storage = storageGetter.apply(context);
                    if (maxScore > 0 && bestIndex != UtilityData.DEFAULT.index()) {
                        arr[bestIndex].node.start(context);
                        storage.set(new UtilityData(bestIndex));
                    } else {
                        storage.set(UtilityData.DEFAULT);
                    }
                }

                @Override
                public void stop(final Context context) {
                    final ObjectStorage<UtilityData> storage = storageGetter.apply(context);
                    final UtilityData data = storage.get();
                    if (data.index() != UtilityData.DEFAULT.index()) {
                        arr[data.index()].node.stop(context);
                    }
                }
            };
        }

        public BehaviourTreeNode<Context> buildContinuousUtility(final boolean propagateFailure, final Function<? super Context, ObjectStorage<UtilityData>> storageGetter) {
            final Entry<Context>[] arr = entries.toArray(Entry[]::new);
            return new BehaviourTreeNode<>() {
                @Override
                public BehaviourState tick(final Context context) {
                    double maxScore = Double.NEGATIVE_INFINITY;
                    int bestIndex = UtilityData.DEFAULT.index();
                    for (int i = 0; i < arr.length; i++) {
                        final double score = arr[i].utilityScorer.applyAsDouble(context);
                        if (score > maxScore) {
                            maxScore = score;
                            bestIndex = i;
                        }
                    }
                    final int effectiveIndex = maxScore > 0 ? bestIndex : UtilityData.DEFAULT.index();
                    final ObjectStorage<UtilityData> storage = storageGetter.apply(context);
                    final UtilityData data = storage.get();
                    if (effectiveIndex != data.index()) {
                        if (data.index() != UtilityData.DEFAULT.index()) {
                            arr[data.index()].node.stop(context);
                        }
                        if (effectiveIndex != UtilityData.DEFAULT.index()) {
                            arr[data.index()].node.start(context);
                        }
                    }
                    if (effectiveIndex != UtilityData.DEFAULT.index()) {
                        final BehaviourState state = arr[bestIndex].node.tick(context);
                        if (propagateFailure && state == BehaviourState.FAILED) {
                            return BehaviourState.FAILED;
                        }
                        return state != BehaviourState.FAILED ? state : BehaviourState.FINISHED;
                    } else {
                        storage.set(UtilityData.DEFAULT);
                        return BehaviourState.FAILED;
                    }
                }

                @Override
                public void start(final Context context) {
                    final ObjectStorage<UtilityData> storage = storageGetter.apply(context);
                    storage.set(UtilityData.DEFAULT);
                }

                @Override
                public void stop(final Context context) {
                    final ObjectStorage<UtilityData> storage = storageGetter.apply(context);
                    final UtilityData data = storage.get();
                    if (data.index() != UtilityData.DEFAULT.index()) {
                        arr[data.index()].node.stop(context);
                    }
                }
            };
        }

        private static final class Entry<Context> {
            private final BehaviourTreeNode<? super Context> node;
            private final ToDoubleFunction<? super Context> utilityScorer;

            private Entry(final BehaviourTreeNode<? super Context> node, final ToDoubleFunction<? super Context> utilityScorer) {
                this.node = node;
                this.utilityScorer = utilityScorer;
            }
        }
    }

    public record UtilityData(int index) {
        public static final UtilityData DEFAULT = new UtilityData(-1);
    }

    private BehaviourTreeComposites() {
    }
}
