package com.hayden.tracing;

import com.hayden.tracing.observation_aspects.ArgumentExtractor;
import com.hayden.utilitymodule.MapFunctions;
import org.aspectj.lang.ProceedingJoinPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaReflectionArgumentExtractor implements ArgumentExtractor {

    // TODO: Load class matchers to extract args and signatures. For example DataSource connection.


    @NotNull
    @Override
    public Map<String, Object> extract(@NotNull ProceedingJoinPoint proceeding) {
        return MapFunctions.CollectMap(
                Arrays.stream(proceeding.getArgs())
                        .flatMap(arg -> this.extractRecursive(arg, null, 3, 0).entrySet().stream())
        );

    }
    public Map<String, Object> extractRecursive(Object obj) {
        return extractRecursive(obj, null, 3, 0);
    }


    public Map<String, Object> extractRecursive(Object obj, int maxDepth, int depth) {
        return extractRecursive(obj, null, maxDepth, depth);
    }

    public Map<String, Object> extractRecursive(Object obj, @Nullable String name, int maxDepth, int depth) {
        return Optional.ofNullable(obj)
                .stream()
                .map(objCreated -> {
                    return Map.entry(
                            name == null
                                    ? objCreated.getClass().getSimpleName()
                                    : "%s.%s".formatted(objCreated.getClass().getSimpleName(), name),
                            depth <= maxDepth
                                     ? Arrays.stream(objCreated.getClass().getDeclaredFields())
                                        .filter(AccessibleObject::trySetAccessible)
                                        .filter(f -> Arrays.stream(f.getAnnotations()).anyMatch(a -> a.annotationType().equals(Logged.class)))
                                        .flatMap(f -> {
                                            try {
                                                return Optional.ofNullable(f.get(obj))
                                                        .stream()
                                                        .flatMap(obj1 -> extractRecursive(obj1, f.getName(), maxDepth, depth + 1).entrySet().stream());
                                            } catch (IllegalAccessException ignored) {
                                                return Stream.empty();
                                            }
                                        })
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1 + ", " + k2))
                                    : objCreated
                    );
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1));
    }

}
