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
        this.processor = new ItemClickProcessor();
        this.preprocessor = new ItemClickPreprocessor();
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
        preprocessor.preprocess(info);

        info.player.sendMessage("Action NEW: " + info.getAction());
        processor.process(info);
        timer.printReport();
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
