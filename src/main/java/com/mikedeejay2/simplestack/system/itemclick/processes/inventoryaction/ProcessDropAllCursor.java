package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public class ProcessDropAllCursor implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        MoveUtils.dropItemPlayer(info.player, info.cursor);
        info.player.setItemOnCursor(null);
    }
}
