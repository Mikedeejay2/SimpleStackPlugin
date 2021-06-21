package com.mikedeejay2.simplestack.system.itemdrag.process.dragtype;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.Pair;
import com.mikedeejay2.simplestack.system.itemdrag.ItemDragInfo;
import com.mikedeejay2.simplestack.system.itemdrag.process.ItemDragProcess;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ProcessEven implements ItemDragProcess
{
    @Override
    public void invoke(ItemDragInfo data)
    {
        System.out.println("Process Even");
        ItemStack originalItem = data.oldCursor;
        int amount = data.oldCursorAmount;
        int perSlot = amount / data.rawSlots.size();
        for(int slot : data.rawSlots)
        {
            if(amount == 0) break;
            int amtToMove = perSlot;
            Pair<Boolean, Boolean> applicable = InventoryIdentifiers.applicableForSlot(slot, data.invView, originalItem.getType());
            if(!applicable.getLeft() && !applicable.getRight()) continue;
            boolean singletonSlot = InventoryIdentifiers.singletonSlot(slot, data.invView);
            if(singletonSlot)
            {
                amtToMove = 1;
            }
            ItemStack curItem = data.invView.getItem(slot);
            int curAmt = 0;
            if(curItem != null)
            {
                curAmt = curItem.getAmount();
                if(curItem.getAmount() > 0 && singletonSlot) continue;
            }
            int newAmt = curAmt + amtToMove;
            if(curItem != null && !ItemComparison.equalsEachOther(originalItem, curItem) && curItem.getType() != Material.AIR) continue;
            if(newAmt > data.stackSize) continue;
            amount -= amtToMove;
            ItemStack newItem = originalItem.clone();
            newItem.setAmount(newAmt);
            data.invView.setItem(slot, newItem);
        }
        ItemStack cursorItem = originalItem.clone();
        cursorItem.setAmount(amount);
        Bukkit.getScheduler().runTask(
            data.plugin, () -> data.player.setItemOnCursor(cursorItem));
    }
}
