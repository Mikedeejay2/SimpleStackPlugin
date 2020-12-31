package com.mikedeejay2.simplestack.system.itemclick.preprocesses;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocesses.clicktype.*;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class PreprocessItemClick implements ItemClickPreprocess
{
    protected List<ItemClickPreprocess> leftClickProcesses;
    protected List<ItemClickPreprocess> shiftClickProcesses;
    protected List<ItemClickPreprocess> rightClickProcesses;
    protected List<ItemClickPreprocess> middleClickProcesses;
    protected List<ItemClickPreprocess> numberKeyProcesses;
    protected List<ItemClickPreprocess> doubleClickProcesses;
    protected List<ItemClickPreprocess> dropProcesses;
    protected List<ItemClickPreprocess> controlDropProcesses;
    protected List<ItemClickPreprocess> swapOffhandProcesses;

    public PreprocessItemClick()
    {
        leftClickProcesses = new ArrayList<>(1);
        shiftClickProcesses = new ArrayList<>(1);
        rightClickProcesses = new ArrayList<>(1);
        middleClickProcesses = new ArrayList<>(1);
        numberKeyProcesses = new ArrayList<>(1);
        doubleClickProcesses = new ArrayList<>(1);
        dropProcesses = new ArrayList<>(1);
        controlDropProcesses = new ArrayList<>(1);
        swapOffhandProcesses = new ArrayList<>(1);
    }

    public void initDefault()
    {
        addPreprocess(ClickType.LEFT, new PreprocessLeft());
        addPreprocess(ClickType.SHIFT_LEFT, new PreprocessShift());
        addPreprocess(ClickType.RIGHT, new PreprocessRight());
        addPreprocess(ClickType.MIDDLE, new PreprocessMiddle());
        addPreprocess(ClickType.NUMBER_KEY, new PreprocessNumber());
        addPreprocess(ClickType.DOUBLE_CLICK, new PreprocessDoubleClick());
        addPreprocess(ClickType.DROP, new PreprocessDrop());
        addPreprocess(ClickType.CONTROL_DROP, new PreprocessControlDrop());
        addPreprocess(ClickType.SWAP_OFFHAND, new PreprocessSwapOffhand());
        addPreprocess(ClickType.LEFT, new PreprocessLeft());
    }

    public PreprocessItemClick addPreprocess(ClickType type, ItemClickPreprocess process)
    {
        switch(type)
        {
            case LEFT:
                leftClickProcesses.add(process);
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                shiftClickProcesses.add(process);
                break;
            case RIGHT:
                rightClickProcesses.add(process);
                break;
            case MIDDLE:
                middleClickProcesses.add(process);
                break;
            case NUMBER_KEY:
                numberKeyProcesses.add(process);
                break;
            case DOUBLE_CLICK:
                doubleClickProcesses.add(process);
                break;
            case DROP:
                dropProcesses.add(process);
                break;
            case CONTROL_DROP:
                controlDropProcesses.add(process);
                break;
            case SWAP_OFFHAND:
                swapOffhandProcesses.add(process);
                break;
        }
        return this;
    }

    @Override
    public void invoke(ItemClickInfo info)
    {
        switch(info.clickType)
        {
            case LEFT:
                leftClickProcesses.forEach(process -> process.invoke(info));
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                shiftClickProcesses.forEach(process -> process.invoke(info));
                break;
            case RIGHT:
                rightClickProcesses.forEach(process -> process.invoke(info));
                break;
            case MIDDLE:
                middleClickProcesses.forEach(process -> process.invoke(info));
                break;
            case NUMBER_KEY:
                numberKeyProcesses.forEach(process -> process.invoke(info));
                break;
            case DOUBLE_CLICK:
                doubleClickProcesses.forEach(process -> process.invoke(info));
                break;
            case DROP:
                dropProcesses.forEach(process -> process.invoke(info));
                break;
            case CONTROL_DROP:
                controlDropProcesses.forEach(process -> process.invoke(info));
                break;
            case SWAP_OFFHAND:
                swapOffhandProcesses.forEach(process -> process.invoke(info));
                break;
        }
    }
}
