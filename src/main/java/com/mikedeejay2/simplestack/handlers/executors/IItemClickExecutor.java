package com.mikedeejay2.simplestack.handlers.executors;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface IItemClickExecutor
{
    void execNothing(InventoryAction action, InventoryClickEvent event);
    void execPickupAll(InventoryAction action, InventoryClickEvent event);
    void execPickupSome(InventoryAction action, InventoryClickEvent event);
    void execPickupHalf(InventoryAction action, InventoryClickEvent event);
    void execPickupOne(InventoryAction action, InventoryClickEvent event);
    void execPlaceAll(InventoryAction action, InventoryClickEvent event);
    void execPlaceSome(InventoryAction action, InventoryClickEvent event);
    void execPlaceOne(InventoryAction action, InventoryClickEvent event);
    void execSwapWithCursor(InventoryAction action, InventoryClickEvent event);
    void execDropAllCursor(InventoryAction action, InventoryClickEvent event);
    void execDropOneCursor(InventoryAction action, InventoryClickEvent event);
    void execDropAllSlot(InventoryAction action, InventoryClickEvent event);
    void execDropOneSlot(InventoryAction action, InventoryClickEvent event);
    void execMoveToOtherInventory(InventoryAction action, InventoryClickEvent event);
    void execHotbarMoveAndReadd(InventoryAction action, InventoryClickEvent event);
    void execHotbarSwap(InventoryAction action, InventoryClickEvent event);
    void execCloneStack(InventoryAction action, InventoryClickEvent event);
    void execCollectToCursor(InventoryAction action, InventoryClickEvent event);
    void execUnknown(InventoryAction action, InventoryClickEvent event);
}
