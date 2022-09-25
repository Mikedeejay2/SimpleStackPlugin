package com.mikedeejay2.simplestack.gui.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIConstructor;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractHandlerDefault;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimDecoratorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.gui.debug.GUIDebugOpenerModule;
import com.mikedeejay2.simplestack.gui.modules.GUIConfigModule;
import org.bukkit.Material;

import static com.mikedeejay2.mikedeejay2lib.gui.util.SlotMatcher.*;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification.Position;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification.Style;

/**
 * Miscellaneous static methods to create GUIs or GUI-related objects.
 *
 * @author Mikedeejay2
 */
public class GUIConfigConstructor implements GUIConstructor {
    public static final GUIConfigConstructor INSTANCE = new GUIConfigConstructor(SimpleStack.getInstance());

    private static final AnimatedGUIItem ANIMATED_GUI_ITEM =
        new AnimatedGUIItem(ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).setEmptyName().get(), true)
            .addFrame(ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).setEmptyName().get(), 10)
            .addFrame(ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE).setEmptyName().get(), 10)
            .addFrame(ItemBuilder.of(Material.MAGENTA_STAINED_GLASS_PANE).setEmptyName().get(), 10)
            .addFrame(ItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setEmptyName().get(), 10)
            .addFrame(ItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).setEmptyName().get(), 10)
            .addFrame(ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE).setEmptyName().get(), 10);

    private final SimpleStack plugin;

    private GUIConfigConstructor(SimpleStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = new GUIContainer(plugin, Text.of("simplestack.gui.config.title"), 5);
        GUIAnimationModule animation = new GUIAnimationModule(plugin, 1);
        GUIAnimDecoratorModule outlineModule = new GUIAnimDecoratorModule(
            inRange(1, 1, 5, 1).or(inRange(1, 9, 5, 9)),
            ANIMATED_GUI_ITEM, new AnimationSpecification(Position.TOP_LEFT, Style.ROW));
        GUINavigatorModule naviModule = new GUINavigatorModule(plugin, "config");
        GUIConfigModule configModule = new GUIConfigModule(plugin);
        GUIDebugOpenerModule debugModule = new GUIDebugOpenerModule(plugin);
        gui.addModule(animation);
        gui.addModule(outlineModule);
        gui.addModule(naviModule);
        gui.addModule(configModule);
        gui.addModule(debugModule);
        GUIInteractHandler handler = new GUIInteractHandlerDefault(64);
        gui.setInteractionHandler(handler);

        return gui;
    }
}
