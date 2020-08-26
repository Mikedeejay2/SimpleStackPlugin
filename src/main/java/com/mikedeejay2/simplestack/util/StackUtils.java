package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.config.ListMode;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class StackUtils
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

    // Max stack size. Changing this produces some really weird results because
    // Minecraft really doesn't know how to handle anything higher than 64.
    public static final int MAX_AMOUNT_IN_STACK = 64;

    private static final NamespacedKey key = new NamespacedKey(plugin, "simplestack");

    /*
     * This helper method takes an item that is on the ground and moves
     * it into a player's inventory while also attempting to stack the
     * item with other items in the player's inventory.
     */
    public static void moveItemToInventory(Cancellable event, Item groundItem, Player player, ItemStack item)
    {
        if(item.getType().getMaxStackSize() == 64) return;
        PlayerInventory inv = player.getInventory();
        for(int i = 0; i < inv.getSize(); i++)
        {
            if(!moveItemInternal(item, inv, i)) continue;
            groundItem.remove();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.1f, 1);
            event.setCancelled(true);
            break;
        }
        if(item.getAmount() == 0) return;
        for(int i = 0; i < inv.getSize(); i++)
        {
            if(inv.getItem(i) != null) continue;
            inv.setItem(i, item);
            groundItem.remove();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.1f, 1);
            event.setCancelled(true);
            break;
        }
    }

    public static void moveItemToInventory(ItemStack item, Inventory fromInv, Inventory toInv, int amountBeingMoved)
    {
        if(item.getType().getMaxStackSize() == 64) return;
        int amountLeft = amountBeingMoved;
        item = item.clone();
        item.setAmount(amountBeingMoved);
        ItemStack origItem = item.clone();
        Inventory inv = toInv;
        for(int i = 0; i < inv.getSize(); i++)
        {
            if(!moveItemInternal(item, inv, i)) continue;
            amountLeft -= item.getAmount();
            break;
        }
        if(amountBeingMoved != 0)
        {
            for(int i = 0; i < inv.getSize(); i++)
            {
                if(inv.getItem(i) != null) continue;
                inv.setItem(i, item);
                amountLeft = 0;
                break;
            }
        }
        removeItemFromInventory(origItem, fromInv, amountBeingMoved - amountLeft);
    }

    public static void removeItemFromInventory(ItemStack item, Inventory inv, int amount)
    {
        for(int i = 0; i < inv.getSize(); i++)
        {
            ItemStack curItem = inv.getItem(i);
            if(curItem == null) continue;
            if(!equalsEachOther(curItem, item)) continue;
            if(amount > curItem.getAmount())
            {
                amount -= curItem.getAmount();
                curItem.setAmount(0);
            }
            else
            {
                curItem.setAmount(curItem.getAmount() - amount);
                amount = 0;
            }
            inv.setItem(i, curItem);
            if(amount == 0) break;
        }
    }

    public static void useAnvilCheck(Player player, Inventory topInv, int slot, Inventory clickedInventory, boolean rightClick)
    {
        Sound sound = Sound.BLOCK_ANVIL_USE;
        if(clickedInventory instanceof AnvilInventory && slot == 2)
        {
            triggerAnvilSmithingUse(player, topInv, rightClick, sound);
        }
    }

    public static void useSmithingCheck(Player player, Inventory topInv, int slot, Inventory clickedInventory, boolean rightClick)
    {
        if(Simplestack.getMCVersion() < 1.16) return;
        Sound sound = Sound.BLOCK_SMITHING_TABLE_USE;
        if(clickedInventory instanceof SmithingInventory && slot == 2)
        {
            triggerAnvilSmithingUse(player, topInv, rightClick, sound);
        }
    }

    public static void triggerAnvilSmithingUse(Player player, Inventory topInv, boolean rightClick, Sound sound)
    {
        ItemStack item1 = topInv.getItem(0);
        ItemStack item2 = topInv.getItem(1);
        ItemStack result = topInv.getItem(2);
        double divider = rightClick ? 2 : 1;
        if(result != null)
        {
            if(item2 != null)
            {
                if(item2.getAmount() > item1.getAmount())
                {
                    result.setAmount(item1.getAmount());
                }
                else
                {
                    result.setAmount(item2.getAmount());
                }
                item2.setAmount(item2.getAmount() - (int) Math.ceil(result.getAmount() / divider));
            }
            if(item1 != null) item1.setAmount(item1.getAmount() - (int) Math.ceil(result.getAmount() / divider));
            player.getWorld().playSound(player.getLocation(), sound, 1, 1);
        }
    }

    public static void updateAnvilManual(Inventory topInv)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(topInv instanceof AnvilInventory)
                {
                    triggerAnvilSmithingUpdate(topInv);
                }
                if(Simplestack.getMCVersion() < 1.16) return;
                if(topInv instanceof SmithingInventory)
                {
                    triggerAnvilSmithingUpdate(topInv);
                }
            }
        }.runTask(plugin);
    }

    private static void triggerAnvilSmithingUpdate(Inventory topInv)
    {
        ItemStack item1 = topInv.getItem(0);
        ItemStack item2 = topInv.getItem(1);
        topInv.setItem(0, null);
        topInv.setItem(1, null);
        topInv.setItem(0, item1);
        topInv.setItem(1, item2);
    }

    /*
     * This helper method moves an item into a player's inventory with a set starting slot
     * and ending slot to search through. This method can also be called in reverse if needed.
     */
    public static boolean moveItem(ItemStack itemPickUp, Inventory clickedInventory, int slot, Inventory inv, int startingSlot, int endingSlot, boolean reverse)
    {
        if(!reverse)
        {
            if(addItemToExistingStack(itemPickUp, inv, startingSlot, endingSlot, false)) return true;
            return addItemIgnoreStacks(itemPickUp, clickedInventory, slot, inv, startingSlot, endingSlot, false);
        }
        else
        {
            if(addItemToExistingStack(itemPickUp, inv, startingSlot, endingSlot, true)) return true;
            return addItemIgnoreStacks(itemPickUp, clickedInventory, slot, inv, startingSlot, endingSlot, true);
        }
    }

    private static boolean addItemIgnoreStacks(ItemStack itemPickUp, Inventory clickedInventory, int slot, Inventory inv, int startingSlot, int endingSlot, boolean reverse)
    {
        if(!reverse)
        {
            for(int i = startingSlot; i < endingSlot; i++)
            {
                if(inv.getItem(i) != null) continue;
                inv.setItem(i, itemPickUp);
                clickedInventory.setItem(slot, null);
                return true;
            }
        }
        else
        {
            for(int i = endingSlot-1; i >= startingSlot; i--)
            {
                if(inv.getItem(i) != null) continue;
                inv.setItem(i, itemPickUp);
                clickedInventory.setItem(slot, null);
                return true;
            }
        }
        return false;
    }

    private static boolean addItemToExistingStack(ItemStack itemPickUp, Inventory inv, int startingSlot, int endingSlot, boolean reverse)
    {
        if(!reverse)
        {
            for(int i = startingSlot; i < endingSlot; i++)
            {
                if(moveItemInternal(itemPickUp, inv, i)) break;
            }
        }
        else
        {
            for(int i = startingSlot; i < endingSlot; i++)
            {
                if(moveItemInternal(itemPickUp, inv, i)) break;
            }
        }
        return itemPickUp.getAmount() == 0;
    }

    public static void moveItemPlayerOrder(ItemStack itemPickUp, Inventory clickedInventory, int slot, Inventory inv)
    {
        if(addItemToExistingStack(itemPickUp, inv, 0, 9, false)) return;
        if(addItemToExistingStack(itemPickUp, inv, 9, 36, false)) return;
        if(!moveItem(itemPickUp, clickedInventory, slot, inv, 9, 36, false))
        {
            moveItem(itemPickUp, clickedInventory, slot, inv, 0, 9, false);
        }
    }

    /*
     * A helper method that attempts to move an item into a slot if all conditions
     * are correct (same type of item, not already stacked to 64, etc).
     */
    public static boolean moveItemInternal(ItemStack itemPickUp, Inventory inv, int i)
    {
        ItemStack itemStack = inv.getItem(i);
        if(itemStack == null || !equalsEachOther(itemPickUp, itemStack)) return false;
        int newAmount = itemStack.getAmount() + itemPickUp.getAmount();
        int extraAmount = 0;
        if(newAmount > MAX_AMOUNT_IN_STACK)
        {
            extraAmount = (newAmount - MAX_AMOUNT_IN_STACK);
            newAmount = MAX_AMOUNT_IN_STACK;
        }
        itemStack.setAmount(newAmount);
        itemPickUp.setAmount(extraAmount);
        return itemPickUp.getAmount() == 0;
    }

    /*
     * This makes an item unique and fools the Minecraft client to sending a move item
     * packet even with "fully stacked" items. This has to be worked around because
     * without InventoryClickEvent on fully stacked items there would be no way
     * for this code to know if the player was trying to combine fully stacked
     * items.
     */
    public static void makeUnique(ItemStack itemPickUp, NamespacedKey key)
    {
        ItemMeta itemMeta = itemPickUp.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        if(!data.has(key, PersistentDataType.BYTE))
        {
            data.set(key, PersistentDataType.BYTE, (byte) 1);
            itemPickUp.setItemMeta(itemMeta);
        }
    }

    /*
     * Simple helper method that takes 2 item metas
     * and checks to see if they equal each other.
     * Maybe the most important method in this code.
     */
    public static boolean equalsEachOther(ItemStack stack1, ItemStack stack2)
    {
        ItemMeta meta1 = stack1.getItemMeta();
        ItemMeta meta2 = stack2.getItemMeta();
        if(meta1 == null || meta2 == null) return false;
        if(!meta1.equals(meta2)) return false;
        if(!stack1.getType().equals(stack2.getType())) return false;
        return true;
    }

    /*
     * If the player doesn't have the permission "simplestack.use" (enabled by default)
     * then this will tell the event it's being called in to return and not run stacking code.
     */
    public static boolean cancelStackCheck(Material material)
    {
        Config config = Simplestack.getCustomConfig();
        if(material == null ||
           material.getMaxStackSize() == 64 ||
           material.equals(Material.AIR))
            return true;
        if(Simplestack.getCustomConfig().LIST_MODE.equals(ListMode.BLACKLIST))
        {
            return config.LIST.contains(material);
        }
        else
        {
            return !config.LIST.contains(material);
        }
    }

    public static boolean cancelPlayerCheck(Player player)
    {
        return !player.hasPermission(Simplestack.getPermission());
    }

    /*
     * By default shulker boxes unstack items when broken, this stops that.
     */
    public static void preserveShulkerBox(BlockBreakEvent event, Block block)
    {
        Location location = block.getLocation();
        World world = location.getWorld();
        ShulkerBox shulkerBox = (ShulkerBox) block.getState();
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
        StackUtils.makeUnique(item, plugin.getKey());

        world.dropItemNaturally(location, item);
        block.setType(Material.AIR);
        event.setCancelled(true);
    }


    public static void moveAllItemsToPlayerInv(Inventory inv, Player player, Inventory playerInv)
    {
        int startingSlot = 0;
        if(inv instanceof CraftingInventory) startingSlot = 1;
        for(int i = startingSlot; i < inv.getSize(); i++)
        {
            ItemStack stack = inv.getItem(i);
            if(stack == null) continue;

            boolean cancel = StackUtils.cancelStackCheck(stack.getType());
            if(cancel) continue;

            StackUtils.moveItem(stack, inv, i, playerInv, 0, 36, false);
            player.updateInventory();
        }
    }
}
