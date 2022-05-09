package io.github.stuff_stuffs.vaa.common.entity.path.valid;

import io.github.stuff_stuffs.vaa.common.entity.path.open.OpenSetType;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class ValidLocationSetType<T> {
    private static final Map<Identifier, ValidLocationSetType<?>> TYPES = new Object2ReferenceOpenHashMap<>();
    private final Identifier id;
    private final UniverseInfo<T> universeInfo;
    private final ValidLocationSet.Validator<T> validator;
    private final OpenSetType openSetType;

    private ValidLocationSetType(final Identifier id, final UniverseInfo<T> universeInfo, final ValidLocationSet.Validator<T> validator, final OpenSetType openSetType) {
        this.id = id;
        this.validator = validator;
        this.openSetType = openSetType;
        this.universeInfo = universeInfo;
    }

    public UniverseInfo<T> getUniverseInfo() {
        return universeInfo;
    }

    public ValidLocationSet.Validator<T> getValidator() {
        return validator;
    }

    public OpenSetType getOpenSetType() {
        return openSetType;
    }

    public static ValidLocationSetType<?> get(final Identifier id) {
        return TYPES.get(id);
    }

    public static <T extends Enum<T>> ValidLocationSetType<T> register(final Identifier id, final UniverseInfo<T> universeInfo, final ValidLocationSet.Validator<T> validator, final OpenSetType openSetType) {
        final ValidLocationSetType<T> type = new ValidLocationSetType<>(id, universeInfo, validator, openSetType);
        TYPES.put(id, type);
        return type;
    }

    @Override
    public String toString() {
        return "ValidLocationSetType{" + "id=" + id + '}';
    }

    public interface UniverseInfo<T> {
        T getDefaultValue();

        int toInt(T value);

        T fromInt(int value);

        int getUniverseSize();
    }
}
