package com.mikedeejay2.simplestack.system.generic;

import com.mikedeejay2.simplestack.Simplestack;

import java.util.ArrayList;
import java.util.List;

public abstract class PreprocessorBase<T extends SimpleStackPreprocess<I>, I, R extends PreprocessorBase<T, I, R>> implements SimpleStackPreprocessor<I>
{
    protected final Simplestack plugin;

    protected List<T> processes;

    public PreprocessorBase(Simplestack plugin)
    {
        this.plugin = plugin;
        this.processes = new ArrayList<>();
    }

    public abstract void initDefault();

    @Override
    public void preprocess(I info)
    {
        for(T process : processes)
        {
            process.invoke(info);
        }
    }

    public R addPreprocess(T preprocess)
    {
        processes.add(preprocess);
        return (R) this;
    }

    public R removePreprocess(int index)
    {
        processes.remove(index);
        return (R) this;
    }

    public R removePreprocess(T preprocess)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            T curProcess = processes.get(i);
            if(!preprocess.equals(curProcess)) continue;
            processes.remove(i);
            break;
        }
        return (R) this;
    }

    public R removePreprocess(Class<? extends T> preprocessClass)
    {
        for(int i = 0; i < processes.size(); ++i)
        {
            T preprocess = processes.get(i);
            if(preprocessClass != preprocess.getClass()) continue;
            processes.remove(i);
            break;
        }
        return (R) this;
    }

    public boolean containsPreprocess(T preprocess)
    {
        for(T curProcess : processes)
        {
            if(!preprocess.equals(curProcess)) return true;
        }
        return false;
    }

    public boolean containsPreprocess(Class<? extends T> preprocessClass)
    {
        for(T preprocess : processes)
        {
            if(preprocessClass == preprocess.getClass()) return true;
        }
        return false;
    }

    public T getPreprocess(int index)
    {
        return processes.get(index);
    }

    public <Z extends T> Z getPreprocess(Class<Z> preprocessClass)
    {
        for(T preprocess : processes)
        {
            if(preprocessClass == preprocess.getClass()) return (Z) preprocess;
        }
        return null;
    }

    public R resetPreprocesses()
    {
        processes.clear();
        return (R) this;
    }
}
