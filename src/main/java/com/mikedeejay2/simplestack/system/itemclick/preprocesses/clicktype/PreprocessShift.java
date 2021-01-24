package com.mikedeejay2.simplestack.system.itemclick.preprocesses.clicktype;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocesses.ItemClickPreprocess;
import com.mikedeejay2.simplestack.util.CheckUtils;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PreprocessShift implements ItemClickPreprocess
{
    private final Simplestack plugin;

    public PreprocessShift(Simplestack plugin)
    {
        this.plugin = plugin;
    }


    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.selectedNull) return;
        info.setAction(InventoryAction.MOVE_TO_OTHER_INVENTORY);
    }
}
