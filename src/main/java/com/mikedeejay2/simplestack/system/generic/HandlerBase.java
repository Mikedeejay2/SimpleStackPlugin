package com.mikedeejay2.simplestack.system.generic;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.event.Event;

public abstract class HandlerBase<E extends Event, P1 extends SimpleStackPreprocessor<?>, P2 extends SimpleStackProcessor<?>> implements SimpleStackHandler<E>
{
    protected final Simplestack plugin;

    protected final P1 preprocessor;
    protected final P2 processor;

    public HandlerBase(Simplestack plugin, P1 preprocessor, P2 processor)
    {
        this.plugin = plugin;
        this.preprocessor = preprocessor;
        this.processor = processor;
    }

    public abstract void initDefault();

    public P1 getPreprocessor()
    {
        return preprocessor;
    }

    public P2 getProcessor()
    {
        return processor;
    }
}
