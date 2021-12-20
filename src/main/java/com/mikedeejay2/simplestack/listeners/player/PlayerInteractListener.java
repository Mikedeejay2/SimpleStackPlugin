package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CancelUtils;
import com.mikedeejay2.simplestack.util.MoveUtils;
import com.mikedeejay2.simplestack.util.ShulkerBoxes;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Listens for block breaking events
 *
 * @author Mikedeejay2
 */
public class PlayerInteractListener implements Listener
{
    private final Simplestack plugin;

    public PlayerInteractListener(Simplestack plugin)
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
    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBreakEvent(PlayerInteractEvent event)
    {
        ItemStack item = event.getItem();
        if(item == null) return;
        if(event.getAction() != Action.RIGHT_CLICK_AIR &&
            event.getAction() != Action.RIGHT_CLICK_BLOCK ||
            !InventoryIdentifiers.isArmor(item.getType()))
            return;
        if(plugin.config().shouldStackArmor()) return;
        event.setCancelled(true);
        ItemStack armorItem = item.clone();
        armorItem.setAmount(1);
        ItemStack slotItem = item.clone();
        slotItem.setAmount(slotItem.getAmount() - 1);

        Material material = item.getType();
        int slot = -1;
        if(InventoryIdentifiers.isHelmet(material)) slot = InventoryIdentifiers.HELMET_SLOT;
        else if(InventoryIdentifiers.isChestplate(material)) slot = InventoryIdentifiers.CHESTPLATE_SLOT;
        else if(InventoryIdentifiers.isLeggings(material)) slot = InventoryIdentifiers.LEGGINGS_SLOT;
        else if(InventoryIdentifiers.isBoots(material)) slot = InventoryIdentifiers.BOOTS_SLOT;

        if(slot == -1) return;
        PlayerInventory inventory = event.getPlayer().getInventory();
        if(inventory.getItem(slot) != null) return;
        inventory.setItem(slot, armorItem);
        inventory.setItem(inventory.getHeldItemSlot(), slotItem);
    }
}
