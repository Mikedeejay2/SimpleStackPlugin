package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.mikedeejay2lib.text.language.TranslationManager;
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

import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MappingsLookupTest {
    private static SimpleStack plugin;

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
    }
}