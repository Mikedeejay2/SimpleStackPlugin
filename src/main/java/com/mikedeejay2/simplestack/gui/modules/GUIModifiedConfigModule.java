package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.simplestack.SimpleStack;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIModifiedConfigModule implements GUIModule {
    public static final GUIModifiedConfigModule INSTANCE = new GUIModifiedConfigModule(SimpleStack.getInstance());

    private final SimpleStack plugin;

    private GUIModifiedConfigModule(SimpleStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onClickedHead(InventoryClickEvent event, GUIContainer gui) {
        plugin.config().setModified(true);
    }
}
