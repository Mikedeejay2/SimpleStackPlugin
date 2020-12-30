package com.mikedeejay2.simplestack.system.itemclick.executors;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.itemclick.processes.*;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

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
        new ProcessPickupAll(event, plugin).invoke();
    }

    @Override
    public void execPickupSome(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessPickupSome(event, plugin).invoke();
    }

    @Override
    public void execPickupHalf(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessPickupHalf(event, plugin).invoke();
    }

    @Override
    public void execPickupOne(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessPickupOne(event, plugin).invoke();
    }

    @Override
    public void execPlaceAll(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessPlaceAll(event, plugin).invoke();
    }

    @Override
    public void execPlaceSome(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessPlaceSome(event, plugin).invoke();
    }

    @Override
    public void execPlaceOne(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessPlaceOne(event, plugin).invoke();
    }

    @Override
    public void execSwapWithCursor(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessSwapWithCursor(event, plugin).invoke();
    }

    @Override
    public void execDropAllCursor(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessDropAllCursor(event, plugin).invoke();
    }

    @Override
    public void execDropOneCursor(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessDropOneCursor(event, plugin).invoke();
    }

    @Override
    public void execDropAllSlot(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessDropAllSlot(event, plugin).invoke();
    }

    @Override
    public void execDropOneSlot(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessDropOneSlot(event, plugin).invoke();
    }

    @Override
    public void execMoveToOtherInventory(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessMoveToOtherInventory(event, plugin).invoke();
    }

    @Override
    public void execHotbarMoveAndReadd(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessHotbarMoveAndReadd(event, plugin).invoke();
    }

    @Override
    public void execHotbarSwap(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessHotbarSwap(event, plugin).invoke();
    }

    @Override
    public void execCloneStack(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessCloneStack(event, plugin).invoke();
    }

    @Override
    public void execCollectToCursor(InventoryAction action, InventoryClickEvent event)
    {
        new ProcessCollectToCursor(event, plugin).invoke();
    }

    @Override
    public void execUnknown(InventoryAction action, InventoryClickEvent event) {}
}
