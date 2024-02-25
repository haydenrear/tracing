package com.hayden.tracing;

import com.hayden.tracing.observation_aspects.ArgumentExtractor;
import com.hayden.tracing.observation_aspects.AnnotationRegistrarObservabilityUtility;
import com.hayden.utilitymodule.MapFunctions;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AccessibleObject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaReflectionArgumentExtractor implements ArgumentExtractor {

    // TODO: Load class matchers to extract args and signatures. For example DataSource connection.


    @NotNull
    @Override
    public Map<String, Object> extract(@NotNull JoinPoint proceeding,
                                       @NotNull AnnotationRegistrarObservabilityUtility utility) {
        if (utility.matchers().stream().anyMatch(u -> u.matches(proceeding)))
            return MapFunctions.CollectMap(
                    Arrays.stream(proceeding.getArgs())
                            .flatMap(arg -> this.extractRecursive(arg, null, 3, 0, utility).entrySet().stream())
            );
        return new HashMap<>();

    }
    public Map<String, Object> extractRecursive(Object obj, AnnotationRegistrarObservabilityUtility util) {
        return extractRecursive(obj, null, 3, 0, util);
    }


    public Map<String, Object> extractRecursive(Object obj, int maxDepth, int depth, AnnotationRegistrarObservabilityUtility util) {
        return extractRecursive(obj, null, maxDepth, depth, util);
    }

    public Map<String, Object> extractRecursive(Object obj,
                                                @Nullable String name,
                                                int maxDepth,
                                                int depth,
                                                AnnotationRegistrarObservabilityUtility util) {
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
                                        .filter(f -> util.matchers().stream().anyMatch(b -> b.matches(f)))
                                        .flatMap(f -> {
                                            try {
                                                return Optional.ofNullable(f.get(obj))
                                                        .filter(o -> util.matchers().stream().anyMatch(b -> b.matches(o)))
                                                        .stream()
                                                        .flatMap(obj1 -> extractRecursive(obj1, f.getName(), maxDepth, depth + 1, util)
                                                                .entrySet().stream()
                                                        );
                                            } catch (IllegalAccessException ignored) {
                                                return Stream.empty();
                                            }
                                        })
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1 + ", " + k2))
                                    : Optional.ofNullable(util.getSerializer(objCreated))
                                        .map(c -> c.doSerialize(objCreated))
                                        .orElse(objCreated.toString())
                    );
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1));
    }

}
