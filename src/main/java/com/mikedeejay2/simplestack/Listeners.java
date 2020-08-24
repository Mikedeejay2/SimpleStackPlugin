package com.mikedeejay2.simplestack;

import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.config.ListMode;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class Listeners implements Listener
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

    // A namespaced key for adding a small piece of NBT data that makes each item "Unique".
    // This has to happen because if we don't make each item unique then the InventoryClickEvent won't be called
    // when trying to stack 2 fully stacked items of the same type.
    // Certainly a hacky work around, but it works.
    private static final NamespacedKey key = new NamespacedKey(plugin, "simplestack");

    private static final String permission = "simplestack.use";

    /*
     * InventoryClickEvent handler,
     * This does a majority of the work for this plugin.
     */
    @EventHandler
    public void stackEvent(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        if(!player.hasPermission(permission)) return;
        ItemStack itemPickUp = event.getCurrentItem();
        ItemStack itemPutDown = event.getCursor();
        ClickType clickType = event.getClick();

        if(itemPickUp == null ||
        itemPickUp.getData().getItemType().getMaxStackSize() == 64 ||
        itemPickUp.getType().equals(Material.AIR))
            return;

        Config config = Simplestack.getCustomConfig();

        if(config.LIST_MODE.equals(ListMode.BLACKLIST))
        {
            if(config.LIST.contains(itemPickUp.getType())) return;
        }
        else
        {
            if(!config.LIST.contains(itemPickUp.getType())) return;
        }

        StackUtils.makeUnique(itemPickUp, key);

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
        StackUtils.moveItemToInventory(event, event.getItem(), player, item);
    }

    /*
     * BlockBreakEvent
     * This is needed for when a shulker box breaks, because by default Minecraft
     * unstacks items inside of a shulker box automatically
     */
    @EventHandler
    public void breakBlockEvent(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        if(!player.hasPermission(permission)) return;
        Block block = event.getBlock();
        if(!block.getType().toString().endsWith("SHULKER_BOX")) return;

        Location location = block.getLocation();
        World world = location.getWorld();
        ShulkerBox shulkerBox = (ShulkerBox)block.getState();
        ItemStack item = new ItemStack(block.getType());
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();

        meta.setDisplayName(shulkerBox.getCustomName());
        for(ItemStack curItem : shulkerBox.getInventory().getStorageContents())
        {
            if(curItem == null) continue;
            meta.setBlockState(shulkerBox);
            break;
        }
        item.setItemMeta(meta);
        StackUtils.makeUnique(item, key);

        world.dropItemNaturally(location, item);
        block.setType(Material.AIR);
        event.setCancelled(true);
    }
}
