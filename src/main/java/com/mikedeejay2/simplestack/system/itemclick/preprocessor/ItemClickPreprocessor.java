package com.mikedeejay2.simplestack.system.itemclick.preprocessor;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.SimpleStackPreprocessor;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocesses.*;
import com.mikedeejay2.simplestack.system.itemclick.preprocesses.global.PreprocessResultSlot;

import java.util.*;

public class ItemClickPreprocessor implements SimpleStackPreprocessor
{
    protected final Simplestack plugin;

    protected List<ItemClickPreprocess> processes;

    public ItemClickPreprocessor(Simplestack plugin)
    {
        this.plugin = plugin;
        this.processes = new ArrayList<>();
    }

    public void initDefault()
    {
        PreprocessItemClick itemClick = new PreprocessItemClick(plugin);
        itemClick.initDefault();
        addPreprocess(itemClick);
        PreprocessResultSlot resultSlot = new PreprocessResultSlot();
        addPreprocess(resultSlot);
    }

    public void preprocess(ItemClickInfo info)
    {
        for(ItemClickPreprocess process : processes)
        {
            process.invoke(info);
        }
    }

    public ItemClickPreprocessor addPreprocess(ItemClickPreprocess processList)
    {
        processes.add(processList);
        return this;
    }

    public ItemClickPreprocessor removePreprocess(int index)
    {
        processes.remove(index);
        return this;
    }

    public ItemClickPreprocessor removePreprocess(ItemClickPreprocess preprocess)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            ItemClickPreprocess curProcess = processes.get(i);
            if(!preprocess.equals(curProcess)) continue;
            processes.remove(i);
            break;
        }
        return this;
    }

    public ItemClickPreprocessor removePreprocess(Class<? extends ItemClickPreprocess> preprocessClass)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            ItemClickPreprocess preprocess = processes.get(i);
            if(preprocessClass != preprocess.getClass()) continue;
            processes.remove(i);
            break;
        }
        return this;
    }

    public boolean containsPreprocess(ItemClickPreprocess preprocess)
    {
        for(ItemClickPreprocess curProcess : processes)
        {
            if(!preprocess.equals(curProcess)) return true;
        }
        return false;
    }

    public boolean containsPreprocess(Class<? extends ItemClickPreprocess> preprocessClass)
    {
        for(ItemClickPreprocess preprocess : processes)
        {
            if(preprocessClass == preprocess.getClass()) return true;
        }
        return false;
    }

    public ItemClickPreprocess getPreprocess(int index)
    {
        return processes.get(index);
    }

    public <T extends ItemClickPreprocess> T getPreprocess(Class<T> preprocessClass)
    {
        for(ItemClickPreprocess preprocess : processes)
        {
            if(preprocessClass == preprocess.getClass()) return (T) preprocess;
        }
        return null;
    }

    public ItemClickPreprocessor resetPreprocesses()
    {
        processes.clear();
        return this;
    }
}
