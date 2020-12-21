package com.mikedeejay2.simplestack.gui.events;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Heads;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemCreator;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * A <tt>GUIEvent</tt> that changes whether hopper movement should be processed by
 * Simple Stack in the config.
 *
 * @author Mikedeejay2
 */
public class GUIHopperMovementEvent implements GUIEvent
{
    private final Simplestack plugin;

    public GUIHopperMovementEvent(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void execute(InventoryClickEvent event, GUIContainer gui)
    {
        Player player = (Player) event.getWhoClicked();
        GUILayer layer = gui.getLayer(0);
        Config config = plugin.config();
        if(event.getClick() != ClickType.LEFT) return;
        int slot = event.getSlot();
        int row = layer.getRowFromSlot(slot);
        int col = layer.getColFromSlot(slot);
        GUIItem item = layer.getItem(row, col);
        boolean newHopper = !config.shouldProcessHoppers();
        if(newHopper)
        {
            item.setItem(ItemCreator.createHeadItem(Base64Heads.GREEN, 1,
                    "&b&l" + plugin.langManager().getText(player, "simplestack.gui.config.hopper_move_select"),
                    "",
                    "&a&l⊳ " + plugin.langManager().getTextLib(player, "generic.enabled"),
                    "&7  " + plugin.langManager().getTextLib(player, "generic.disabled")));
        }
        else
        {
            item.setItem(ItemCreator.createHeadItem(Base64Heads.RED, 1,
                    "&b&l" + plugin.langManager().getText(player, "simplestack.gui.config.hopper_move_select"),
                    "",
                    "&7  " + plugin.langManager().getTextLib(player, "generic.enabled"),
                    "&c&l⊳ " + plugin.langManager().getTextLib(player, "generic.disabled")));
        }
        config.setHopperMovement(newHopper);
    }
}
