package com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessCrafting implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.rawSlot != 0) return;
        /* DEBUG */ System.out.println("Process Crafting");
        if(!InventoryIdentifiers.takeResult(info.getAction())) return;

        Inventory inventory = info.topInv;
        ItemStack result = inventory.getItem(0);
        if(result == null) return;
        ItemStack[] inputs = inventory.getContents();
        boolean useMax = info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        int takeValue = 1;
        if(useMax)
        {
            int maxTake = Integer.MAX_VALUE;
            for(int i = 1; i < inputs.length; ++i)
            {
                ItemStack curStack = inputs[i];
                if(curStack == null || curStack.getType() == Material.AIR) continue;
                int curAmt = curStack.getAmount();
                if(curAmt >= maxTake) continue;
                maxTake = curAmt;
            }
            takeValue = maxTake;
            --maxTake;

            ItemStack takeItem = info.selected;
            Inventory playerInv = info.bottomInv;
            ItemStack[] toItems = playerInv.getStorageContents();
            for(int count = 0; count < maxTake; ++count)
            {
                ItemStack curItem = takeItem.clone();
                int selectedAmt = takeItem.getAmount();
                for(int i = 0; i < toItems.length; ++i)
                {
                    ItemStack item = toItems[i];
                    if(item == null) continue;
                    if(item.getType() == Material.AIR) continue;
                    if(!ItemComparison.equalsEachOther(item, curItem)) continue;
                    int itemAmt = item.getAmount();
                    if(itemAmt == info.selectedMax) continue;
                    int newAmt = itemAmt + selectedAmt;
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
                        curItem.setAmount(selectedAmt);
                        break;
                    }
                }

                for(int i = 8; i >= 0; --i)
                {
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
                    playerInv.setItem(i, item);
                    if(selectedAmt <= 0)
                    {
                        curItem.setAmount(0);
                        break;
                    }
                }

                for(int i = 9; i < toItems.length; ++i)
                {
                    ItemStack item = toItems[i];
                    if(item != null && item.getType() != Material.AIR) continue;
                    item = curItem.clone();
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
                    playerInv.setItem(i, item);
                    if(selectedAmt <= 0)
                    {
                        curItem.setAmount(0);
                        break;
                    }
                }
                curItem.setAmount(selectedAmt);
            }
        }

        for(int i = 1; i < inputs.length; ++i)
        {
            ItemStack curItem = inputs[i];
            if(curItem == null || curItem.getType() == Material.AIR) continue;
            if(curItem.getAmount() == 0) continue;
            int newAmount = curItem.getAmount() - takeValue;
            curItem.setAmount(newAmount);
        }
    }
}
