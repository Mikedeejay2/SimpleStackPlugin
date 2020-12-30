package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessMoveToOtherInventory extends ItemClickProcess
{
    public ProcessMoveToOtherInventory(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }

    @Override
    public void invoke()
    {
        Inventory   otherInv    = clickedBottom ? topInv : bottomInv;
        ItemStack[] topItems    = otherInv.getStorageContents();
        Material    selectedMat = selected.getType();
        for(int i = 0; i < topItems.length; ++i)
        {
            ItemStack item = topItems[i];
            if(item == null) continue;
            if(item.getType() != selectedMat) continue;
            int itemAmt = item.getAmount();
            if(itemAmt == selectedMax) continue;
            int newAmt = itemAmt + selectedAmt;
            if(newAmt > selectedMax)
            {
                selectedAmt = newAmt - selectedMax;
                newAmt = selectedMax;
            }
            else
            {
                selectedAmt = 0;
            }
            item.setAmount(newAmt);
            if(selectedAmt <= 0)
            {
                selected.setAmount(0);
                return;
            }
        }
        for(int i = 0; i < topItems.length; ++i)
        {
            ItemStack item = topItems[i];
            if(item != null) continue;
            item = selected.clone();
            int newAmt = selectedAmt;
            if(newAmt > selectedMax)
            {
                selectedAmt = newAmt - selectedMax;
                newAmt = selectedMax;
            }
            else
            {
                selectedAmt -= newAmt;
            }
            item.setAmount(newAmt);
            otherInv.setItem(i, item);
            player.sendMessage("selectedAmt: " + selectedAmt);
            if(selectedAmt <= 0)
            {
                selected.setAmount(0);
                return;
            }
        }
        selected.setAmount(selectedAmt);
    }
}
