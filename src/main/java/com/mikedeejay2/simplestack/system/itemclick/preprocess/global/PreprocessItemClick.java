package com.mikedeejay2.simplestack.system.itemclick.preprocess.global;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocess.ItemClickPreprocess;
import com.mikedeejay2.simplestack.system.itemclick.preprocess.clicktype.*;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PreprocessItemClick implements ItemClickPreprocess
{
    protected final Simplestack plugin;

    protected Map<ClickType, List<ItemClickPreprocess>> clickPreprocesses;

    public PreprocessItemClick(Simplestack plugin)
    {
        this.plugin = plugin;
        this.clickPreprocesses = new EnumMap<>(ClickType.class);
        for(ClickType clickType : ClickType.values())
        {
            clickPreprocesses.put(clickType, new ArrayList<>());
        }
    }

    public void initDefault()
    {
        addPreprocess(ClickType.LEFT, new PreprocessLeft());
        ItemClickPreprocess preprocessShift = new PreprocessShift(plugin);
        addPreprocess(ClickType.SHIFT_LEFT, preprocessShift);
        addPreprocess(ClickType.SHIFT_RIGHT, preprocessShift);
        addPreprocess(ClickType.RIGHT, new PreprocessRight());
        addPreprocess(ClickType.MIDDLE, new PreprocessMiddle());
        addPreprocess(ClickType.NUMBER_KEY, new PreprocessNumber());
        addPreprocess(ClickType.DOUBLE_CLICK, new PreprocessDoubleClick());
        addPreprocess(ClickType.DROP, new PreprocessDrop());
        addPreprocess(ClickType.CONTROL_DROP, new PreprocessControlDrop());
        addPreprocess(ClickType.LEFT, new PreprocessLeft());
    }

    public PreprocessItemClick addPreprocess(ClickType type, ItemClickPreprocess process)
    {
        clickPreprocesses.get(type).add(process);
        return this;
    }

    @Override
    public void invoke(ItemClickInfo info)
    {
        clickPreprocesses.get(info.clickType).forEach(process -> process.invoke(info));
    }
}
