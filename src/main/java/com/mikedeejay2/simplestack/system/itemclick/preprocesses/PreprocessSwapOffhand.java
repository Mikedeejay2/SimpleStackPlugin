package com.mikedeejay2.simplestack.system.itemclick.preprocesses;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.util.InvActionStruct;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public class PreprocessSwapOffhand extends ItemClickPreprocess
{
    @Override
    protected void invoke(ItemClickInfo info, InvActionStruct action)
    {
        if(info.clickType != ClickType.SWAP_OFFHAND) return;
        ItemStack offhand = info.bottomInv.getItem(40);
        if(offhand != null && offhand.getType() == Material.AIR) offhand = null;
        int offhandMax = offhand == null ? 0 : StackUtils.getMaxAmount(info.plugin, offhand);
        int offhandAmt = offhand == null ? 0 : offhand.getAmount();
        boolean offhandNull = offhand == null;
        if(!info.selectedNull || !offhandNull)
        {
            if(info.selectedAmt <= info.selectedMax && offhandAmt <= offhandMax)
            {
                action.setAction(InventoryAction.HOTBAR_SWAP);
            }
        }
    }
}
