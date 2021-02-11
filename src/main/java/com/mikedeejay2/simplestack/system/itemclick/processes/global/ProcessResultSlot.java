package com.mikedeejay2.simplestack.system.itemclick.processes.global;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIterator;
import com.mikedeejay2.mikedeejay2lib.util.item.InventoryOrder;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.CheckUtils;
import com.mikedeejay2.simplestack.util.MoveUtils;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessResultSlot implements ItemClickProcess
{
    protected final Simplestack plugin;

    public ProcessResultSlot(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.slotType != InventoryType.SlotType.RESULT) return;
        if(!CheckUtils.takeResult(info)) return;
        int stackAmt = info.selected.getAmount();
        if(stackAmt <= 0) return;
        Inventory   toInv    = info.bottomInv;
        ItemStack[] toItems  = toInv.getStorageContents();
        int selectedAmt = info.selected.getAmount();

        int start = 0;
        int end   = toItems.length;
        if(info.clickedInvType == InventoryType.PLAYER)
        {
            if(info.slotType == InventoryType.SlotType.QUICKBAR)
            {
                start = 9;
            }
            else
            {
                end = 9;
            }
        }
        for(int i = start; i < end; ++i)
        {
            ItemStack item = toItems[i];
            if(item == null) continue;
            if(item.getType() == Material.AIR) continue;
            if(!ItemComparison.equalsEachOther(item, info.selected)) continue;
            int itemAmt = item.getAmount();
            if(itemAmt == info.selectedMax) continue;
            int newAmt = itemAmt + selectedAmt;
            if(newAmt > info.selectedMax)
            {
                selectedAmt = newAmt - info.selectedMax;
                newAmt = info.selectedMax;
            }
            else
            {
                selectedAmt = 0;
            }
            item.setAmount(newAmt);
            if(selectedAmt <= 0)
            {
                info.selected.setAmount(selectedAmt);
                return;
            }
        }
        for(int i = start; i < end; ++i)
        {
            ItemStack item = toItems[i];
            if(item != null && item.getType() != Material.AIR) continue;
            item = info.selected.clone();
            int newAmt = selectedAmt;
            if(newAmt > info.selectedMax)
            {
                selectedAmt = newAmt - info.selectedMax;
                newAmt = info.selectedMax;
            }
            else
            {
                selectedAmt -= newAmt;
            }
            item.setAmount(newAmt);
            toInv.setItem(i, item);
            if(selectedAmt <= 0)
            {
                info.selected.setAmount(0);
                return;
            }
        }
        info.selected.setAmount(selectedAmt);

        if(selectedAmt > 0)
        {
            MoveUtils.dropItemPlayer(info.player, info.selected.clone());
            info.selected.setAmount(0);
        }
    }
}
