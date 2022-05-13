package io.github.stuff_stuffs.vaa.common.util;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;
import java.util.function.Consumer;

public class Watchable<T> {
    private final Map<Key, T> watchers = new Reference2ObjectOpenHashMap<>();

    public Key watch(final T watcher) {
        final Key key = new Key(this);
        watchers.put(key, watcher);
        return key;
    }

    public void destroyWatch(final Key key) {
        if (key.parent != this) {
            throw new RuntimeException("Tried to destroy watch with wrong parent");
        }
        watchers.remove(key);
    }

    protected void iterate(final Consumer<T> action) {
        watchers.values().forEach(action);
    }

    public static final class Key {
        private final Watchable<?> parent;

        public Key(final Watchable<?> parent) {
            this.parent = parent;
        }
    }
}
