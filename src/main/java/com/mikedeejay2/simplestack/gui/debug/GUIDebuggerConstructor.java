package com.mikedeejay2.simplestack.gui.debug;

import com.mikedeejay2.mikedeejay2lib.gui.GUIConstructor;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.GUIAnimPattern;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimOutlineModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;

public class GUIDebuggerConstructor implements GUIConstructor
{
    private static final AnimatedGUIItem outlineItem = new AnimatedGUIItem(
            ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).setEmptyName().get(), true)
            .addFrame(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).setEmptyName().get(), 20)
            .addFrame(ItemBuilder.of(Material.RED_STAINED_GLASS_PANE).setEmptyName().get(), 1)
            .addFrame(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).setEmptyName().get(), 100);

    private final Simplestack plugin;

    public GUIDebuggerConstructor(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public GUIContainer get()
    {
        GUIContainer gui = new GUIContainer(plugin, "&cDebug Menu", 3);
        gui.addModule(new GUIAnimationModule(plugin, 1));
        gui.addModule(new GUIAnimOutlineModule(outlineItem, GUIAnimPattern.LEFT_RIGHT));
        gui.addModule(new GUIDebugSettingsModule(plugin.getDebugConfig()));

        return gui;
    }
}
