package com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype;

import com.mikedeejay2.mikedeejay2lib.nms.xpcalc.NMS_XP;
import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ProcessGrindstone implements ItemClickProcess
{
    private static final Random random = new Random();

    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.rawSlot != 2) return;
        /* DEBUG */ System.out.println("Process Grindstone");
        if(!InventoryIdentifiers.takeResult(info.getAction())) return;

        GrindstoneInventory inventory = (GrindstoneInventory) info.topInv;
        ItemStack result   = inventory.getItem(2);
        Location  location = inventory.getLocation();
        World world = location.getWorld();
        if(result == null) return;
        boolean useMax = info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        int takeValue = 1;
        if(useMax)
        {
            takeValue = MoveUtils.resultSlotShift(info, 1);
        }
        if(takeValue == 0) return;
        inventory.setItem(2, null);

        NMS_XP xpCalculator = info.plugin.NMS().getXP();
        int xpAmt = xpCalculator.calculateXP(inventory.getItem(0), world);
        xpAmt += xpCalculator.calculateXP(inventory.getItem(1), world);
        xpCalculator.spawnXP(xpAmt, new Location(world, location.getBlockX(), location.getBlockY() + 0.5D, location.getBlockZ() + 0.5D));

        world.playSound(location, Sound.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1, 1);

        for(int i = 0; i < inventory.getSize() - 1; ++i)
        {
            ItemStack curItem = inventory.getItem(i);
            if(curItem == null) continue;
            if(curItem.getAmount() == 0) continue;
            curItem.setAmount(0);
        }
    }
}
