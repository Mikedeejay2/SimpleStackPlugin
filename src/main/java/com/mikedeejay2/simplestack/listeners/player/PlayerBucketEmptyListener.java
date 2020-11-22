package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CancelUtils;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Listens for Player Bucket Empty events
 *
 * @author Mikedeejay2
 */
public class PlayerBucketEmptyListener implements Listener
{
    private final Simplestack plugin;

    public PlayerBucketEmptyListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Fix bug where player uses all buckets in a stack at a time.
     *
     * @param event The event being called
     */
    @EventHandler
    public void playerBucketEmptyEvent(PlayerBucketEmptyEvent event)
    {
        Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE) return;
        if(CancelUtils.cancelPlayerCheck(plugin, player)) return;
        int slot = player.getInventory().getHeldItemSlot();
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(!stack.getType().toString().endsWith("BUCKET"))
        {
            slot = 40;
            stack = player.getInventory().getItemInOffHand();
        }
        if(CancelUtils.cancelStackCheck(plugin, stack)) return;
        PlayerInventory inv = player.getInventory();
        if(stack.getAmount() <= 1) return;
        stack.setAmount(stack.getAmount()-1);
        MoveUtils.moveItem(plugin, new ItemStack(Material.BUCKET), inv, slot, inv, 0, 36, false);


        int finalSlot = slot;
        ItemStack finalStack = stack;
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                inv.setItem(finalSlot, finalStack);
                player.updateInventory();
            }
        }.runTask(plugin);
    }
}
