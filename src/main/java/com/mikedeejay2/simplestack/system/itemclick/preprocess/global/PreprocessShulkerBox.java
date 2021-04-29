package com.mikedeejay2.simplestack.system.itemclick.preprocess.global;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocess.ItemClickPreprocess;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryAction;

public class PreprocessShulkerBox implements ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.cursor == null || info.topInv == null) return;
        if(!InventoryIdentifiers.isShulkerBox(info.cursor.getType()) || info.topInv.getLocation() == null) return;
        Location location = info.topInv.getLocation();
        World world = location.getWorld();
        Block block = world.getBlockAt(location);
        Material blockType = block.getType();
        if(!InventoryIdentifiers.isShulkerBox(blockType)) return;
        info.setAction(InventoryAction.NOTHING);
    }
}
