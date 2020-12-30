package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
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
        Location dropLoc = info.player.getEyeLocation();
        Vector   lookVec = dropLoc.getDirection().multiply(1.0 / 3.0);
        World    world   = dropLoc.getWorld();
        Item     item    = world.dropItem(dropLoc, dropItem);
        item.setVelocity(lookVec);
    }
}
