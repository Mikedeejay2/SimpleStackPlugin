package com.mikedeejay2.simplestack.handlers.executors;

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
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;

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
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;

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
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;

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
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;

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
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;

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
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;

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
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;

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
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;

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
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;

        Location dropLoc = player.getEyeLocation();
        Vector   lookVec = dropLoc.getDirection().multiply(1.0 / 3.0);
        World    world   = dropLoc.getWorld();
        Item     item    = world.dropItem(dropLoc, cursor);
        item.setVelocity(lookVec);
    }

    @Override
    public void execDropOneCursor(InventoryAction action, InventoryClickEvent event)
    {
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;

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
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;
    }

    @Override
    public void execDropOneSlot(InventoryAction action, InventoryClickEvent event)
    {
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;
    }

    @Override
    public void execMoveToOtherInventory(InventoryAction action, InventoryClickEvent event)
    {
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;
    }

    @Override
    public void execHotbarMoveAndReadd(InventoryAction action, InventoryClickEvent event)
    {
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;
    }

    @Override
    public void execHotbarSwap(InventoryAction action, InventoryClickEvent event)
    {
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;
    }

    @Override
    public void execCloneStack(InventoryAction action, InventoryClickEvent event)
    {
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;
    }

    @Override
    public void execCollectToCursor(InventoryAction action, InventoryClickEvent event)
    {
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;
    }

    @Override
    public void execUnknown(InventoryAction action, InventoryClickEvent event) {}
}
