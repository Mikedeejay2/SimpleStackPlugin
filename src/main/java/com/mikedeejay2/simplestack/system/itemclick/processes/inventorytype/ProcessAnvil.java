package com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public class ProcessAnvil implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.rawSlot != 2) return;
        switch(info.getAction())
        {
            case PICKUP_ALL:
            case PICKUP_HALF:
            case PICKUP_ONE:
            case PICKUP_SOME:
            case MOVE_TO_OTHER_INVENTORY:
                break;
            default:
                return;
        }
        /* DEBUG */ System.out.println("Process Anvil");
        ItemStack result = info.topInv.getItem(2);
        if(result == null) return;

        ItemStack item1 = info.topInv.getItem(0);
        ItemStack item2 = info.topInv.getItem(1);
        boolean item1Null = item1 == null;
        boolean item2Null = item2 == null;
        int item1Amount = item1Null ? 0 : item1.getAmount();
        int item2Amount = item2Null ? 0 : item2.getAmount();

        if(!item1Null)
        {

        }
        info.player.getWorld().playSound(info.player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
    }
}
