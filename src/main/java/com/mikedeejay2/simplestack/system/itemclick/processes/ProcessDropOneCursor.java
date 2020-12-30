package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ProcessDropOneCursor extends ItemClickProcess
{
    public ProcessDropOneCursor(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }

    @Override
    public void invoke()
    {
        int dropAmt = 1;
        int extraAmt = cursorAmt - 1;
        if(extraAmt < 0) return;
        ItemStack dropItem = cursor.clone();
        dropItem.setAmount(dropAmt);
        cursor.setAmount(extraAmt);
        player.setItemOnCursor(cursor);
        Location dropLoc = player.getEyeLocation();
        Vector   lookVec = dropLoc.getDirection().multiply(1.0 / 3.0);
        World    world   = dropLoc.getWorld();
        Item     item    = world.dropItem(dropLoc, dropItem);
        item.setVelocity(lookVec);
    }
}
