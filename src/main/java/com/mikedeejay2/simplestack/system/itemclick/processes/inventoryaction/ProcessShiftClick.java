package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.CraftingInventory;

public class ProcessShiftClick implements ItemClickProcess
{
    protected ItemClickProcess otherProcess;
    protected ItemClickProcess sameProcess;
    protected ItemClickProcess hotbarProcess;
    protected ItemClickProcess hotbarReverseProcess;

    protected enum ShiftType
    {
        OTHER,
        SAME,
        HOTBAR,
        HOTBAR_REVERSE
    }

    public ProcessShiftClick()
    {
        this.otherProcess = new ProcessMoveToOtherInv();
        this.sameProcess = new ProcessMoveToSameInv();
        this.hotbarProcess = new ProcessMoveToHotbar();
        this.hotbarReverseProcess = new ProcessMoveToHotbarReverse();
    }

    @Override
    public void invoke(ItemClickInfo info)
    {
        ShiftType type;

        switch(info.slotType)
        {
            case CRAFTING:
                if(info.topInv instanceof BrewerInventory)
                {
                    type = ShiftType.HOTBAR_REVERSE;
                }
                else
                {
                    type = ShiftType.OTHER;
                }
                break;
            case CONTAINER:
            case RESULT:
                type = ShiftType.HOTBAR_REVERSE;
                break;
            default:
                type = ShiftType.OTHER;
                break;
        }

        if(info.topInv instanceof CraftingInventory && info.topInv.getSize() == 5)
        {
            type = ShiftType.SAME;
        }

        switch(type)
        {
            case OTHER:
                otherProcess.invoke(info);
                break;
            case SAME:
                sameProcess.invoke(info);
                break;
            case HOTBAR:
                hotbarProcess.invoke(info);
                break;
            case HOTBAR_REVERSE:
                hotbarReverseProcess.invoke(info);
                break;
        }
    }
}
