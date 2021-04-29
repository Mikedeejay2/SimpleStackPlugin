package com.mikedeejay2.simplestack.system.itemclick.process.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.inventory.ItemStack;

public class ProcessDropOneSlot implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        int dropAmt = 1;
        int extraAmt = info.selectedAmt - 1;
        if(extraAmt < 0) return;
        ItemStack dropItem = info.selected.clone();
        dropItem.setAmount(dropAmt);
        info.selected.setAmount(extraAmt);
        MoveUtils.dropItemPlayer(info.player, dropItem);
    }
}
