package com.mikedeejay2.simplestack.system.executors.itemclick;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.processes.itemclick.*;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemClickExecutor implements IItemClickExecutor
{
    protected final Simplestack plugin;

    public ItemClickExecutor(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void execNothing(InventoryAction action, InventoryClickEvent event) {}

    @Override
    public void execPickupAll(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessPickupAll(event, plugin).execute();
    }

    @Override
    public void execPickupSome(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessPickupSome(event, plugin).execute();
    }

    @Override
    public void execPickupHalf(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessPickupHalf(event, plugin).execute();
    }

    @Override
    public void execPickupOne(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessPickupOne(event, plugin).execute();
    }

    @Override
    public void execPlaceAll(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessPlaceAll(event, plugin).execute();
    }

    @Override
    public void execPlaceSome(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessPlaceSome(event, plugin).execute();
    }

    @Override
    public void execPlaceOne(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessPlaceOne(event, plugin).execute();
    }

    @Override
    public void execSwapWithCursor(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessSwapWithCursor(event, plugin).execute();
    }

    @Override
    public void execDropAllCursor(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessDropAllCursor(event, plugin).execute();
    }

    @Override
    public void execDropOneCursor(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessDropOneCursor(event, plugin).execute();
    }

    @Override
    public void execDropAllSlot(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessDropAllSlot(event, plugin).execute();
    }

    @Override
    public void execDropOneSlot(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessDropOneSlot(event, plugin).execute();
    }

    @Override
    public void execMoveToOtherInventory(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessMoveToOtherInventory(event, plugin).execute();
    }

    @Override
    public void execHotbarMoveAndReadd(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessHotbarMoveAndReadd(event, plugin).execute();
    }

    @Override
    public void execHotbarSwap(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessHotbarSwap(event, plugin).execute();
    }

    @Override
    public void execCloneStack(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessCloneStack(event, plugin).execute();
    }

    @Override
    public void execCollectToCursor(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessCollectToCursor(event, plugin).execute();
    }

    @Override
    public void execUnknown(InventoryAction action, InventoryClickEvent event) {}
}
