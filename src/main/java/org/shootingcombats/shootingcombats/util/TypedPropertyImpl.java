package org.shootingcombats.shootingcombats.util;

import java.util.Objects;
import java.util.Optional;

public final class TypedPropertyImpl implements TypedProperty {
    private final Object value;

    public TypedPropertyImpl(Object value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public Class<?> getValueClass() {
        return  value.getClass();
    }

    @Override
    public <T> Optional<T> getValue(Class<T> typeToken) {
        return value.getClass().equals(typeToken) ? Optional.of(typeToken.cast(value)) : Optional.empty();
    }
}
