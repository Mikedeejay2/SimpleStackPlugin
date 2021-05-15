package com.mikedeejay2.simplestack.system.itemdrag;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.generic.HandlerBase;
import com.mikedeejay2.simplestack.system.itemdrag.preprocess.ItemDragPreprocessor;
import com.mikedeejay2.simplestack.system.itemdrag.process.ItemDragProcessor;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ItemDragHandler extends HandlerBase<InventoryClickEvent, ItemDragPreprocessor, ItemDragProcessor>
{
    public ItemDragHandler(Simplestack plugin)
    {
        super(plugin, new ItemDragPreprocessor(plugin), new ItemDragProcessor(plugin));
    }

    @Override
    public void initDefault()
    {
        preprocessor.initDefault();
        processor.initDefault();
    }

    @Override
    public void handle(InventoryClickEvent event)
    {

    }
}
