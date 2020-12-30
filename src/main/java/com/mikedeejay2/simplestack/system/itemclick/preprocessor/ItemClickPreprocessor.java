package com.mikedeejay2.simplestack.system.itemclick.preprocessor;

import com.mikedeejay2.simplestack.system.SimpleStackPreprocessor;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocesses.*;
import com.mikedeejay2.simplestack.util.InvActionStruct;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.function.Predicate;

public class ItemClickPreprocessor implements SimpleStackPreprocessor
{
    protected List<Map.Entry<Predicate<ItemClickInfo>, List<ItemClickPreprocess>>> processes;

    public ItemClickPreprocessor()
    {
        this.processes = new ArrayList<>();
    }

    public void initDefault()
    {
        Predicate<ItemClickInfo> leftPredicate = info -> info.clickType == ClickType.LEFT;
        Predicate<ItemClickInfo> shiftPredicate = info -> info.clickType == ClickType.SHIFT_LEFT || info.clickType == ClickType.SHIFT_RIGHT;
        Predicate<ItemClickInfo> rightPredicate = info -> info.clickType == ClickType.RIGHT;
        Predicate<ItemClickInfo> middlePredicate = info -> info.clickType == ClickType.MIDDLE;
        Predicate<ItemClickInfo> numberKeyPredicate = info -> info.clickType == ClickType.NUMBER_KEY;
        Predicate<ItemClickInfo> doubleClickPredicate = info -> info.clickType == ClickType.DOUBLE_CLICK;
        Predicate<ItemClickInfo> dropPredicate = info -> info.clickType == ClickType.DROP;
        Predicate<ItemClickInfo> controlDropPredicate = info -> info.clickType == ClickType.CONTROL_DROP;
        Predicate<ItemClickInfo> swapOffhandPredicate = info -> info.clickType == ClickType.SWAP_OFFHAND;

        List<ItemClickPreprocess> leftProcesses = new ArrayList<>();
        List<ItemClickPreprocess> shiftProcesses = new ArrayList<>();
        List<ItemClickPreprocess> rightProcesses = new ArrayList<>();
        List<ItemClickPreprocess> middleProcesses = new ArrayList<>();
        List<ItemClickPreprocess> numberKeyProcesses = new ArrayList<>();
        List<ItemClickPreprocess> doubleClickProcesses = new ArrayList<>();
        List<ItemClickPreprocess> dropProcesses = new ArrayList<>();
        List<ItemClickPreprocess> controlDropProcesses = new ArrayList<>();
        List<ItemClickPreprocess> swapOffhandProcesses = new ArrayList<>();

        leftProcesses.add(new PreprocessLeft());
        shiftProcesses.add(new PreprocessShift());
        rightProcesses.add(new PreprocessRight());
        middleProcesses.add(new PreprocessMiddle());
        numberKeyProcesses.add(new PreprocessNumber());
        doubleClickProcesses.add(new PreprocessDoubleClick());
        dropProcesses.add(new PreprocessDrop());
        controlDropProcesses.add(new PreprocessControlDrop());
        swapOffhandProcesses.add(new PreprocessSwapOffhand());

        addPreprocess(leftPredicate, leftProcesses);
        addPreprocess(shiftPredicate, shiftProcesses);
        addPreprocess(rightPredicate, rightProcesses);
        addPreprocess(middlePredicate, middleProcesses);
        addPreprocess(numberKeyPredicate, numberKeyProcesses);
        addPreprocess(doubleClickPredicate, doubleClickProcesses);
        addPreprocess(dropPredicate, dropProcesses);
        addPreprocess(controlDropPredicate, controlDropProcesses);
        addPreprocess(swapOffhandPredicate, swapOffhandProcesses);

//        LEFT,
//        SHIFT_LEFT,
//        RIGHT,
//        MIDDLE,
//        NUMBER_KEY,
//        DOUBLE_CLICK,
//        DROP,
//        CONTROL_DROP,
//        SWAP_OFFHAND,
//        ;
    }

    public void preprocess(ItemClickInfo info, InvActionStruct action)
    {
        for(Map.Entry<Predicate<ItemClickInfo>, List<ItemClickPreprocess>> entry : processes)
        {
            Predicate<ItemClickInfo> condition = entry.getKey();
            List<ItemClickPreprocess> list = entry.getValue();
            if(!condition.test(info)) continue;
            for(ItemClickPreprocess process : list)
            {
                process.invoke(info, action);
            }
        }
    }

    public ItemClickPreprocessor addPreprocess(Predicate<ItemClickInfo> condition, List<ItemClickPreprocess> processList)
    {
        processes.add(new AbstractMap.SimpleEntry<>(condition, processList));
        return this;
    }

    public ItemClickPreprocessor addPreprocess(Predicate<ItemClickInfo> condition, ItemClickPreprocess... processList)
    {
        this.addPreprocess(condition, Arrays.asList(processList));
        return this;
    }
}