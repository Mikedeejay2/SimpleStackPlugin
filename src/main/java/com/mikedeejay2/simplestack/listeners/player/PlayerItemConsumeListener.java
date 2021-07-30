package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
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
        if(CancelUtils.cancelStackCheck(plugin, stack)) return;
        PlayerInventory inv = player.getInventory();
        if(InventoryIdentifiers.isSoup(stack.getType()))
        {
            if(player.getGameMode() == GameMode.CREATIVE) return;
            int slot = inv.getHeldItemSlot();
            if(!stack.equals(inv.getItemInMainHand()))
            {
                slot = InventoryIdentifiers.OFFHAND_SLOT;
            }
            if(stack.getAmount() <= 1) return;
            stack.setAmount(stack.getAmount() - 1);
            MoveUtils.moveItem(plugin, new ItemStack(Material.BOWL, 1), inv);

            int finalSlot = slot;
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    inv.setItem(finalSlot, stack);
                    player.updateInventory();
                }
            }.runTask(plugin);
        }
        else if(InventoryIdentifiers.isBucket(stack.getType()))
        {
            if(player.getGameMode() == GameMode.CREATIVE) return;
            if(stack.getAmount() <= 1) return;
            MoveUtils.moveItem(plugin, new ItemStack(Material.BUCKET, 1), inv);
        }
    }
}