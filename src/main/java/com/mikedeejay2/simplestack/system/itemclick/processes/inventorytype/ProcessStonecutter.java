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
        Inventory inventory = info.topInv;
        ItemStack result = inventory.getItem(1);
        ItemStack input = inventory.getItem(0);
        if(result == null || input == null) return;
        int inputAmt = input.getAmount();
        int resultAmt = result.getAmount();
        boolean useMax = info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        int takeValue = 1;
        if(useMax)
        {
            int maxTake = resultAmt * inputAmt;

            takeValue = MoveUtils.resultSlotShift(info, maxTake);
        }
        input.setAmount(input.getAmount() - takeValue);

        ItemStack clonedItem = inventory.getItem(0) == null ? null : result.clone();
        // Unfortunately, this is the only decent way I've found to preserve the
        // result slot without having the item be removed from the slot.
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
