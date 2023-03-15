package com.mikedeejay2.simplestack.bytebuddy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.ImmutablePair;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TypeConverterTest {
    private static final Map<String, Pair<List<Class<?>>, Class<?>>> validDescriptors = ImmutableMap.<String, Pair<List<Class<?>>, Class<?>>>builder()
        .put("test(IILjava/util/List;)Z", new ImmutablePair<>(ImmutableList.of(int.class, int.class, List.class), boolean.class))
        .put("ab(F)V", new ImmutablePair<>(ImmutableList.of(float.class), void.class))
        .put("method()V", new ImmutablePair<>(ImmutableList.of(), void.class))
        .put("method(IZFJDLjava/util/List;Ljava/util/Collection;)Ljava/util/Map;", new ImmutablePair<>(ImmutableList.of(int.class, boolean.class, float.class, long.class, double.class, List.class, Collection.class), Map.class))
        .put("hi()Ljava/util/List;", new ImmutablePair<>(ImmutableList.of(), List.class))
        .put("hi(Ljava/util/List;)Z", new ImmutablePair<>(ImmutableList.of(List.class), boolean.class))
        .put("arr([[I[D)V", new ImmutablePair<>(ImmutableList.of(int[][].class, double[].class), void.class))
        .put("arr()[[[I", new ImmutablePair<>(ImmutableList.of(), int[][][].class))
        .put("full(Ljava/util/List;II[Ljava/util/Collection;)[[[I", new ImmutablePair<>(ImmutableList.of(List.class, int.class, int.class, Collection[].class), int[][][].class))
        .put("full2([[[Ljava/util/List;[II[Ljava/util/Collection;)[Ljava/util/Map;", new ImmutablePair<>(ImmutableList.of(List[][][].class, int[].class, int.class, Collection[].class), Map[].class))
        .build();

    private static final Map<String, Class<? extends Exception>> invalidDescriptors = ImmutableMap.<String, Class<? extends Exception>>builder()
        .put("test(IILLjava/util/List;)Z", ClassNotFoundException.class) // No class name
        .put("ab(F)VI", IllegalArgumentException.class) // Two return values
        .put("method(I", IllegalArgumentException.class) // Missing end
        .put("method(IZFJDLjava/util/Listy;)Ljava/util/Map;", ClassNotFoundException.class) // Invalid class name
        .put("hi()Ljava/util/List", IllegalArgumentException.class) // No semicolon
        .put("hi(java/util/List;)Z", IllegalArgumentException.class) // Missing class
        .put("arr([[I[)V", IllegalArgumentException.class) // Trailing array
        .put("arr()[[[", IllegalArgumentException.class) // Trailing array
        .put("full(Ljava/util/List;II[Ljava/util/Collection;)", IllegalArgumentException.class) // No return value
        .put("[[[Ljava/util/List;[II[Ljava/util/Collection;)[Ljava/util/Map;", IllegalArgumentException.class) // No opening parenthesis
    .build();

    @Test
    void validDescriptorParameters() throws ClassNotFoundException {
        for(final String descriptor : validDescriptors.keySet()) {
            final List<Class<?>> expectedParams = validDescriptors.get(descriptor).getLeft();
            final List<Class<?>> actualParams = TypeConverter.convertParameterTypes(descriptor);
            assertThat(actualParams)
                .describedAs("Mismatched parameters for descriptor \"%s\"", descriptor)
                .isEqualTo(expectedParams);
        }
    }

    @Test
    void validDescriptorReturnValue() throws ClassNotFoundException {
        for(final String descriptor : validDescriptors.keySet()) {
            final Class<?> expectedReturn = validDescriptors.get(descriptor).getRight();
            final Class<?> actualReturn = TypeConverter.convertReturnType(descriptor);
            assertThat(actualReturn)
                .describedAs("Mismatched return values for descriptor \"%s\"", descriptor)
                .isEqualTo(expectedReturn);
        }
    }

    @Test
    void invalidDescriptors() {
        for(final String descriptor : invalidDescriptors.keySet()) {
            assertThatExceptionOfType(invalidDescriptors.get(descriptor))
                .describedAs("Exception expected for descriptor \"%s\"", descriptor)
                .isThrownBy(() -> {
                    TypeConverter.convertParameterTypes(descriptor);
                    TypeConverter.convertReturnType(descriptor);
                });
        }
    }
}