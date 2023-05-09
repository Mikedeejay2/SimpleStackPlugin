package com.mikedeejay2.simplestack.gui.config.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.config.ItemMap;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl;
import org.bukkit.entity.Player;

public class GUIModifiedConfigModule implements GUIModule {
    public static final GUIModifiedConfigModule INSTANCE = new GUIModifiedConfigModule();

    private final SimpleStackConfigImpl config = ((SimpleStackConfigImpl) SimpleStackAPI.getConfig());
    private final ItemMap itemMap = config.getItemMap();
    private int itemMapHash;

    @Override
    public void onOpenHead(Player player, GUIContainer gui) {
        itemMapHash = itemMap.hashCode();
    }

    @Override
    public void onClose(Player player, GUIContainer gui) {
        if(itemMapHash == itemMap.hashCode()) return;
        itemMap.buildMaps();
        config.setItemsModified(true);
    }
}
