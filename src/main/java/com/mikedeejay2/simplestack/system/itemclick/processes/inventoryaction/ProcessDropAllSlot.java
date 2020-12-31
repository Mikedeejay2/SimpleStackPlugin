package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public class ProcessDropAllSlot implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        Location dropLoc = info.player.getEyeLocation();
        Vector   lookVec = dropLoc.getDirection().multiply(1.0 / 3.0);
        World    world   = dropLoc.getWorld();
        Item     item    = world.dropItem(dropLoc, info.selected);
        item.setVelocity(lookVec);
        info.clickedInv.setItem(info.slot, null);
    }
}
