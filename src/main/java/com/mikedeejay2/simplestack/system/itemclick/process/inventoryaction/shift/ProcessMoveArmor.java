package com.mikedeejay2.simplestack.system.itemclick.process.inventoryaction.shift;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessMoveArmor implements ItemClickProcess
{
    protected ItemClickProcess backupProcess;

    public ProcessMoveArmor(ItemClickProcess backupProcess)
    {
        this.backupProcess = backupProcess;
    }

    @Override
    public void invoke(ItemClickInfo info)
    {
        Inventory toInv = info.bottomInv;
        Material selectedMat = info.selected.getType();
        int selectedAmt = info.selectedAmt;

        int start = 9;
        int end = toInv.getSize() - 5;
        if(!InventoryIdentifiers.isArmorSlot(info.slot))
        {
            if(InventoryIdentifiers.isBoots(selectedMat))
            {
                start = InventoryIdentifiers.BOOTS_SLOT;
                end = InventoryIdentifiers.BOOTS_SLOT + 1;
            }
            else if(InventoryIdentifiers.isLeggings(selectedMat))
            {
                start = InventoryIdentifiers.LEGGINGS_SLOT;
                end = InventoryIdentifiers.LEGGINGS_SLOT + 1;
            }
            else if(InventoryIdentifiers.isChestplate(selectedMat))
            {
                start = InventoryIdentifiers.CHESTPLATE_SLOT;
                end = InventoryIdentifiers.CHESTPLATE_SLOT + 1;
            }
            else if(InventoryIdentifiers.isHelmet(selectedMat))
            {
                start = InventoryIdentifiers.HELMET_SLOT;
                end = InventoryIdentifiers.HELMET_SLOT + 1;
            }
        }

        for(int i = start; i < end; ++i)
        {
            ItemStack item = toInv.getItem(i);
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
            ItemStack item = toInv.getItem(i);
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
        if(selectedAmt == info.selectedAmt)
        {
            backupProcess.invoke(info);
        }
    }
}
