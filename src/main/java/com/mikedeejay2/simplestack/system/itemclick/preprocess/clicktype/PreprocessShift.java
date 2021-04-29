package com.mikedeejay2.simplestack.system.itemclick.preprocess.clicktype;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocess.ItemClickPreprocess;
import org.bukkit.event.inventory.InventoryAction;

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
