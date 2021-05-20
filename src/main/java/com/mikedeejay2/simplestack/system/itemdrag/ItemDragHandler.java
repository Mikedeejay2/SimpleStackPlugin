package com.mikedeejay2.simplestack.system.itemdrag;

import com.mikedeejay2.mikedeejay2lib.util.debug.DebugTimer;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.generic.HandlerBase;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemdrag.preprocess.ItemDragPreprocessor;
import com.mikedeejay2.simplestack.system.itemdrag.process.ItemDragProcessor;
import org.bukkit.event.inventory.InventoryDragEvent;

public class ItemDragHandler extends HandlerBase<InventoryDragEvent, ItemDragPreprocessor, ItemDragProcessor>
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
    public void handle(InventoryDragEvent event)
    {
        DebugTimer timer = new DebugTimer("Handle Inventory Drag Event");
        ItemDragInfo info = new ItemDragInfo(event, plugin);
        if(plugin.getDebugConfig().isPrintTimings())
        {
            timer.addPrintPoint("Initialize info");
        }
        preprocessor.preprocess(info);
        if(plugin.getDebugConfig().isPrintTimings())
        {
            timer.addPrintPoint("Preprocess");
        }

        processor.process(info, timer);
        if(plugin.getDebugConfig().isPrintTimings())
        {
            timer.printReport(10);
        }
        if(plugin.getDebugConfig().isPrintAction())
        {
            info.player.sendMessage("Drag type NEW: " + info.getType());
        }
    }
}
