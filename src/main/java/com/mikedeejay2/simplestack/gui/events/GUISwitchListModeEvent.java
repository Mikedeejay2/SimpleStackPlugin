package com.mikedeejay2.simplestack.gui.events;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIEventInfo;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.config.ListMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * A <tt>GUIEvent</tt> that switches the list mode in the config to another
 * mode.
 *
 * @author Mikedeejay2
 */
public class GUISwitchListModeEvent implements GUIEvent
{
    private final Simplestack plugin;

    public GUISwitchListModeEvent(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void execute(GUIEventInfo event)
    {
        Player    player        = (Player) event.getWhoClicked();
        ClickType type          = event.getClick();
        if(type != ClickType.LEFT) return;
        GUILayer layer          = event.getGUI().getLayer(0);
        Config   config         = plugin.config();
        int      slot           = event.getSlot();
        int      row            = layer.getRowFromSlot(slot);
        int      col            = layer.getColFromSlot(slot);
        GUIItem  switchListMode = layer.getItem(row, col);
        config.setListMode(config.getListMode() == ListMode.BLACKLIST ? ListMode.WHITELIST : ListMode.BLACKLIST);
        if(plugin.config().getListMode() == ListMode.BLACKLIST)
        {
            switchListMode.setItem(ItemBuilder.of(Base64Head.X_BLACK.get())
                                       .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.list_type.blacklist"))
                                       .setLore(
                                           "&7" + plugin.getLangManager().getText(player, "simplestack.gui.item_types.change_mode_whitelist"),
                                           "",
                                           "&a&l⊳ " + plugin.getLangManager().getText(player, "simplestack.list_type.blacklist"),
                                           "&7  " + plugin.getLangManager().getText(player, "simplestack.list_type.whitelist"))
                                       .get());
        }
        else
        {
            switchListMode.setItem(ItemBuilder.of(Base64Head.CHECKMARK_WHITE.get())
                                       .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.list_type.whitelist"))
                                       .setLore(
                                           "&7" + plugin.getLangManager().getText(player, "simplestack.gui.item_types.change_mode_blacklist"),
                                           "",
                                           "&7  " + plugin.getLangManager().getText(player, "simplestack.list_type.blacklist"),
                                           "&a&l⊳ " + plugin.getLangManager().getText(player, "simplestack.list_type.whitelist"))
                                       .get());
        }
    }
}