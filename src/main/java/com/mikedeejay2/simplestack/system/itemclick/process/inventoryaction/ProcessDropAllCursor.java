package com.mikedeejay2.simplestack.system.itemclick.process.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;

public class ProcessDropAllCursor implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        MoveUtils.dropItemPlayer(info.player, info.cursor);
        info.player.setItemOnCursor(null);
    }
}
