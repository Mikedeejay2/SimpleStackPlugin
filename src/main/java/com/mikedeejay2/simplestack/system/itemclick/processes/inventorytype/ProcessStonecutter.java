package com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.CheckUtils;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ProcessStonecutter implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.rawSlot != 1) return;
        if(!InventoryIdentifiers.takeResult(info.getAction())) return;
        /* DEBUG */ System.out.println("Process Stonecutter");
        Inventory inventory = info.topInv;
        ItemStack result = inventory.getItem(1);
        ItemStack input = inventory.getItem(0);
        if(result == null) return;
        int inputAmt = input.getAmount();
        int resultAmt = result.getAmount();
        boolean useMax = info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        int takeValue = 1;
        if(useMax)
        {
            takeValue = resultAmt * inputAmt;
            int maxTake = takeValue - 1;

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
        input.setAmount(input.getAmount() - takeValue);

        if(input.getAmount() != 0)
        {
            ItemStack clonedItem = result.clone();
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    inventory.setItem(1, clonedItem);
                    info.player.updateInventory();
                }
            }.runTask(info.plugin);
        }
    }
}
