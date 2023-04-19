package org.shootingcombats.shootingcombats.util;

import java.util.Optional;

public interface TypedProperty {
    Class<?> getValueClass();
    <T> Optional<T> getValue(Class<T> typeToken);
}
