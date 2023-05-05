package com.mikedeejay2.simplestack.gui.config.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIModifiedConfigModule implements GUIModule {
    public static final GUIModifiedConfigModule INSTANCE = new GUIModifiedConfigModule();

    @Override
    public void onClickedHead(InventoryClickEvent event, GUIContainer gui) {
        SimpleStackAPI.getConfig().setModified(true);
    }
}
