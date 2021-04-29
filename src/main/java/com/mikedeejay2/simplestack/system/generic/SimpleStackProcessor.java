package com.mikedeejay2.simplestack.system.generic;

import com.mikedeejay2.mikedeejay2lib.util.debug.DebugTimer;

public interface SimpleStackProcessor<T>
{
    void process(T data, DebugTimer timer);
}
