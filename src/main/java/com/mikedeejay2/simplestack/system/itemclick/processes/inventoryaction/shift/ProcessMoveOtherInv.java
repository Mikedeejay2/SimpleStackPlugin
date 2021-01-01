package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction.shift;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessMoveOtherInv implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        info.player.sendMessage("Move to Other Inv");
        Inventory   toInv    = info.clickedBottom ? info.topInv : info.bottomInv;
        ItemStack[] toItems    = toInv.getContents();
        Material    selectedMat = info.selected.getType();
        int selectedAmt = info.selectedAmt;
        int rawStart = info.clickedBottom ? InventoryIdentifiers.FIRST_TOP_RAW_SLOT : 0;
        for(int i = 0; i < toInv.getSize(); ++i)
        {
            int convertedSlot = info.invView.convertSlot(rawStart + i);
            InventoryType.SlotType slotType = info.invView.getSlotType(convertedSlot);
            System.out.println("Slot: " + convertedSlot + ", slot type: " + slotType);
            if(slotType == InventoryType.SlotType.RESULT) continue;
            ItemStack item = toItems[i];
            if(item == null) continue;
            if(item.getType() == Material.AIR) continue;
            if(item.getType() != selectedMat) continue;
            int itemAmt = item.getAmount();
            if(itemAmt == info.selectedMax) continue;
            int newAmt = itemAmt + info.selectedAmt;
            if(newAmt > info.selectedMax)
            {
                selectedAmt = newAmt - info.selectedMax;
                newAmt = info.selectedMax;
            }
            else
            {
                selectedAmt = 0;
            }
            item.setAmount(newAmt);
            if(selectedAmt <= 0)
            {
                info.selected.setAmount(0);
                return;
            }
        }

        for(int i = 0; i < toInv.getSize(); ++i)
        {
            int convertedSlot = info.invView.convertSlot(rawStart + i);
            InventoryType.SlotType slotType = info.invView.getSlotType(convertedSlot);
            System.out.println("Slot: " + convertedSlot + ", slot type: " + slotType);
            if(slotType == InventoryType.SlotType.RESULT) continue;
            ItemStack item = toItems[i];
            if(item != null && item.getType() != Material.AIR) continue;
            item = info.selected.clone();
            int newAmt = selectedAmt;
            if(newAmt > info.selectedMax)
            {
                selectedAmt = newAmt - info.selectedMax;
                newAmt = info.selectedMax;
            }
            else
            {
                selectedAmt -= newAmt;
            }
            item.setAmount(newAmt);
            toInv.setItem(i, item);
            if(selectedAmt <= 0)
            {
                info.selected.setAmount(0);
                return;
            }
        }
        info.selected.setAmount(selectedAmt);
    }
}
