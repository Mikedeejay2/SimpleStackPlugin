package com.mikedeejay2.simplestack.system.itemclick.processor;

import com.mikedeejay2.simplestack.system.SimpleStackProcessor;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.*;
import org.bukkit.event.inventory.InventoryAction;

import java.util.*;
import java.util.function.Predicate;

public class ItemClickProcessor implements SimpleStackProcessor
{
    protected List<Map.Entry<Predicate<ItemClickInfo>, List<ItemClickProcess>>> processes;

    public ItemClickProcessor()
    {
        this.processes = new ArrayList<>();
    }

    public void initDefault()
    {
        Predicate<ItemClickInfo> pickupAllPredicate = info -> info.getAction() == InventoryAction.PICKUP_ALL;
        Predicate<ItemClickInfo> pickupSomePredicate = info -> info.getAction() == InventoryAction.PICKUP_SOME;
        Predicate<ItemClickInfo> pickupHalfPredicate = info -> info.getAction() == InventoryAction.PICKUP_HALF;
        Predicate<ItemClickInfo> pickupOnePredicate = info -> info.getAction() == InventoryAction.PICKUP_ONE;
        Predicate<ItemClickInfo> placeAllPredicate = info -> info.getAction() == InventoryAction.PLACE_ALL;
        Predicate<ItemClickInfo> placeSomePredicate = info -> info.getAction() == InventoryAction.PLACE_SOME;
        Predicate<ItemClickInfo> placeOnePredicate = info -> info.getAction() == InventoryAction.PLACE_ONE;
        Predicate<ItemClickInfo> swapWithCursorPredicate = info -> info.getAction() == InventoryAction.SWAP_WITH_CURSOR;
        Predicate<ItemClickInfo> dropAllCursorPredicate = info -> info.getAction() == InventoryAction.DROP_ALL_CURSOR;
        Predicate<ItemClickInfo> dropOneCursorPredicate = info -> info.getAction() == InventoryAction.DROP_ONE_CURSOR;
        Predicate<ItemClickInfo> dropAllSlotPredicate = info -> info.getAction() == InventoryAction.DROP_ALL_SLOT;
        Predicate<ItemClickInfo> dropOneSlotPredicate = info -> info.getAction() == InventoryAction.DROP_ONE_SLOT;
        Predicate<ItemClickInfo> moveToOtherInventoryPredicate = info -> info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        Predicate<ItemClickInfo> hotbarMoveAndReaddPredicate = info -> info.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD;
        Predicate<ItemClickInfo> hotbarSwapPredicate = info -> info.getAction() == InventoryAction.HOTBAR_SWAP;
        Predicate<ItemClickInfo> cloneStackPredicate = info -> info.getAction() == InventoryAction.CLONE_STACK;
        Predicate<ItemClickInfo> collectToCursorPredicate = info -> info.getAction() == InventoryAction.COLLECT_TO_CURSOR;

        List<ItemClickProcess> pickupAllProcess = new ArrayList<>();
        List<ItemClickProcess> pickupSomeProcess = new ArrayList<>();
        List<ItemClickProcess> pickupHalfProcess = new ArrayList<>();
        List<ItemClickProcess> pickupOneProcess = new ArrayList<>();
        List<ItemClickProcess> placeAllProcess = new ArrayList<>();
        List<ItemClickProcess> placeSomeProcess = new ArrayList<>();
        List<ItemClickProcess> placeOneProcess = new ArrayList<>();
        List<ItemClickProcess> swapWithCursorProcess = new ArrayList<>();
        List<ItemClickProcess> dropAllCursorProcess = new ArrayList<>();
        List<ItemClickProcess> dropOneCursorProcess = new ArrayList<>();
        List<ItemClickProcess> dropAllSlotProcess = new ArrayList<>();
        List<ItemClickProcess> dropOneSlotProcess = new ArrayList<>();
        List<ItemClickProcess> moveToOtherInventoryProcess = new ArrayList<>();
        List<ItemClickProcess> hotbarMoveAndReaddProcess = new ArrayList<>();
        List<ItemClickProcess> hotbarSwapProcess = new ArrayList<>();
        List<ItemClickProcess> cloneStackProcess = new ArrayList<>();
        List<ItemClickProcess> collectToCursorProcess =  new ArrayList<>();

        pickupAllProcess.add(new ProcessPickupAll());
        pickupSomeProcess.add(new ProcessPickupSome());
        pickupHalfProcess.add(new ProcessPickupHalf());
        pickupOneProcess.add(new ProcessPickupOne());
        placeAllProcess.add(new ProcessPlaceAll());
        placeSomeProcess.add(new ProcessPlaceSome());
        placeOneProcess.add(new ProcessPlaceOne());
        swapWithCursorProcess.add(new ProcessSwapWithCursor());
        dropAllCursorProcess.add(new ProcessDropAllCursor());
        dropOneCursorProcess.add(new ProcessDropOneCursor());
        dropAllSlotProcess.add(new ProcessDropAllSlot());
        dropOneSlotProcess.add(new ProcessDropOneSlot());
        moveToOtherInventoryProcess.add(new ProcessMoveToOtherInventory());
        hotbarMoveAndReaddProcess.add(new ProcessHotbarMoveAndReadd());
        hotbarSwapProcess.add(new ProcessHotbarSwap());
        cloneStackProcess.add(new ProcessCloneStack());
        collectToCursorProcess.add(new ProcessCollectToCursor());

        addProcess(pickupAllPredicate, pickupAllProcess);
        addProcess(pickupSomePredicate, pickupSomeProcess);
        addProcess(pickupHalfPredicate, pickupHalfProcess);
        addProcess(pickupOnePredicate, pickupOneProcess);
        addProcess(placeAllPredicate, placeAllProcess);
        addProcess(placeSomePredicate, placeSomeProcess);
        addProcess(placeOnePredicate, placeOneProcess);
        addProcess(swapWithCursorPredicate, swapWithCursorProcess);
        addProcess(dropAllCursorPredicate, dropAllCursorProcess);
        addProcess(dropOneCursorPredicate, dropOneCursorProcess);
        addProcess(dropAllSlotPredicate, dropAllSlotProcess);
        addProcess(dropOneSlotPredicate, dropOneSlotProcess);
        addProcess(moveToOtherInventoryPredicate, moveToOtherInventoryProcess);
        addProcess(hotbarMoveAndReaddPredicate, hotbarMoveAndReaddProcess);
        addProcess(hotbarSwapPredicate, hotbarSwapProcess);
        addProcess(cloneStackPredicate, cloneStackProcess);
        addProcess(collectToCursorPredicate, collectToCursorProcess);
    }

