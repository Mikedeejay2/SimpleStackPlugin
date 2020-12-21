package com.mikedeejay2.simplestack.handlers;

import org.bukkit.event.Event;

public interface SimpleStackHandler<E extends Event>
{
    void handle(E event);
}
