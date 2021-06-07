package com.mikedeejay2.simplestack.system.itemdrag;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public final class ItemDragInfo
{
    public final Simplestack plugin;
    public final ItemStack oldCursor;
    public final ItemStack newCursor;
    public final Inventory inventory;
    public final InventoryView invView;
    public final Player player;

    private ItemDragType type;

    public ItemDragInfo(InventoryDragEvent event, Simplestack plugin)
    {
        this.plugin = plugin;
        this.oldCursor = event.getOldCursor();
        this.newCursor = event.getCursor();
        this.inventory = event.getInventory();
        this.invView = event.getView();
        this.player = (Player) event.getWhoClicked();
        this.type = ItemDragType.valueOf(event.getType().toString());
    }

    public ItemDragType getType()
    {
        return type;
    }

    public void setType(ItemDragType type)
    {
        this.type = type;
    }
}
