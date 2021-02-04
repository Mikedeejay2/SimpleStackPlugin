package com.mikedeejay2.simplestack.system.itemclick.processor;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.SimpleStackProcessor;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.*;

import java.util.*;

public class ItemClickProcessor implements SimpleStackProcessor
{
    protected final Simplestack plugin;

    protected List<ItemClickProcess> processes;

    public ItemClickProcessor(Simplestack plugin)
    {
        this.plugin = plugin;
        this.processes = new ArrayList<>();
    }

    public void initDefault()
    {
        ProcessInvType invType = new ProcessInvType(plugin);
        invType.initDefault();
        addProcess(invType);

        ProcessAction action = new ProcessAction(plugin);
        action.initDefault();
        addProcess(action);
    }

    public void process(ItemClickInfo info)
    {
        for(ItemClickProcess process : processes)
        {
            process.invoke(info);
        }
    }

    public ItemClickProcessor addProcess(ItemClickProcess processList)
    {
        processes.add(processList);
        return this;
    }

    public ItemClickProcessor removeProcess(int index)
    {
        processes.remove(index);
        return this;
    }

    public ItemClickProcessor removeProcess(ItemClickProcess Process)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            ItemClickProcess curProcess = processes.get(i);
            if(!Process.equals(curProcess)) continue;
            processes.remove(i);
            break;
        }
        return this;
    }

    public ItemClickProcessor removeProcess(Class<? extends ItemClickProcess> ProcessClass)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            ItemClickProcess process = processes.get(i);
            if(ProcessClass != process.getClass()) continue;
            processes.remove(i);
            break;
        }
        return this;
    }

    public boolean containsProcess(ItemClickProcess Process)
    {
        for(ItemClickProcess curProcess : processes)
        {
            if(!Process.equals(curProcess)) return true;
        }
        return false;
    }

    public boolean containsProcess(Class<? extends ItemClickProcess> ProcessClass)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            ItemClickProcess process = processes.get(i);
            if(ProcessClass == process.getClass()) return true;
        }
        return false;
    }

    public ItemClickProcess getProcess(int index)
    {
        return processes.get(index);
    }

    public <T extends ItemClickProcess> T getProcess(Class<T> ProcessClass)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            ItemClickProcess process = processes.get(i);
            if(ProcessClass == process.getClass()) return (T) process;
        }
        return null;
    }

    public ItemClickProcessor resetProcesses()
    {
        processes.clear();
        return this;
    }
}
