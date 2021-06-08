package com.mikedeejay2.simplestack.system.itemdrag;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public final class ItemDragInfo
{
    public final Simplestack plugin;
    public final Map<Integer, ItemStack> newItems;
    public final Set<Integer> rawSlots;
    public final Set<Integer> invSlots;
    public final ItemStack cursor;
    public final ItemStack oldCursor;
    public final Inventory inventory;
    public final InventoryView invView;
    public final Player player;
    public final DragType oldType;
    public final boolean cursorNull;

    private ItemDragType type;

    public ItemDragInfo(InventoryDragEvent event, Simplestack plugin)
    {
        this.plugin = plugin;
        this.oldCursor = event.getOldCursor();
        this.newItems = event.getNewItems();
        this.rawSlots = event.getRawSlots();
        this.invSlots = event.getInventorySlots();
        this.cursor = event.getCursor();
        this.inventory = event.getInventory();
        this.invView = event.getView();
        this.player = (Player) event.getWhoClicked();
        this.type = ItemDragType.NOTHING;
        this.oldType = event.getType();
        this.cursorNull = cursor == null;
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
