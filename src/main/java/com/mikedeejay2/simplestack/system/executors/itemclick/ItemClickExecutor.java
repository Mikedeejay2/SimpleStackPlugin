package com.mikedeejay2.simplestack.system.executors.itemclick;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemClickExecutor implements IItemClickExecutor
{
    protected final Simplestack plugin;

    public ItemClickExecutor(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void execNothing(InventoryAction action, InventoryClickEvent event) {}

    @Override
    public void execPickupAll(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        int newAmount = cursorAmt + selectedAmt;
        int extraAmount = 0;
        if(newAmount > selectedMax)
        {
            extraAmount = newAmount - selectedMax;
            newAmount = selectedMax;
        }
        if(cursorNull) cursor = selected.clone();
        cursor.setAmount(newAmount);
        selected.setAmount(extraAmount);
        player.setItemOnCursor(cursor);
    }

    @Override
    public void execPickupSome(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        int newAmount = cursorAmt + selectedAmt;
        int extraAmount = 0;
        if(newAmount > selectedMax)
        {
            extraAmount = newAmount - selectedMax;
            newAmount = selectedMax;
        }
        if(cursorNull) cursor = selected.clone();
        cursor.setAmount(newAmount);
        selected.setAmount(extraAmount);
        player.setItemOnCursor(cursor);
    }

    @Override
    public void execPickupHalf(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        int halfSelected = (int) Math.floor(selectedAmt / 2.0);
        int halfCursor = (int) Math.ceil(selectedAmt / 2.0);
        if(halfCursor > selectedMax)
        {
            halfSelected += halfCursor - selectedMax;
            halfCursor = selectedMax;
        }
        if(cursorNull) cursor = selected.clone();
        cursor.setAmount(halfCursor);
        selected.setAmount(halfSelected);
        player.setItemOnCursor(cursor);
    }

    @Override
    public void execPickupOne(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        int newAmount = cursorAmt + 1;
        int extraAmount = selectedAmt - 1;
        if(newAmount > selectedMax || extraAmount < 0)
        {
            return;
        }
        if(cursorNull) cursor = selected.clone();
        cursor.setAmount(newAmount);
        selected.setAmount(extraAmount);
        player.setItemOnCursor(cursor);
    }

    @Override
    public void execPlaceAll(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        int newAmount = selectedAmt + cursorAmt;
        int extraAmount = 0;
        if(newAmount > cursorMax)
        {
            extraAmount = newAmount - cursorMax;
            newAmount = cursorMax;
        }
        if(selectedNull) selected = cursor.clone();
        selected.setAmount(newAmount);
        cursor.setAmount(extraAmount);
        clickedInv.setItem(slot, selected);
        player.setItemOnCursor(cursor);
    }

    @Override
    public void execPlaceSome(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        int newAmount = selectedAmt + cursorAmt;
        int extraAmount = 0;
        if(newAmount > cursorMax)
        {
            extraAmount = newAmount - cursorMax;
            newAmount = cursorMax;
        }
        if(selectedNull) selected = cursor.clone();
        selected.setAmount(newAmount);
        cursor.setAmount(extraAmount);
        clickedInv.setItem(slot, selected);
        player.setItemOnCursor(cursor);
    }

    @Override
    public void execPlaceOne(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        int newAmount = selectedAmt + 1;
        int extraAmount = cursorAmt - 1;
        if(newAmount > cursorMax || extraAmount < 0)
        {
            return;
        }
        if(selectedNull) selected = cursor.clone();
        selected.setAmount(newAmount);
        cursor.setAmount(extraAmount);
        clickedInv.setItem(slot, selected);
        player.setItemOnCursor(cursor);
    }

    @Override
    public void execSwapWithCursor(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        if(selectedAmt > selectedMax || cursorAmt > cursorMax)
        {
            return;
        }
        clickedInv.setItem(slot, cursor);
        player.setItemOnCursor(selected);
    }

    @Override
    public void execDropAllCursor(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        Location dropLoc = player.getEyeLocation();
        Vector   lookVec = dropLoc.getDirection().multiply(1.0 / 3.0);
        World    world   = dropLoc.getWorld();
        Item     item    = world.dropItem(dropLoc, cursor);
        player.setItemOnCursor(null);
        item.setVelocity(lookVec);
    }

    @Override
    public void execDropOneCursor(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        int dropAmt = 1;
        int extraAmt = cursorAmt - 1;
        if(extraAmt < 0) return;
        ItemStack dropItem = cursor.clone();
        dropItem.setAmount(dropAmt);
        cursor.setAmount(extraAmt);
        player.setItemOnCursor(cursor);
        Location dropLoc = player.getEyeLocation();
        Vector   lookVec = dropLoc.getDirection().multiply(1.0 / 3.0);
        World    world   = dropLoc.getWorld();
        Item     item    = world.dropItem(dropLoc, dropItem);
        item.setVelocity(lookVec);
    }

    @Override
    public void execDropAllSlot(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        Location dropLoc = player.getEyeLocation();
        Vector   lookVec = dropLoc.getDirection().multiply(1.0 / 3.0);
        World    world   = dropLoc.getWorld();
        Item     item    = world.dropItem(dropLoc, selected);
        item.setVelocity(lookVec);
        clickedInv.setItem(slot, null);
    }

    @Override
    public void execDropOneSlot(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        int dropAmt = 1;
        int extraAmt = selectedAmt - 1;
        if(extraAmt < 0) return;
        ItemStack dropItem = selected.clone();
        dropItem.setAmount(dropAmt);
        selected.setAmount(extraAmt);
        Location dropLoc = player.getEyeLocation();
        Vector   lookVec = dropLoc.getDirection().multiply(1.0 / 3.0);
        World    world   = dropLoc.getWorld();
        Item     item    = world.dropItem(dropLoc, dropItem);
        item.setVelocity(lookVec);
    }

    @Override
    public void execMoveToOtherInventory(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        Inventory otherInv = clickedBottom ? topInv : bottomInv;
        ItemStack[] topItems = otherInv.getStorageContents();
        Material selectedMat = selected.getType();
        for(int i = 0; i < topItems.length; ++i)
        {
            ItemStack item = topItems[i];
            if(item == null) continue;
            if(item.getType() != selectedMat) continue;
            int itemAmt = item.getAmount();
            if(itemAmt == selectedMax) continue;
            int newAmt = itemAmt + selectedAmt;
            if(newAmt > selectedMax)
            {
                selectedAmt = newAmt - selectedMax;
                newAmt = selectedMax;
            }
            else
            {
                selectedAmt = 0;
            }
            item.setAmount(newAmt);
            if(selectedAmt <= 0)
            {
                selected.setAmount(0);
                return;
            }
        }
        for(int i = 0; i < topItems.length; ++i)
        {
            ItemStack item = topItems[i];
            if(item != null) continue;
            item = selected.clone();
            int newAmt = selectedAmt;
            if(newAmt > selectedMax)
            {
                selectedAmt = newAmt - selectedMax;
                newAmt = selectedMax;
            }
            else
            {
                selectedAmt -= newAmt;
            }
            item.setAmount(newAmt);
            otherInv.setItem(i, item);
            player.sendMessage("selectedAmt: " + selectedAmt);
            if(selectedAmt <= 0)
            {
                selected.setAmount(0);
                return;
            }
        }
        selected.setAmount(selectedAmt);
    }

    @Override
    public void execHotbarMoveAndReadd(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        ItemStack hotbarItem = bottomInv.getItem(hotbar);
        int hotbarAmt = hotbarItem.getAmount();
        int hotbarMax = StackUtils.getMaxAmount(plugin, hotbarItem);
        if(hotbarAmt > hotbarMax || selectedAmt > selectedMax) return;
        clickedInv.setItem(slot, hotbarItem);
        bottomInv.setItem(hotbar, selected);
    }

    @Override
    public void execHotbarSwap(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        ItemStack hotbarItem = bottomInv.getItem(hotbar);
        if(hotbarItem != null && hotbarItem.getType() == Material.AIR) hotbarItem = null;
        boolean hotbarNull = hotbarItem == null;
        int hotbarAmt = hotbarNull ? 0 : hotbarItem.getAmount();
        int hotbarMax = hotbarNull ? 0 : StackUtils.getMaxAmount(plugin, hotbarItem);
        if(hotbarAmt > hotbarMax || selectedAmt > selectedMax) return;
        clickedInv.setItem(slot, hotbarItem);
        bottomInv.setItem(hotbar, selected);
    }

    @Override
    public void execCloneStack(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        cursor = selected.clone();
        cursor.setAmount(selectedMax);
        player.setItemOnCursor(cursor);
    }

    @Override
    public void execCollectToCursor(InventoryAction action, InventoryClickEvent event)
    {
        Player                 player         = (Player) event.getWhoClicked();
        ClickType              clickType      = event.getClick();
        ItemStack              cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack              selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int                    slot           = event.getSlot();
        int                    rawSlot        = event.getRawSlot();
        int                    hotbar         = event.getHotbarButton();
        boolean                cursorNull     = cursor == null;
        boolean                selectedNull   = selected == null;
        int                    cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int                    selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int                    cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        int                    selectedAmt    = selectedNull ? 0 : selected.getAmount();
        boolean                validSlot      = slot >= 0;
        InventoryView          invView        = event.getView();
        Inventory              bottomInv      = invView.getBottomInventory();
        Inventory              topInv         = invView.getTopInventory();
        Inventory              clickedInv     = event.getClickedInventory();
        boolean                clickedBottom  = clickedInv == bottomInv;
        boolean                clickedTop     = clickedInv == topInv;
        InventoryType.SlotType slotType       = invView.getSlotType(rawSlot);
        boolean                clickedBorder  = slot == -1;
        boolean                clickedOutside = slot == -999;

        ItemStack[] topItems = topInv.getStorageContents();
        ItemStack[] bottomItems = bottomInv.getStorageContents();
        Material cursorMat = cursor.getType();
        for(int i = 0; i < topItems.length; ++i)
        {
            ItemStack item = topItems[i];
            if(item == null) continue;
            if(item.getType() != cursorMat) continue;
            int newAmount = item.getAmount() + cursorAmt;
            int extraAmount = 0;
            if(newAmount > cursorMax)
            {
                extraAmount =  newAmount - cursorMax;
                newAmount = cursorMax;
            }
            item.setAmount(extraAmount);
            topInv.setItem(i, item);
            cursorAmt = newAmount;
            if(cursorAmt == cursorMax)
            {
                cursor.setAmount(cursorAmt);
                return;
            }
        }
        for(int i = bottomItems.length - 1; i >= 0; --i)
        {
            ItemStack item = bottomItems[i];
            if(item == null) continue;
            if(item.getType() != cursorMat) continue;
            int newAmount = item.getAmount() + cursorAmt;
            int extraAmount = 0;
            if(newAmount > cursorMax)
            {
                extraAmount =  newAmount - cursorMax;
                newAmount = cursorMax;
            }
            item.setAmount(extraAmount);
            bottomInv.setItem(i, item);
            cursorAmt = newAmount;
            if(cursorAmt == cursorMax)
            {
                cursor.setAmount(cursorAmt);
                return;
            }
        }
    }

    @Override
    public void execUnknown(InventoryAction action, InventoryClickEvent event) {}
}
