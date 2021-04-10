package com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.CheckUtils;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessAnvil implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        Inventory inventory = info.topInv;
        if(info.rawSlot != 2) return;
        if(!InventoryIdentifiers.takeResult(info.getAction())) return;
        ItemStack result = inventory.getItem(2);
        if(result == null) return;

        ItemStack item1 = inventory.getItem(0);
        ItemStack item2 = inventory.getItem(1);
        boolean item1Null = item1 == null;
        boolean item2Null = item2 == null;
        int item1Amount = item1Null ? 0 : item1.getAmount();
        int item2Amount = item2Null ? 0 : item2.getAmount();
        int amount = 1;
        if(!item1Null && !item2Null)
        {
            amount = Math.min(item1Amount, item2Amount);
        }
        else if(!item1Null)
        {
            amount = item1Amount;
        }

        if(!item1Null)
        {
            item1.setAmount(item1Amount - amount);
        }
        if(!item2Null)
        {
            item2.setAmount(item2Amount - amount);
        }

        info.player.getWorld().playSound(info.player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
    }
}
