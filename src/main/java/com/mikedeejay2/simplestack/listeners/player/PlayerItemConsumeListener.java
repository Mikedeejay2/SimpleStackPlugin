package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CancelUtils;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Listens for Player Bucket Empty events
 *
 * @author Mikedeejay2
 */
public class PlayerItemConsumeListener implements Listener
{
    private final Simplestack plugin;

    public PlayerItemConsumeListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Fix bug where stews would replace the rest of the
     *
     * @param event The event being called
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerItemConsumeEvent(PlayerItemConsumeEvent event)
    {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        if(!stack.getType().toString().endsWith("_STEW") && !stack.getType().toString().endsWith("_SOUP")) return;
        if(player.getGameMode() == GameMode.CREATIVE) return;
        if(CancelUtils.cancelPlayerCheck(plugin, player)) return;
        PlayerInventory inv = player.getInventory();
        int slot = inv.getHeldItemSlot();
        if(!stack.equals(inv.getItemInMainHand()))
        {
            slot = 40;
        }
        if(CancelUtils.cancelStackCheck(plugin, stack)) return;
        if(stack.getAmount() <= 1) return;
        stack.setAmount(stack.getAmount()-1);
        MoveUtils.moveItem(plugin, new ItemStack(Material.BOWL, 1), inv);

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
