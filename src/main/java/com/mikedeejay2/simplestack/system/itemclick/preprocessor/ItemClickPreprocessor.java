package com.mikedeejay2.simplestack.system.itemclick.preprocessor;

import com.mikedeejay2.simplestack.system.SimpleStackPreprocessor;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocesses.ItemClickPreprocess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ItemClickPreprocessor implements SimpleStackPreprocessor
{
    protected Map<Consumer<ItemClickInfo>, List<ItemClickPreprocess>> processes;

    public ItemClickPreprocessor()
    {
        this.processes = new HashMap<>();
    }

    public ItemClickPreprocessor addPreprocess(Consumer<ItemClickInfo> condition, List<ItemClickPreprocess> processList)
    {
        processes.put(condition, processList);
        return this;
    }

    public ItemClickPreprocessor addPreprocess(Consumer<ItemClickInfo> condition, ItemClickPreprocess... processList)
    {
        this.addPreprocess(condition, Arrays.asList(processList));
        return this;
    }
}
