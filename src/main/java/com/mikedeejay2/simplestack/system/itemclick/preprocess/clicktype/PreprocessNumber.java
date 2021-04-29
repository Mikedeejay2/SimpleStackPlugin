package com.mikedeejay2.simplestack.system.itemclick.preprocess.clicktype;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocess.ItemClickPreprocess;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PreprocessNumber implements ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        ItemStack hotbarItem = info.bottomInv.getItem(info.hotbar);
        if(hotbarItem != null && hotbarItem.getType() == Material.AIR) hotbarItem = null;
        boolean   hotbarNull = hotbarItem == null;
        if(!hotbarNull)
        {
            Map.Entry<Boolean, Boolean> allowed = InventoryIdentifiers.applicableForSlot(info.rawSlot, info.invView, hotbarItem.getType());
            if(!allowed.getKey() && !allowed.getValue()) return;
        }
        if(info.clickedTop)
        {
            if(!hotbarNull && !info.selectedNull)
            {
                info.setAction(InventoryAction.HOTBAR_MOVE_AND_READD);
            }
            else if(!hotbarNull || !info.selectedNull)
            {
                info.setAction(InventoryAction.HOTBAR_SWAP);
            }
        }
        else if(!hotbarNull || !info.selectedNull)
        {
            info.setAction(InventoryAction.HOTBAR_SWAP);
        }
    }
}
