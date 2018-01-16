package org.springframework.contrib.gae.util;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public interface Nulls {

    static <I, O> O ifNotNull(@Nullable I source, Function<I, O> function) {
        return Optional.ofNullable(source)
                .map(function)
                .orElse(null);
    }

}
