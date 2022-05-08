package io.github.stuff_stuffs.vaa.common.path.open;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class OpenSetType {
    private static final Map<Identifier, OpenSetType> TYPES = new Object2ReferenceOpenHashMap<>();
    private final Identifier id;
    private final OpenSet.Classifier classifier;

    private OpenSetType(final Identifier id, final OpenSet.Classifier classifier) {
        this.id = id;
        this.classifier = classifier;
    }

    public OpenSet.Classifier getClassifier() {
        return classifier;
    }

    public static OpenSetType get(final Identifier id) {
        return TYPES.get(id);
    }

    public static OpenSetType register(final Identifier id, final OpenSet.Classifier classifier) {
        final OpenSetType type = new OpenSetType(id, classifier);
        TYPES.put(id, type);
        return type;
    }

    @Override
    public String toString() {
        return "OpenSetType{" + "id=" + id + '}';
    }
}
