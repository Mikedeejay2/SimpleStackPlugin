package com.mikedeejay2.simplestack.system.itemclick.process.inventoryaction;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import com.mikedeejay2.simplestack.system.itemclick.process.inventoryaction.shift.*;
import com.mikedeejay2.simplestack.util.ShiftType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.CraftingInventory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ProcessShiftClick implements ItemClickProcess
{
    protected Map<ShiftType, List<ItemClickProcess>> shiftProcesses;

    public ProcessShiftClick()
    {
        this.shiftProcesses = new EnumMap<>(ShiftType.class);
        for(ShiftType type : ShiftType.values())
        {
            shiftProcesses.put(type, new ArrayList<>());
        }
        ItemClickProcess sameProcess = new ProcessMoveSameInv();
        shiftProcesses.get(ShiftType.SAME).add(sameProcess);
        shiftProcesses.get(ShiftType.OTHER).add(new ProcessMoveOtherInv(sameProcess));
        shiftProcesses.get(ShiftType.HOTBAR).add(new ProcessMoveHotbar(sameProcess));
        shiftProcesses.get(ShiftType.HOTBAR_REVERSE).add(new ProcessMoveHotbarReverse(sameProcess));
        shiftProcesses.get(ShiftType.OFFHAND).add(new ProcessMoveOffhand(sameProcess));
        shiftProcesses.get(ShiftType.ARMOR).add(new ProcessMoveArmor(sameProcess));
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
                    if(info.topInv instanceof CraftingInventory && info.topInv.getSize() == 5)
                    {
                        type = ShiftType.SAME;

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
                    }
                    else
                    {
                        type = ShiftType.OTHER;
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
            case ARMOR:
                type = ShiftType.ARMOR;
                break;
            default:
                type = ShiftType.OTHER;
                break;
        }

        shiftProcesses.get(type).forEach(process -> process.invoke(info));
    }

    public Map<ShiftType, List<ItemClickProcess>> getShiftProcesses()
    {
        return shiftProcesses;
    }

    public List<ItemClickProcess> getShiftProcesses(ShiftType type)
    {
        return shiftProcesses.get(type);
    }

    public ProcessShiftClick addProcess(ShiftType type, ItemClickProcess process)
    {
        shiftProcesses.get(type).add(process);
        return this;
    }
}
