package com.mikedeejay2.simplestack.system.itemclick.preprocess.clicktype;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocess.ItemClickPreprocess;
import org.bukkit.GameMode;
import org.bukkit.event.inventory.InventoryAction;

public class PreprocessMiddle implements ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        GameMode gamemode = info.player.getGameMode();
        if(gamemode == GameMode.CREATIVE && !info.selectedNull)
        {
            info.setAction(InventoryAction.CLONE_STACK);
        }
    }
}
