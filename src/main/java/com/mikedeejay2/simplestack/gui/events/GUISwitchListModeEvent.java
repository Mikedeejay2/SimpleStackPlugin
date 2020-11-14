package com.mikedeejay2.simplestack.gui.events;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Heads;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemCreator;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.config.ListMode;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUISwitchListModeEvent implements GUIEvent
{
    private final Simplestack plugin;

    public GUISwitchListModeEvent(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void execute(InventoryClickEvent event, GUIContainer gui)
    {
        ClickType type = event.getClick();
        if(type != ClickType.LEFT) return;
        GUILayer layer = gui.getLayer(0);
        Config config = plugin.config();
        int slot = event.getSlot();
        int row = layer.getRowFromSlot(slot);
        int col = layer.getColFromSlot(slot);
        GUIItem switchListMode = layer.getItem(row, col);
        config.setListMode(config.getListMode() == ListMode.BLACKLIST ? ListMode.WHITELIST : ListMode.BLACKLIST);
        if(plugin.config().getListMode() == ListMode.BLACKLIST)
        {
            switchListMode.setItem(ItemCreator.createHeadItem(Base64Heads.X_BLACK, 1, "&fBlacklist", "&7Click to toggle to whitelist"));
        }
        else
        {
            switchListMode.setItem(ItemCreator.createHeadItem(Base64Heads.CHECKMARK_WHITE, 1, "&fWhitelist", "&7Click to toggle to blacklist"));
        }
    }
}