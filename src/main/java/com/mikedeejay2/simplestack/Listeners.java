package com.mikedeejay2.simplestack;

import org.bukkit.Bukkit;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class Listeners implements Listener
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

    private static final String permission = "simplestack.use";

    /*
     * InventoryClickEvent handler,
     * This does a majority of the work for this plugin.
     */
    @EventHandler
    public void stackEvent(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        StackUtils.updateAnvilManual(player.getOpenInventory().getTopInventory());
        if(!player.hasPermission(permission)) return;
        ItemStack itemPickUp = event.getCurrentItem();
        ItemStack itemPutDown = event.getCursor();
        ClickType clickType = event.getClick();
        if(itemPickUp == null || clickType.equals(ClickType.CREATIVE)) return;

        boolean cancel = StackUtils.cancelStackCheck(itemPickUp.getType());
        if(cancel || event.isCancelled())
        {
            return;
        }
        event.setCancelled(true);

        StackUtils.makeUnique(itemPickUp, plugin.getKey());

        switch(clickType)
        {
            case LEFT:
                StackUtils.leftClick(itemPickUp, itemPutDown, player, event);
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                StackUtils.shiftClick(itemPickUp, player, event);
                break;
            case RIGHT:
                StackUtils.rightClick(itemPickUp, itemPutDown, player, event);
                break;
        }
    }

    /*
     * EntityPickupItemEvent
     * This is for when multiple unstackable items are on the ground and
     * are picked up by a player.
     * This code will automatically stack them in their inventory as if they
     * were a stack of 64.
     */
    @EventHandler
    public void entityPickupItemEvent(EntityPickupItemEvent event)
    {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if(!player.hasPermission(permission)) return;
        ItemStack item = event.getItem().getItemStack();

        boolean cancel = StackUtils.cancelStackCheck(item.getType());
        if(cancel) return;

        StackUtils.moveItemToInventory(event, event.getItem(), player, item);
    }

    /*
     * BlockBreakEvent
     * This is needed for when a shulker box breaks, because by default Minecraft
     * unstacks items inside of a shulker box automatically
     */
    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        if(!player.hasPermission(permission)) return;
        Block block = event.getBlock();
        if(!block.getType().toString().endsWith("SHULKER_BOX")) return;

        boolean cancel = StackUtils.cancelStackCheck(block.getType());
        if(cancel) return;

        StackUtils.preserveShulkerBox(event, block);
    }

    @EventHandler
    public void inventoryMoveItemEvent(InventoryMoveItemEvent event)
    {
        ItemStack item = event.getItem();

        boolean cancel = StackUtils.cancelStackCheck(item.getType());
        if(cancel) return;
        event.setCancelled(true);

        Inventory fromInv = event.getSource();
        Inventory toInv = event.getDestination();
        int amountBeingMoved = item.getAmount();

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                StackUtils.moveItemToInventory(item, fromInv, toInv, amountBeingMoved);
            }
        }.runTaskLater(plugin, 0);

    }

    @EventHandler
    public void craftingTableCloseEvent(InventoryCloseEvent event)
    {
        Inventory inv = event.getInventory();
        InventoryType type = inv.getType();
        if(!(type.equals(InventoryType.WORKBENCH) ||
        type.equals(InventoryType.ENCHANTING) ||
        type.equals(InventoryType.ANVIL))) return;
        Player player = (Player) event.getPlayer();
        Inventory playerInv = player.getInventory();
        StackUtils.moveAllItemsToPlayerInv(inv, player, playerInv);
    }

    @EventHandler
    public void prepareAnvilEvent(PrepareAnvilEvent event)
    {
        AnvilInventory inv = event.getInventory();
        ItemStack result = event.getResult();
        ItemStack item1 = inv.getItem(0);
        ItemStack item2 = inv.getItem(1);
        if(item2 == null || result == null || item1 == null) return;
        if(item1.getAmount() < item2.getAmount())
        {
            result.setAmount(item1.getAmount());
        }
        else if(item1.getAmount() > item2.getAmount())
        {
            result.setAmount(item2.getAmount());
        }
        else if(item1.getAmount() == item2.getAmount())
        {
            result.setAmount(item1.getAmount());
        }
    }

    @EventHandler
    public void prepareSmithingEvent(PrepareSmithingEvent event)
    {
        SmithingInventory inv = event.getInventory();
        ItemStack result = event.getResult();
        ItemStack item1 = inv.getItem(0);
        ItemStack item2 = inv.getItem(1);
        if(item2 == null || result == null || item1 == null) return;
        if(item1.getAmount() < item2.getAmount())
        {
            result.setAmount(item1.getAmount());
        }
        else if(item1.getAmount() > item2.getAmount())
        {
            result.setAmount(item2.getAmount());
        }
        else if(item1.getAmount() == item2.getAmount())
        {
            result.setAmount(item1.getAmount());
        }
    }

//    @EventHandler
//    public void event(InventoryDragEvent event)
//    {
//
//    }
}
