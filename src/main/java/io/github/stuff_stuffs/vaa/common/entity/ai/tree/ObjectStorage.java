package io.github.stuff_stuffs.vaa.common.entity.ai.tree;

public interface ObjectStorage<T> {
    T get();

    void set(T val);
}
