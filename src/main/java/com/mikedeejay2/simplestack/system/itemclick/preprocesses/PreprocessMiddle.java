package com.mikedeejay2.simplestack.system.itemclick.preprocesses;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.util.InvActionStruct;
import org.bukkit.GameMode;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

public class PreprocessMiddle extends ItemClickPreprocess
{
    @Override
    protected void invoke(ItemClickInfo info, InvActionStruct action)
    {
        if(info.clickType != ClickType.MIDDLE) return;
        GameMode gamemode = info.player.getGameMode();
        if(gamemode == GameMode.CREATIVE && !info.selectedNull)
        {
            action.setAction(InventoryAction.CLONE_STACK);
        }
    }
}
