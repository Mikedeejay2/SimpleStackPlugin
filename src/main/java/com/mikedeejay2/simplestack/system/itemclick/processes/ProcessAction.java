package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction.*;
import org.bukkit.event.inventory.InventoryAction;

import java.util.ArrayList;
import java.util.List;

public class ProcessAction implements ItemClickProcess
{
    protected final Simplestack plugin;

    protected List<ItemClickProcess> pickupAllProcesses;
    protected List<ItemClickProcess> pickupSomeProcesses;
    protected List<ItemClickProcess> pickupHalfProcesses;
    protected List<ItemClickProcess> pickupOneProcesses;
    protected List<ItemClickProcess> placeAllProcesses;
    protected List<ItemClickProcess> placeSomeProcesses;
    protected List<ItemClickProcess> placeOneProcesses;
    protected List<ItemClickProcess> swapWithCursorProcesses;
    protected List<ItemClickProcess> dropAllCursorProcesses;
    protected List<ItemClickProcess> dropOneCursorProcesses;
    protected List<ItemClickProcess> dropAllSlotProcesses;
    protected List<ItemClickProcess> dropOneSlotProcesses;
    protected List<ItemClickProcess> moveToOtherInventoryProcesses;
    protected List<ItemClickProcess> hotbarMoveAndReaddProcesses;
    protected List<ItemClickProcess> hotbarSwapProcesses;
    protected List<ItemClickProcess> cloneStackProcesses;
    protected List<ItemClickProcess> collectToCursorProcesses;

    public ProcessAction(Simplestack plugin)
    {
        this.plugin = plugin;
        pickupAllProcesses = new ArrayList<>(1);
        pickupSomeProcesses = new ArrayList<>(1);
        pickupHalfProcesses = new ArrayList<>(1);
        pickupOneProcesses = new ArrayList<>(1);
        placeAllProcesses = new ArrayList<>(1);
        placeSomeProcesses = new ArrayList<>(1);
        placeOneProcesses = new ArrayList<>(1);
        swapWithCursorProcesses = new ArrayList<>(1);
        dropAllCursorProcesses = new ArrayList<>(1);
        dropOneCursorProcesses = new ArrayList<>(1);
        dropAllSlotProcesses = new ArrayList<>(1);
        dropOneSlotProcesses = new ArrayList<>(1);
        moveToOtherInventoryProcesses = new ArrayList<>(1);
        hotbarMoveAndReaddProcesses = new ArrayList<>(1);
        hotbarSwapProcesses = new ArrayList<>(1);
        cloneStackProcesses = new ArrayList<>(1);
        collectToCursorProcesses = new ArrayList<>(1);
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
        switch(info.getAction())
        {
            case PICKUP_ALL:
                pickupAllProcesses.forEach(process -> process.invoke(info));
                break;
            case PICKUP_SOME:
                pickupSomeProcesses.forEach(process -> process.invoke(info));
                break;
            case PICKUP_HALF:
                pickupHalfProcesses.forEach(process -> process.invoke(info));
                break;
            case PICKUP_ONE:
                pickupOneProcesses.forEach(process -> process.invoke(info));
                break;
            case PLACE_ALL:
                placeAllProcesses.forEach(process -> process.invoke(info));;
                break;
            case PLACE_SOME:
                placeSomeProcesses.forEach(process -> process.invoke(info));
                break;
            case PLACE_ONE:
                placeOneProcesses.forEach(process -> process.invoke(info));
                break;
            case SWAP_WITH_CURSOR:
                swapWithCursorProcesses.forEach(process -> process.invoke(info));
                break;
            case DROP_ALL_CURSOR:
                dropAllCursorProcesses.forEach(process -> process.invoke(info));
                break;
            case DROP_ONE_CURSOR:
                dropOneCursorProcesses.forEach(process -> process.invoke(info));
                break;
            case DROP_ALL_SLOT:
                dropAllSlotProcesses.forEach(process -> process.invoke(info));
                break;
            case DROP_ONE_SLOT:
                dropOneSlotProcesses.forEach(process -> process.invoke(info));
                break;
            case MOVE_TO_OTHER_INVENTORY:
                moveToOtherInventoryProcesses.forEach(process -> process.invoke(info));
                break;
            case HOTBAR_MOVE_AND_READD:
                hotbarMoveAndReaddProcesses.forEach(process -> process.invoke(info));
                break;
            case HOTBAR_SWAP:
                hotbarSwapProcesses.forEach(process -> process.invoke(info));
                break;
            case CLONE_STACK:
                cloneStackProcesses.forEach(process -> process.invoke(info));
                break;
            case COLLECT_TO_CURSOR:
                collectToCursorProcesses.forEach(process -> process.invoke(info));
                break;
        }
    }

    public ProcessAction addProcess(InventoryAction action, ItemClickProcess process)
    {
        switch(action)
        {
            case PICKUP_ALL:
                pickupAllProcesses.add(process);
                break;
            case PICKUP_SOME:
                pickupSomeProcesses.add(process);
                break;
            case PICKUP_HALF:
                pickupHalfProcesses.add(process);
                break;
            case PICKUP_ONE:
                pickupOneProcesses.add(process);
                break;
            case PLACE_ALL:
                placeAllProcesses.add(process);
                break;
            case PLACE_SOME:
                placeSomeProcesses.add(process);
                break;
            case PLACE_ONE:
                placeOneProcesses.add(process);
                break;
            case SWAP_WITH_CURSOR:
                swapWithCursorProcesses.add(process);
                break;
            case DROP_ALL_CURSOR:
                dropAllCursorProcesses.add(process);
                break;
            case DROP_ONE_CURSOR:
                dropOneCursorProcesses.add(process);
                break;
            case DROP_ALL_SLOT:
                dropAllSlotProcesses.add(process);
                break;
            case DROP_ONE_SLOT:
                dropOneSlotProcesses.add(process);
                break;
            case MOVE_TO_OTHER_INVENTORY:
                moveToOtherInventoryProcesses.add(process);
                break;
            case HOTBAR_MOVE_AND_READD:
                hotbarMoveAndReaddProcesses.add(process);
                break;
            case HOTBAR_SWAP:
                hotbarSwapProcesses.add(process);
                break;
            case CLONE_STACK:
                cloneStackProcesses.add(process);
                break;
            case COLLECT_TO_CURSOR:
                collectToCursorProcesses.add(process);
                break;
        }
        return this;
    }
}
