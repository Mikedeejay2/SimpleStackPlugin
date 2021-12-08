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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * A <tt>GUIEvent</tt> that changes the ground items stacking state in the
 * Simple Stack config.
 *
 * @author Mikedeejay2
 */
public class GUIGroundStackingEvent implements GUIEvent
{
    private final Simplestack plugin;

    public GUIGroundStackingEvent(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void execute(GUIEventInfo event)
    {
        Player   player   = (Player) event.getWhoClicked();
        GUILayer layer    = event.getGUI().getLayer(0);
        Config   config   = plugin.config();
        if(event.getClick() != ClickType.LEFT) return;
        int     slot      = event.getSlot();
        int     row       = layer.getRowFromSlot(slot);
        int     col       = layer.getColFromSlot(slot);
        GUIItem item      = layer.getItem(row, col);
        boolean newHopper = !config.processGroundItems();
        if(newHopper)
        {
            item.setItem(ItemBuilder.of(Base64Head.GREEN.get())
                             .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.config.ground_stacking_select"))
                             .setLore(
                                 "",
                                 "&a&l⊳ " + plugin.getLibLangManager().getText(player, "generic.enabled"),
                                 "&7  " + plugin.getLibLangManager().getText(player, "generic.disabled"))
                             .get());
        }
        else
        {
            item.setItem(ItemBuilder.of(Base64Head.RED.get())
                             .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.config.ground_stacking_select"))
                             .setLore(
                                 "",
                                 "&7  " + plugin.getLibLangManager().getText(player, "generic.enabled"),
                                 "&c&l⊳ " + plugin.getLibLangManager().getText(player, "generic.disabled"))
                             .get());
        }
        config.setGroundStacks(newHopper);
    }
}