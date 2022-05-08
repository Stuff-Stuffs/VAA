package io.github.stuff_stuffs.vaa.common.path.valid;

import io.github.stuff_stuffs.vaa.common.path.open.OpenSetType;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class ValidLocationSetType {
    private static final Map<Identifier, ValidLocationSetType> TYPES = new Object2ReferenceOpenHashMap<>();
    private final Identifier id;
    private final ValidLocationSet.Validator validator;
    private final OpenSetType openSetType;

    private ValidLocationSetType(final Identifier id, final ValidLocationSet.Validator validator, final OpenSetType openSetType) {
        this.id = id;
        this.validator = validator;
        this.openSetType = openSetType;
    }

    public ValidLocationSet.Validator getValidator() {
        return validator;
    }

    public OpenSetType getOpenSetType() {
        return openSetType;
    }

    public static ValidLocationSetType get(final Identifier id) {
        return TYPES.get(id);
    }

    public static ValidLocationSetType register(final Identifier id, final ValidLocationSet.Validator validator, final OpenSetType openSetType) {
        final ValidLocationSetType type = new ValidLocationSetType(id, validator, openSetType);
        TYPES.put(id, type);
        return type;
    }

    @Override
    public String toString() {
        return "ValidLocationSetType{" + "id=" + id + '}';
    }
}
