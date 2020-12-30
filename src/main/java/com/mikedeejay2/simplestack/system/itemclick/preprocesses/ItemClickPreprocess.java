package com.mikedeejay2.simplestack.system.itemclick.preprocesses;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.SimpleStackPreprocess;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickContainer;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ItemClickPreprocess extends ItemClickContainer implements SimpleStackPreprocess
{
    public ItemClickPreprocess(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }
}
