package com.mikedeejay2.simplestack.system.itemclick.preprocesses.clicktype;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocesses.ItemClickPreprocess;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public class PreprocessSwapOffhand implements ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        ItemStack offhand = info.bottomInv.getItem(40);
        if(offhand != null && offhand.getType() == Material.AIR) offhand = null;
        int offhandMax = offhand == null ? 0 : StackUtils.getMaxAmount(info.plugin, offhand);
        int offhandAmt = offhand == null ? 0 : offhand.getAmount();
        boolean offhandNull = offhand == null;
        if(!info.selectedNull || !offhandNull)
        {
            if(info.selectedAmt <= info.selectedMax && offhandAmt <= offhandMax)
            {
                info.setAction(InventoryAction.HOTBAR_SWAP);
            }
        }
    }
}
