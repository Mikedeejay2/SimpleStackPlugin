package com.mikedeejay2.simplestack.system.itemclick;

import com.mikedeejay2.mikedeejay2lib.util.debug.DebugTimer;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.generic.HandlerBase;
import com.mikedeejay2.simplestack.system.generic.SimpleStackHandler;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcessor;
import com.mikedeejay2.simplestack.system.itemclick.preprocess.ItemClickPreprocessor;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ItemClickHandler extends HandlerBase<InventoryClickEvent, ItemClickPreprocessor, ItemClickProcessor>
{
    public ItemClickHandler(Simplestack plugin)
    {
        super(plugin, new ItemClickPreprocessor(plugin), new ItemClickProcessor(plugin));
    }

    public void initDefault()
    {
        preprocessor.initDefault();
        processor.initDefault();
    }

    @Override
    public void handle(InventoryClickEvent event)
    {
        DebugTimer timer = new DebugTimer("Handle Inventory Click Event");
        ItemClickInfo info = new ItemClickInfo(event, plugin);
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
            info.player.sendMessage("Action NEW: " + info.getAction());
        }
    }
}
