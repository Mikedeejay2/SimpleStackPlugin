package com.mikedeejay2.simplestack.system.itemclick.handlers;

import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.SimpleStackHandler;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.executors.ItemClickExecutor;
import com.mikedeejay2.simplestack.util.InvActionStruct;
import com.mikedeejay2.simplestack.system.itemclick.preprocesses.ItemClickPreprocess;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemClickHandler implements SimpleStackHandler<InventoryClickEvent>
{
    protected final Simplestack plugin;
    protected List<ItemClickExecutor> executors;
    protected List<ItemClickPreprocess> preprocesses;

    public ItemClickHandler(Simplestack plugin)
    {
        this.plugin = plugin;
        this.executors = new ArrayList<>();
    }

    @Override
    public void handle(InventoryClickEvent event)
    {
        InvActionStruct invAction = new InvActionStruct(InventoryAction.NOTHING);
        ItemClickInfo info = new ItemClickInfo(event, plugin);
        preprocesses.forEach(preprocess -> preprocess.invoke(info, invAction));

        info.player.sendMessage("Action NEW: " + invAction.getAction());
        executeAction(info, invAction.getAction());
    }

    public void executeAction(ItemClickInfo info, InventoryAction action)
    {
        switch(action)
        {
            case NOTHING:                   executors.forEach(e -> e.execNothing(info)); break;
            case PICKUP_ALL:                executors.forEach(e -> e.execPickupAll(info)); break;
            case PICKUP_SOME:               executors.forEach(e -> e.execPickupSome(info)); break;
            case PICKUP_HALF:               executors.forEach(e -> e.execPickupHalf(info)); break;
            case PICKUP_ONE:                executors.forEach(e -> e.execPickupOne(info)); break;
            case PLACE_ALL:                 executors.forEach(e -> e.execPlaceAll(info)); break;
            case PLACE_SOME:                executors.forEach(e -> e.execPlaceSome(info)); break;
            case PLACE_ONE:                 executors.forEach(e -> e.execPlaceOne(info)); break;
            case SWAP_WITH_CURSOR:          executors.forEach(e -> e.execSwapWithCursor(info)); break;
            case DROP_ALL_CURSOR:           executors.forEach(e -> e.execDropAllCursor(info)); break;
            case DROP_ONE_CURSOR:           executors.forEach(e -> e.execDropOneCursor(info)); break;
            case DROP_ALL_SLOT:             executors.forEach(e -> e.execDropAllSlot(info)); break;
            case DROP_ONE_SLOT:             executors.forEach(e -> e.execDropOneSlot(info)); break;
            case MOVE_TO_OTHER_INVENTORY:   executors.forEach(e -> e.execMoveToOtherInventory(info)); break;
            case HOTBAR_MOVE_AND_READD:     executors.forEach(e -> e.execHotbarMoveAndReadd(info)); break;
            case HOTBAR_SWAP:               executors.forEach(e -> e.execHotbarSwap(info)); break;
            case CLONE_STACK:               executors.forEach(e -> e.execCloneStack(info)); break;
            case COLLECT_TO_CURSOR:         executors.forEach(e -> e.execCollectToCursor(info)); break;
            case UNKNOWN:                   executors.forEach(e -> e.execUnknown(info)); break;
        }
    }

    public void addExecutor(ItemClickExecutor executor)
    {
        executors.add(executor);
    }

    public void removeExecutor(ItemClickExecutor executor)
    {
        executors.remove(executor);
    }

    public void removeExecutor(int index)
    {
        executors.remove(index);
    }

    public void removeExecutor(Class<? extends ItemClickExecutor> execClass)
    {
        for(ItemClickExecutor executor : executors)
        {
            if(execClass != executor.getClass()) continue;
            executors.remove(executor);
            return;
        }
    }

    public boolean containsExecutor(ItemClickExecutor executor)
    {
        return executors.contains(executor);
    }

    public boolean containsExecutor(Class<? extends ItemClickExecutor> execClass)
    {
        for(ItemClickExecutor executor : executors)
        {
            if(execClass == executor.getClass()) return true;
        }
        return false;
    }
}
