package com.mikedeejay2.simplestack.system.itemclick.executors;

import com.mikedeejay2.simplestack.system.SimpleStackExecutor;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ItemClickExecutor extends SimpleStackExecutor
{
    void execNothing(ItemClickInfo info);
    void execPickupAll(ItemClickInfo info);
    void execPickupSome(ItemClickInfo info);
    void execPickupHalf(ItemClickInfo info);
    void execPickupOne(ItemClickInfo info);
    void execPlaceAll(ItemClickInfo info);
    void execPlaceSome(ItemClickInfo info);
    void execPlaceOne(ItemClickInfo info);
    void execSwapWithCursor(ItemClickInfo info);
    void execDropAllCursor(ItemClickInfo info);
    void execDropOneCursor(ItemClickInfo info);
    void execDropAllSlot(ItemClickInfo info);
    void execDropOneSlot(ItemClickInfo info);
    void execMoveToOtherInventory(ItemClickInfo info);
    void execHotbarMoveAndReadd(ItemClickInfo info);
    void execHotbarSwap(ItemClickInfo info);
    void execCloneStack(ItemClickInfo info);
    void execCollectToCursor(ItemClickInfo info);
    void execUnknown(ItemClickInfo info);
}
