package com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.mikedeejay2lib.util.math.XPUtil;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
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

//        int xpAmt = 0; // TODO: NMS access for calculating proper grindstone XP output
//        for(int i = 0; i < inventory.getSize() - 1; ++i)
//        {
//            ItemStack item = inventory.getItem(i);
//            if(item == null) continue;
//            Map<Enchantment, Integer> enchantMap = item.getEnchantments();
//            for(Map.Entry<Enchantment, Integer> entry : enchantMap.entrySet())
//            {
//                Enchantment enchantment = entry.getKey();
//                int enchantLevel = entry.getValue();
//                xpAmt += 1 + enchantLevel * 10;
//            }
//        }
//        int modifier = (int)Math.ceil((double)xpAmt / 2.0D);
//        xpAmt += modifier + random.nextInt(modifier);
//        while(xpAmt > 0)
//        {
//            int finalXP = XPUtil.getOrbValue(xpAmt);
//            xpAmt -= finalXP;
//            ExperienceOrb orb = (ExperienceOrb) world.spawnEntity(location, EntityType.EXPERIENCE_ORB);
//            orb.setExperience(finalXP);
//        }
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
