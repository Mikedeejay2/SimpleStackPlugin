package com.mikedeejay2.simplestack.system.itemclick.process.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.inventory.ItemStack;

public class ProcessDropOneCursor implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        int dropAmt = 1;
        int extraAmt = info.cursorAmt - 1;
        if(extraAmt < 0) return;
        ItemStack dropItem = info.cursor.clone();
        dropItem.setAmount(dropAmt);
        info.cursor.setAmount(extraAmt);
        info.player.setItemOnCursor(info.cursor);
        MoveUtils.dropItemPlayer(info.player, dropItem);
    }
}
