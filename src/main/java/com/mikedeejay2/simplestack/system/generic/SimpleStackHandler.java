package com.mikedeejay2.simplestack.system.generic;

import org.bukkit.event.Event;

@FunctionalInterface
public interface SimpleStackHandler<E extends Event>
{
    void handle(E event);
}
