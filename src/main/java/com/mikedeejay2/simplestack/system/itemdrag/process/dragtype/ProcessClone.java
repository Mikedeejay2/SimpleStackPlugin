package com.mikedeejay2.simplestack.system.itemdrag.process.dragtype;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.Pair;
import com.mikedeejay2.simplestack.system.itemdrag.ItemDragInfo;
import com.mikedeejay2.simplestack.system.itemdrag.process.ItemDragProcess;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ProcessClone implements ItemDragProcess
{
    @Override
    public void invoke(ItemDragInfo data)
    {
        ItemStack originalItem = data.oldCursor;
        int maxAmt = StackUtils.getMaxAmount(data.plugin, originalItem);
        for(int slot : data.rawSlots)
        {
            int amtToMove = maxAmt;
            Pair<Boolean, Boolean> applicable = InventoryIdentifiers.applicableForSlot(slot, data.invView, originalItem.getType());
            if(!applicable.getLeft() && !applicable.getRight()) continue;
            boolean singletonSlot = InventoryIdentifiers.singletonSlot(slot, data.invView);
            if(singletonSlot)
            {
                amtToMove = 1;
            }
            ItemStack curItem = data.invView.getItem(slot);
            if(curItem != null && curItem.getAmount() > 0 && singletonSlot)
            {
                continue;
            }
            if(curItem != null && !ItemComparison.equalsEachOther(originalItem, curItem) && curItem.getType() != Material.AIR) continue;
            ItemStack newItem = originalItem.clone();
            newItem.setAmount(amtToMove);
            data.invView.setItem(slot, newItem);
        }
        Bukkit.getScheduler().runTask(
            data.plugin, () -> data.player.setItemOnCursor(new ItemStack(Material.AIR)));
    }
}
