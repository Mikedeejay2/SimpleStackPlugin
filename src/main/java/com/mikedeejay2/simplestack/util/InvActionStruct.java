package com.mikedeejay2.simplestack.util;

import org.bukkit.event.inventory.InventoryAction;

public final class InvActionStruct
{
    private InventoryAction action;

    public InvActionStruct(InventoryAction action)
    {
        this.action = action;
    }

    public InvActionStruct()
    {
        this.action = null;
    }

    public InventoryAction getAction()
    {
        return action;
    }

    public void setAction(InventoryAction action)
    {
        this.action = action;
    }
}
