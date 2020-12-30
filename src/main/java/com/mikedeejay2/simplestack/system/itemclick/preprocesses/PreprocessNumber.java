package com.mikedeejay2.simplestack.system.itemclick.preprocesses;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.util.InvActionStruct;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public class PreprocessNumber extends ItemClickPreprocess
{
    @Override
    protected void invoke(ItemClickInfo info, InvActionStruct action)
    {
        if(info.clickType != ClickType.NUMBER_KEY) return;
        ItemStack hotbarItem = info.bottomInv.getItem(info.hotbar);
        if(hotbarItem != null && hotbarItem.getType() == Material.AIR) hotbarItem = null;
        boolean   hotbarNull = hotbarItem == null;
        if(info.clickedTop)
        {
            if(!hotbarNull && !info.selectedNull)
            {
                action.setAction(InventoryAction.HOTBAR_MOVE_AND_READD);
            }
            else if(!hotbarNull || !info.selectedNull)
            {
                action.setAction(InventoryAction.HOTBAR_SWAP);
            }
        }
        else if(!hotbarNull || !info.selectedNull)
        {
            action.setAction(InventoryAction.HOTBAR_SWAP);
        }
    }
}
