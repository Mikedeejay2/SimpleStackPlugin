package com.mikedeejay2.simplestack.system.itemclick.process.inventoryaction;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.inventory.ItemStack;

public class ProcessHotbarMoveAndReadd implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        ItemStack hotbarItem = info.bottomInv.getItem(info.hotbar);
        int       hotbarAmt  = hotbarItem.getAmount();
        int       hotbarMax  = StackUtils.getMaxAmount(info.plugin, hotbarItem);
        if(hotbarAmt > hotbarMax || info.selectedAmt > info.selectedMax) return;
        boolean singleton = InventoryIdentifiers.singletonSlot(info.rawSlot, info.invView);
        if(!singleton)
        {
            info.clickedInv.setItem(info.slot, hotbarItem);
            info.bottomInv.setItem(info.hotbar, info.selected);
        }
        else
        {
            if(!info.selectedNull) return;
            int newAmt = 1;
            int extraAmt = hotbarAmt - 1;
            ItemStack newItem = hotbarItem.clone();
            newItem.setAmount(newAmt);
            hotbarItem.setAmount(extraAmt);
            info.clickedInv.setItem(info.slot, newItem);
            info.bottomInv.setItem(info.hotbar, hotbarItem);
        }
    }
}
