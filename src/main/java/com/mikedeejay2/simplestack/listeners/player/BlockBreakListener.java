package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

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
    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        if(plugin.cancelUtils().cancelPlayerCheck(player)) return;
        Block block = event.getBlock();
        if(!block.getType().toString().endsWith("SHULKER_BOX")) return;

        boolean cancel = plugin.cancelUtils().cancelStackCheck((ItemStack) block.getDrops().toArray()[0]);
        if(cancel) return;

        plugin.moveUtils().preserveShulkerBox(event, block);
    }
}