    public void process(ItemClickInfo info)
    {
        for(Map.Entry<Predicate<ItemClickInfo>, List<ItemClickProcess>> entry : processes)
        {
            Predicate<ItemClickInfo> condition = entry.getKey();
            List<ItemClickProcess> list = entry.getValue();
            if(!condition.test(info)) continue;
            for(ItemClickProcess process : list)
            {
                process.invoke(info);
            }
        }
    }

    public ItemClickProcessor addProcess(Predicate<ItemClickInfo> condition, List<ItemClickProcess> processList)
    {
        processes.add(new AbstractMap.SimpleEntry<>(condition, processList));
        return this;
    }

    public ItemClickProcessor addProcess(Predicate<ItemClickInfo> condition, ItemClickProcess... processList)
    {
        this.addProcess(condition, Arrays.asList(processList));
        return this;
    }

    public ItemClickProcessor removeProcess(int index)
    {
        processes.remove(index);
        return this;
    }

    public ItemClickProcessor removeProcess(List<ItemClickProcess> list)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            Map.Entry<Predicate<ItemClickInfo>, List<ItemClickProcess>> entry = processes.get(i);
            List<ItemClickProcess> curList = entry.getValue();
            if(!list.equals(curList)) continue;
            processes.remove(i);
            break;
        }
        return this;
    }

    public ItemClickProcessor removeProcess(Predicate<ItemClickInfo> condition)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            Map.Entry<Predicate<ItemClickInfo>, List<ItemClickProcess>> entry = processes.get(i);
            Predicate<ItemClickInfo> curCondition = entry.getKey();
            if(!condition.equals(curCondition)) continue;
            processes.remove(i);
            break;
        }
        return this;
    }

    public boolean containsProcess(List<ItemClickProcess> list)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            Map.Entry<Predicate<ItemClickInfo>, List<ItemClickProcess>> entry = processes.get(i);
            List<ItemClickProcess> curList = entry.getValue();
            if(list.equals(curList)) return true;
        }
        return false;
    }

    public boolean containsProcess(Predicate<ItemClickInfo> condition)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            Map.Entry<Predicate<ItemClickInfo>, List<ItemClickProcess>> entry = processes.get(i);
            Predicate<ItemClickInfo> curCondition = entry.getKey();
            if(!condition.equals(curCondition)) return true;
        }
        return false;
    }
}
