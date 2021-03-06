package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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
