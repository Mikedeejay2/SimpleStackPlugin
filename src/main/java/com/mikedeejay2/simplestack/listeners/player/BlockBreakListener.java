package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CancelUtils;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Listens for block breaking events
 *
 * @author Mikedeejay2
 */
public class BlockBreakListener implements Listener
{
    private final Simplestack plugin;

    public BlockBreakListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * BlockBreakEvent
     * This is needed for when a shulker box breaks, because by default Minecraft
     * unstacks items inside of a shulker box automatically
     *
     * @param event The event being activated
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void blockBreakEvent(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if(!InventoryIdentifiers.isShulkerBox(block.getType())) return;
        if(CancelUtils.cancelPlayerCheck(plugin, player)) return;

        MoveUtils.preserveShulkerBox(event, block);
    }
}
