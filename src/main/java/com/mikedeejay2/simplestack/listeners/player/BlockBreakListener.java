package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

    /*
     * BlockBreakEvent
     * This is needed for when a shulker box breaks, because by default Minecraft
     * unstacks items inside of a shulker box automatically
     */
    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        if(StackUtils.cancelPlayerCheck(player)) return;
        Block block = event.getBlock();
        if(!block.getType().toString().endsWith("SHULKER_BOX")) return;

        boolean cancel = StackUtils.cancelStackCheck(block.getType());
        if(cancel) return;

        StackUtils.preserveShulkerBox(event, block);
    }
}
