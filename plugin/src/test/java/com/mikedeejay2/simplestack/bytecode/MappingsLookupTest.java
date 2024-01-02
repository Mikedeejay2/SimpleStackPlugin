package com.mikedeejay2.simplestack.bytecode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mikedeejay2.mikedeejay2lib.text.language.TranslationManager;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.ImmutablePair;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.Pair;
import com.mikedeejay2.simplestack.SimpleStack;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.Warning;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.SimplePluginManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.*;
import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MappingsLookupTest {
    private static SimpleStack plugin;

    private static final Map<String, Pair<List<String>, List<String>>> classes = ImmutableMap.<String, Pair<List<String>, List<String>>>builder()
        .put("ArrayList", new ImmutablePair<>(ImmutableList.of("size", "contains", "addAll", "get", "indexOf"), ImmutableList.of("DEFAULT_CAPACITY", "elementData")))
        .put("Object", new ImmutablePair<>(ImmutableList.of("equals"), ImmutableList.of()))
        .build();

    private static final List<String> classNames = ImmutableList.of("ArrayList", "Object");

    private static final Map<String, Class<?>> classNameToClass = ImmutableMap.of(
        "ArrayList", ArrayList.class,
        "Object", Object.class);

    private static final Map<String, List<String>> classNameToMethodNames = ImmutableMap.of(
        "ArrayList", ImmutableList.of("size", "contains", "addAll2", "get"),
        "Object", ImmutableList.of("equals"));

    private static final Map<String, List<Pair<String, String>>> methodNameToName = ImmutableMap.of(
        "ArrayList", ImmutableList.of(
            new ImmutablePair<>("size", "size"),
            new ImmutablePair<>("contains", "contains"),
            new ImmutablePair<>("addAll2", "addAll"),
            new ImmutablePair<>("get", "get"),
            new ImmutablePair<>("indexOf", "indexOf")),
        "Object", ImmutableList.of(
            new ImmutablePair<>("equals", "equals"))
    );

    private static final Map<String, List<Pair<String, String>>> methodNameToDescriptor = ImmutableMap.of(
        "ArrayList", ImmutableList.of(
            new ImmutablePair<>("size", "()I"),
            new ImmutablePair<>("contains", "(Ljava/lang/Object;)Z"),
            new ImmutablePair<>("addAll2", "(ILjava/util/Collection;)Z"),
            new ImmutablePair<>("get", "(I)Ljava/lang/Object;"),
            new ImmutablePair<>("indexOf", "(Ljava/lang/Object;)I")),
        "Object", ImmutableList.of(
            new ImmutablePair<>("equals", "(Ljava/lang/Object;)Z"))
    );

    private static final Map<String, List<String>> classNameToFieldNames = ImmutableMap.of(
        "ArrayList", ImmutableList.of("DEFAULT_CAPACITY", "elementDataaa"));

    private static final Map<String, List<Pair<String, String>>> fieldNameToName = ImmutableMap.of(
        "ArrayList", ImmutableList.of(
            new ImmutablePair<>("DEFAULT_CAPACITY", "DEFAULT_CAPACITY"),
            new ImmutablePair<>("elementDataaa", "elementData"))
    );

    private static final Map<String, List<Pair<String, String>>> fieldNameToDescriptor = ImmutableMap.of(
        "ArrayList", ImmutableList.of(
            new ImmutablePair<>("DEFAULT_CAPACITY", "I"),
            new ImmutablePair<>("elementDataaa", "[Ljava/lang/Object;"))
    );

    @BeforeAll
    static void beforeAll() {
        plugin = Mockito.mock(SimpleStack.class);
        Mockito.when(plugin.classLoader()).thenReturn(plugin.getClass().getClassLoader());
        Mockito.when(plugin.getDescription()).thenReturn(new PluginDescriptionFile("SimpleStack", "test-ver", "na"));

        Server server = Mockito.mock(Server.class);
        Mockito.when(server.getVersion()).thenReturn("(MC: 1.0.0)");
        Mockito.when(server.getLogger()).thenReturn(Logger.getAnonymousLogger());
        Mockito.when(server.getPluginManager()).thenReturn(new SimplePluginManager(server, new SimpleCommandMap(server)));
        Mockito.when(server.getWarningState()).thenReturn(Warning.WarningState.DEFAULT);
        Bukkit.setServer(server);

        TranslationManager.GLOBAL.setPlugin(plugin);
        TranslationManager.GLOBAL.registerDirectory("lang/mikedeejay2lib", true);
    }

    @Test
    @Order(0)
    void loadMappings() {
        MappingsLookup.loadMappings(plugin);
        assertThat(MappingsLookup.hasMappings())
            .describedAs("MappingsLookup should have mappings")
            .isTrue();
    }

    @Test
    @Order(1)
    void validateMappings() {
        assertThat(MappingsLookup.validateMappings(plugin))
            .describedAs("MappingsLookup should successfully validate mappings")
            .isTrue();
    }

    @Test
    @Order(2)
    void getClassMappings() {
        classNames.forEach(e -> assertThat(nms(e))
            .describedAs("Retrieved class mapping is null")
            .isNotNull());
    }

    @Test
    @Order(2)
    void validateClassConversion() {
        classNameToClass.forEach((e, c) -> assertThat(nms(e).toClass())
            .describedAs("Converted class is not equal to expected class")
            .isEqualTo(c));
    }

    @Test
    @Order(2)
    void validateClassQualifiedName() {
        classNameToClass.forEach((e, c) -> assertThat(nms(e).qualifiedName())
            .describedAs("Retrieved qualified name is not equal to expected qualified name")
            .isEqualTo(c.getName()));
    }

    @Test
    @Order(2)
    void validateClassInternalName() {
        classNameToClass.forEach((e, c) -> assertThat(nms(e).internalName())
            .describedAs("Retrieved internal name is not equal to expected internal name")
            .isEqualTo(c.getName().replace('.', '/')));
    }

    @Test
    @Order(2)
    void validateClassDescriptor() {
        classNameToClass.forEach((e, c) -> assertThat(nms(e).descriptorName())
            .describedAs("Retrieved internal name is not equal to expected internal name")
            .isEqualTo("L" + c.getName().replace('.', '/') + ";"));
    }

    @Test
    @Order(2)
    void getMethodMappings() {
        classNameToMethodNames.forEach(
            (e, methods) -> methods.forEach(m -> assertThat(nms(e).method(m))
            .describedAs("Retrieved method mapping is null")
            .isNotNull()));
    }

    @Test
    @Order(2)
    void validateMethodNames() {
        methodNameToName.forEach(
            (e, methods) -> methods.forEach(m -> assertThat(nms(e).method(m.getLeft()).name())
            .describedAs("Retrieved method mapping name does not equal expected name")
            .isEqualTo(m.getRight())));
    }

    @Test
    @Order(2)
    void validateMethodDescriptors() {
        methodNameToDescriptor.forEach(
            (e, methods) -> methods.forEach(m -> assertThat(nms(e).method(m.getLeft()).descriptor())
            .describedAs("Retrieved method mapping descriptor does not equal expected descriptor")
            .isEqualTo(m.getRight())));
    }

    @Test
    @Order(2)
    void getFieldMappings() {
        classNameToFieldNames.forEach(
            (e, fields) -> fields.forEach(m -> assertThat(nms(e).field(m))
                .describedAs("Retrieved method mapping is null")
                .isNotNull()));
    }

    @Test
    @Order(2)
    void validateFieldNames() {
        fieldNameToName.forEach(
            (e, fields) -> fields.forEach(m -> assertThat(nms(e).field(m.getLeft()).name())
                .describedAs("Retrieved field mapping name does not equal expected name")
                .isEqualTo(m.getRight())));
    }

    @Test
    @Order(2)
    void validateFieldDescriptors() {
        fieldNameToDescriptor.forEach(
            (e, fields) -> fields.forEach(m -> assertThat(nms(e).field(m.getLeft()).descriptor())
                .describedAs("Retrieved field mapping descriptor does not equal expected descriptor")
                .isEqualTo(m.getRight())));
    }
}