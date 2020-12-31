package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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
        Location dropLoc = info.player.getEyeLocation();
        Vector   lookVec = dropLoc.getDirection().multiply(1.0 / 3.0);
        World    world   = dropLoc.getWorld();
        Item     item    = world.dropItem(dropLoc, dropItem);
        item.setVelocity(lookVec);
    }
}
