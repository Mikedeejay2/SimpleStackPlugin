package com.mikedeejay2.simplestack.gui.events;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Heads;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemCreator;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

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
        GUILayer layer = gui.getLayer(0);
        Config config = plugin.config();
        if(event.getClick() != ClickType.LEFT) return;
        int slot = event.getSlot();
        int row = layer.getRowFromSlot(slot);
        int col = layer.getColFromSlot(slot);
        GUIItem item = layer.getItem(row, col);
        boolean newHopper = !config.isHopperMovement();
        if(newHopper)
        {
            item.setItem(ItemCreator.createHeadItem(Base64Heads.GREEN, 1,
                    "&fStack Hopper Movements",
                    "&aEnabled"));
        }
        else
        {
            item.setItem(ItemCreator.createHeadItem(Base64Heads.RED, 1,
                    "&fStack Hopper Movements",
                    "&cDisabled"));
        }
        config.setHopperMovement(newHopper);
    }
}
