package com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.mikedeejay2lib.util.map.MapUtil;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

public class ProcessCartography implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.rawSlot != 2) return;
        /* DEBUG */ System.out.println("Process Cartography");
        if(!InventoryIdentifiers.takeResult(info.getAction())) return;
        Inventory inventory = info.topInv;
        ItemStack result = inventory.getItem(2);
        ItemStack input1 = inventory.getItem(0);
        ItemStack input2 = inventory.getItem(1);
        if(result == null || input1 == null || input2 == null) return;
        boolean useMax = info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        int takeValue = 1;
        if(useMax)
        {
            int maxTake = Math.min(input1.getAmount(), input2.getAmount());

            takeValue = MoveUtils.resultSlotShift(info, maxTake);
        }
        MapMeta meta = (MapMeta) result.getItemMeta();
        MapUtil.incrementMapSize(meta);
        result.setItemMeta(meta);

        input1.setAmount(input1.getAmount() - takeValue);
        input2.setAmount(input2.getAmount() - takeValue);
    }
}
