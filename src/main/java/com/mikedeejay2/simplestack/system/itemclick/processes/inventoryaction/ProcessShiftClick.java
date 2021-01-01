package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
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
    protected ItemClickProcess offhandProcess;
    protected ItemClickProcess armorProcess;

    protected enum ShiftType
    {
        OTHER,
        SAME,
        HOTBAR,
        HOTBAR_REVERSE,
        OFFHAND,
        ARMOR
    }

    public ProcessShiftClick()
    {
        this.otherProcess = new ProcessMoveOtherInv();
        this.sameProcess = new ProcessMoveSameInv();
        this.hotbarProcess = new ProcessMoveHotbar();
        this.hotbarReverseProcess = new ProcessMoveHotbarReverse();
        this.offhandProcess = new ProcessMoveOffhand();
        this.armorProcess = new ProcessMoveArmor();
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
            case QUICKBAR:
            case CONTAINER:
                if(info.clickedBottom)
                {
                    if(InventoryIdentifiers.isOffhand(info.selected.getType()))
                    {
                        type = ShiftType.OFFHAND;
                    }
                    else if(info.slot == InventoryIdentifiers.OFFHAND_SLOT)
                    {
                        type = ShiftType.OFFHAND;
                    }
                    else if(InventoryIdentifiers.isArmor(info.selected.getType()))
                    {
                        type = ShiftType.ARMOR;
                    }
                    else
                    {
                        type = ShiftType.OTHER;

                        if(info.topInv instanceof CraftingInventory && info.topInv.getSize() == 5)
                        {
                            type = ShiftType.SAME;
                        }
                    }
                }
                else
                {
                    type = ShiftType.HOTBAR_REVERSE;
                }
                break;
            case RESULT:
                type = ShiftType.HOTBAR_REVERSE;
                break;
            default:
                type = ShiftType.OTHER;
                break;
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
            case ARMOR:
                armorProcess.invoke(info);
                break;
            case OFFHAND:
                offhandProcess.invoke(info);
                break;
        }
    }
}
