package com.mikedeejay2.simplestack.system.itemclick.process.global;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import com.mikedeejay2.simplestack.system.itemclick.process.inventoryaction.*;
import org.bukkit.event.inventory.InventoryAction;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ProcessAction implements ItemClickProcess
{
    protected Map<InventoryAction, List<ItemClickProcess>> actionProcesses;

    public ProcessAction()
    {
        this.actionProcesses = new EnumMap<>(InventoryAction.class);
        for(InventoryAction action : InventoryAction.values())
        {
            actionProcesses.put(action, new ArrayList<>());
        }
    }

    public void initDefault()
    {
        addProcess(InventoryAction.PICKUP_ALL, new ProcessPickupAll());
        addProcess(InventoryAction.PICKUP_SOME, new ProcessPickupSome());
        addProcess(InventoryAction.PICKUP_HALF, new ProcessPickupHalf());
        addProcess(InventoryAction.PICKUP_ONE, new ProcessPickupOne());
        addProcess(InventoryAction.PLACE_ALL, new ProcessPlaceAll());
        addProcess(InventoryAction.PLACE_SOME, new ProcessPlaceSome());
        addProcess(InventoryAction.PLACE_ONE, new ProcessPlaceOne());
        addProcess(InventoryAction.SWAP_WITH_CURSOR, new ProcessSwapWithCursor());
        addProcess(InventoryAction.DROP_ALL_CURSOR, new ProcessDropAllCursor());
        addProcess(InventoryAction.DROP_ONE_CURSOR, new ProcessDropOneCursor());
        addProcess(InventoryAction.DROP_ALL_SLOT, new ProcessDropAllSlot());
        addProcess(InventoryAction.DROP_ONE_SLOT, new ProcessDropOneSlot());
        addProcess(InventoryAction.MOVE_TO_OTHER_INVENTORY, new ProcessShiftClick());
        addProcess(InventoryAction.HOTBAR_MOVE_AND_READD, new ProcessHotbarMoveAndReadd());
        addProcess(InventoryAction.HOTBAR_SWAP, new ProcessHotbarSwap());
        addProcess(InventoryAction.CLONE_STACK, new ProcessCloneStack());
        addProcess(InventoryAction.COLLECT_TO_CURSOR, new ProcessCollectToCursor());
    }


    @Override
    public void invoke(ItemClickInfo info)
    {
        actionProcesses.get(info.getAction()).forEach(process -> process.invoke(info));
    }

    public ProcessAction addProcess(InventoryAction action, ItemClickProcess process)
    {
        actionProcesses.get(action).add(process);
        return this;
    }
}
