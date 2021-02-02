package com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class ProcessSmithing implements ItemClickProcess
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
        /* DEBUG */ System.out.println("Process Smithing");
        ItemStack item1  = info.topInv.getItem(0);
        ItemStack         item2  = info.topInv.getItem(1);
        ItemStack         result = info.topInv.getItem(2);
        if(result != null)
        {
            if(item2 != null)
            {
                if(item2.getAmount() > item1.getAmount())
                {
                    result.setAmount(item1.getAmount());
                }
                else
                {
                    result.setAmount(item2.getAmount());
                }
                item2.setAmount(item2.getAmount() - (int) Math.ceil(result.getAmount()));
            }
            if(item1 != null) item1.setAmount(item1.getAmount() - (int) Math.ceil(result.getAmount()));
            info.player.getWorld().playSound(info.player.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 1, 1);
        }
    }
}
