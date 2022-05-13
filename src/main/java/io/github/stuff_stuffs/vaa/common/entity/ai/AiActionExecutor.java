package io.github.stuff_stuffs.vaa.common.entity.ai;

import io.github.stuff_stuffs.vaa.common.entity.AIEntity;
import io.github.stuff_stuffs.vaa.common.util.Watchable;
import org.jetbrains.annotations.Nullable;

public final class AiActionExecutor<T extends AIEntity> extends Watchable<AiActionExecutor.Watch<T>> {
    private Action<? super T> action;
    private final T entity;

    public AiActionExecutor(final T entity) {
        this.entity = entity;
    }

    public @Nullable Action<? super T> getAction() {
        return action;
    }

    public void setAction(final @Nullable Action<? super T> action) {
        if (action != this.action) {
            if (action != null) {
                iterate(w -> w.onActionCancel(this.action));
            }
            iterate(w -> w.onSetAction(this.action, action));
            this.action = action;
        }
    }

    public void cancel() {
        if (action != null) {
            iterate(w -> w.onActionCancel(action));
            action = null;
        }
    }

    public State tick() {
        if (action == null) {
            return State.DONE;
        } else {
            final State state = action.tick(entity);
            if (state != State.RUNNING) {
                if (state == State.DONE) {
                    iterate(w -> w.onActionFinish(action));
                } else {
                    iterate(w -> w.onActionCancel(action));
                }
                action = null;
            }
            return state;
        }
    }

    public interface Action<T extends AIEntity> {
        State tick(T entity);
    }

    public enum State {
        RUNNING,
        DONE,
        CANCELED
    }

    public interface Watch<T extends AIEntity> {
        void onSetAction(@Nullable Action<? super T> oldAction, @Nullable Action<? super T> newAction);

        void onActionFinish(Action<? super T> action);

        void onActionCancel(Action<? super T> action);
    }
}
