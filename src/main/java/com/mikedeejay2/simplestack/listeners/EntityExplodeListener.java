package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.MoveUtils;
import com.mikedeejay2.simplestack.util.ShulkerBoxes;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Listens for block explode events
 *
 * @author Mikedeejay2
 */
public class EntityExplodeListener implements Listener
{
    private final Simplestack plugin;

    public EntityExplodeListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * BlockExplodeEvent
     * This is needed for when a shulker box breaks, because by default Minecraft
     * unstacks items inside of a shulker box automatically
     *
     * @param event The event being activated
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void blockExplodeEvent(EntityExplodeEvent event)
    {
        for(Block block : event.blockList())
        {
            if(!ShulkerBoxes.isShulkerBox(block.getType())) continue;
            MoveUtils.preserveShulkerBox(block);
        }
    }
}
