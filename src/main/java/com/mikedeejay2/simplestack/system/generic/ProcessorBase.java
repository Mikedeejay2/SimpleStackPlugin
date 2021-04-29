package com.mikedeejay2.simplestack.system.generic;

import com.mikedeejay2.mikedeejay2lib.util.debug.DebugTimer;
import com.mikedeejay2.simplestack.Simplestack;

import java.util.ArrayList;
import java.util.List;

public abstract class ProcessorBase<T extends SimpleStackProcess<I>, I, R extends ProcessorBase<T, I, R>> implements SimpleStackProcessor<I>
{
    protected final Simplestack plugin;

    protected List<T> processes;

    public ProcessorBase(Simplestack plugin)
    {
        this.plugin = plugin;
        this.processes = new ArrayList<>();
    }

    public abstract void initDefault();

    @Override
    public void process(I info, DebugTimer timer)
    {
        for(T process : processes)
        {
            process.invoke(info);
            if(plugin.getDebugConfig().isPrintTimings())
            {
                timer.addPrintPoint("Process `" + process.getClass().getSimpleName() + "`");
            }
        }
    }

    public R addProcess(T process)
    {
        processes.add(process);
        return (R) this;
    }

    public R removeProcess(int index)
    {
        processes.remove(index);
        return (R) this;
    }

    public R removeProcess(T process)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            T curProcess = processes.get(i);
            if(!process.equals(curProcess)) continue;
            processes.remove(i);
            break;
        }
        return (R) this;
    }

    public R removeProcess(Class<? extends T> processClass)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            T process = processes.get(i);
            if(processClass != process.getClass()) continue;
            processes.remove(i);
            break;
        }
        return (R) this;
    }

    public boolean containsProcess(T process)
    {
        for(T curProcess : processes)
        {
            if(!process.equals(curProcess)) return true;
        }
        return false;
    }

    public boolean containsProcess(Class<? extends T> processClass)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            T process = processes.get(i);
            if(processClass == process.getClass()) return true;
        }
        return false;
    }

    public T getProcess(int index)
    {
        return processes.get(index);
    }

    public <Z extends T> Z getProcess(Class<Z> ProcessClass)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            T process = processes.get(i);
            if(ProcessClass == process.getClass()) return (Z) process;
        }
        return null;
    }

    public R resetProcesses()
    {
        processes.clear();
        return (R) this;
    }
}
