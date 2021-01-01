package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction.shift;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessMoveHotbarReverse implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        info.player.sendMessage("Move to Hotbar Reverse");
    }
}
