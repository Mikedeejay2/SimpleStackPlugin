package com.mikedeejay2.simplestack.gui.config.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIConstructor;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractHandlerDefault;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimDecoratorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimOutlineModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.gui.dev.modules.GUIDebugOpenerModule;
import com.mikedeejay2.simplestack.gui.config.modules.GUIConfigModule;
import org.bukkit.Material;

import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification.Position;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification.Style;

/**
 * Miscellaneous static methods to create GUIs or GUI-related objects.
 *
 * @author Mikedeejay2
 */
public class GUIConfigConstructor extends GUIBaseConstructor {
    public static final GUIConfigConstructor INSTANCE = new GUIConfigConstructor(SimpleStack.getInstance());

    public static final AnimatedGUIItem ANIMATED_GUI_ITEM =
        new AnimatedGUIItem(ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).setEmptyName().get(), true)
            .addFrame(ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).setEmptyName().get(), 1)
            .addFrame(ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE).setEmptyName().get(), 1)
            .addFrame(ItemBuilder.of(Material.MAGENTA_STAINED_GLASS_PANE).setEmptyName().get(), 1)
            .addFrame(ItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setEmptyName().get(), 1)
            .addFrame(ItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).setEmptyName().get(), 1)
            .addFrame(ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE).setEmptyName().get(), 100);

    private GUIConfigConstructor(SimpleStack plugin) {
        super(plugin, Text.of("simplestack.gui.config.title"), 4);
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = super.get();
        GUIAnimationModule animation = new GUIAnimationModule(plugin, 1);
        GUIAnimDecoratorModule outlineModule = new GUIAnimOutlineModule(
            ANIMATED_GUI_ITEM, new AnimationSpecification(Position.of(2, 5), Style.CIRCULAR));
        GUINavigatorModule naviModule = new GUINavigatorModule(plugin, "config");
        GUIConfigModule configModule = new GUIConfigModule(plugin);
        GUIDebugOpenerModule debugModule = new GUIDebugOpenerModule(plugin);
        gui.addModule(outlineModule);
        gui.addModule(naviModule);
        gui.addModule(configModule);
        gui.addModule(debugModule);
        gui.addModule(animation);
        GUIInteractHandler handler = new GUIInteractHandlerDefault(64);
        gui.setInteractionHandler(handler);

        return gui;
    }
}
