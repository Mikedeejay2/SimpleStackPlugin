package com.mikedeejay2.simplestack.system.itemclick.handlers;

import com.mikedeejay2.mikedeejay2lib.util.debug.DebugTimer;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.SimpleStackHandler;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processor.ItemClickProcessor;
import com.mikedeejay2.simplestack.system.itemclick.preprocessor.ItemClickPreprocessor;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ItemClickHandler implements SimpleStackHandler<InventoryClickEvent>
{
    protected final Simplestack plugin;
    protected ItemClickProcessor processor;
    protected ItemClickPreprocessor preprocessor;

    public ItemClickHandler(Simplestack plugin)
    {
        this.plugin = plugin;
        this.processor = new ItemClickProcessor(plugin);
        this.preprocessor = new ItemClickPreprocessor(plugin);
    }

    public void initDefault()
    {
        preprocessor.initDefault();
        processor.initDefault();
    }

    @Override
    public void handle(InventoryClickEvent event)
    {
        /* DEBUG */ DebugTimer timer = new DebugTimer("Handle Inventory Click Event");
        ItemClickInfo info = new ItemClickInfo(event, plugin);
        timer.addPrintPoint("Initialize info");
        preprocessor.preprocess(info);
        /* DEBUG */ timer.addPrintPoint("Preprocess");

        processor.process(info, timer);
        /* DEBUG */ timer.addPrintPoint("Process");
        /* DEBUG */ timer.printReport(10);
        /* DEBUG */ info.player.sendMessage("Action NEW: " + info.getAction());
    }

    public ItemClickProcessor getProcessor()
    {
        return processor;
    }

    public ItemClickPreprocessor getPreprocessor()
    {
        return preprocessor;
    }
}
