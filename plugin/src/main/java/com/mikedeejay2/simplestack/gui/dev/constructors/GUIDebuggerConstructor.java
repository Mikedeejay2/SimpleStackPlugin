package com.mikedeejay2.simplestack.gui.dev.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIConstructor;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification.Position;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification.Style;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimOutlineModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.gui.config.constructors.GUIBaseConstructor;
import com.mikedeejay2.simplestack.gui.dev.modules.GUIDebugSettingsModule;
import org.bukkit.Material;

public class GUIDebuggerConstructor extends GUIBaseConstructor {
    public static final AnimatedGUIItem OUTLINE_ITEM = new AnimatedGUIItem(
        ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).setEmptyName().get(), true)
        .addFrame(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).setEmptyName().get(), 20)
        .addFrame(ItemBuilder.of(Material.RED_STAINED_GLASS_PANE).setEmptyName().get(), 1)
        .addFrame(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).setEmptyName().get(), 100);

    private final SimpleStack plugin;

    public GUIDebuggerConstructor(SimpleStack plugin) {
        super(plugin, Text.of("&cDebug Menu").color(), 3);
        this.plugin = plugin;
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = super.get();
        gui.addModule(new GUIAnimationModule(plugin, 1));
        gui.addModule(new GUIAnimOutlineModule(
            OUTLINE_ITEM, new AnimationSpecification(Position.TOP_LEFT, Style.COL)));
        gui.addModule(new GUIDebugSettingsModule(plugin));

        return gui;
    }
}
