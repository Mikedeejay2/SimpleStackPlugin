package com.mikedeejay2.simplestack.system.itemclick.executors;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.*;

public class ItemClickExecutorMovement implements ItemClickExecutor
{
    protected final Simplestack plugin;

    public ItemClickExecutorMovement(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void execNothing(ItemClickInfo info) {}

    @Override
    public void execPickupAll(ItemClickInfo info)
    {
        new ProcessPickupAll().invoke(info);
    }

    @Override
    public void execPickupSome(ItemClickInfo info)
    {
        new ProcessPickupSome().invoke(info);
    }

    @Override
    public void execPickupHalf(ItemClickInfo info)
    {
        new ProcessPickupHalf().invoke(info);
    }

    @Override
    public void execPickupOne(ItemClickInfo info)
    {
        new ProcessPickupOne().invoke(info);
    }

    @Override
    public void execPlaceAll(ItemClickInfo info)
    {
        new ProcessPlaceAll().invoke(info);
    }

    @Override
    public void execPlaceSome(ItemClickInfo info)
    {
        new ProcessPlaceSome().invoke(info);
    }

    @Override
    public void execPlaceOne(ItemClickInfo info)
    {
        new ProcessPlaceOne().invoke(info);
    }

    @Override
    public void execSwapWithCursor(ItemClickInfo info)
    {
        new ProcessSwapWithCursor().invoke(info);
    }

    @Override
    public void execDropAllCursor(ItemClickInfo info)
    {
        new ProcessDropAllCursor().invoke(info);
    }

    @Override
    public void execDropOneCursor(ItemClickInfo info)
    {
        new ProcessDropOneCursor().invoke(info);
    }

    @Override
    public void execDropAllSlot(ItemClickInfo info)
    {
        new ProcessDropAllSlot().invoke(info);
    }

    @Override
    public void execDropOneSlot(ItemClickInfo info)
    {
        new ProcessDropOneSlot().invoke(info);
    }

    @Override
    public void execMoveToOtherInventory(ItemClickInfo info)
    {
        new ProcessMoveToOtherInventory().invoke(info);
    }

    @Override
    public void execHotbarMoveAndReadd(ItemClickInfo info)
    {
        new ProcessHotbarMoveAndReadd().invoke(info);
    }

    @Override
    public void execHotbarSwap(ItemClickInfo info)
    {
        new ProcessHotbarSwap().invoke(info);
    }

    @Override
    public void execCloneStack(ItemClickInfo info)
    {
        new ProcessCloneStack().invoke(info);
    }

    @Override
    public void execCollectToCursor(ItemClickInfo info)
    {
        new ProcessCollectToCursor().invoke(info);
    }

    @Override
    public void execUnknown(ItemClickInfo info) {}
}
