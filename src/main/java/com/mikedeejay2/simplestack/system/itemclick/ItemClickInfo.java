package com.mikedeejay2.simplestack.system.itemclick;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public final class ItemClickInfo
{
    public final Simplestack plugin;
    public final Player player;
    public final ClickType clickType;
    public final ItemStack cursor;
    public final ItemStack selected;
    public final int slot;
    public final int rawSlot;
    public final int hotbar;
    public final boolean cursorNull;
    public final boolean selectedNull;
    public final int cursorMax;
    public final int selectedMax;
    public final int cursorAmt;
    public final int selectedAmt;
    public final boolean validSlot;
    public final InventoryView invView;
    public final Inventory bottomInv;
    public final Inventory topInv;
    public final Inventory clickedInv;
    public final boolean clickedBottom;
    public final boolean clickedTop;
    public final InventoryType.SlotType slotType;
    public final boolean clickedBorder;
    public final boolean clickedOutside;

    private InventoryAction action;

    public ItemClickInfo(InventoryClickEvent event, Simplestack plugin)
    {
        this.plugin         = plugin;
        this.player         = (Player) event.getWhoClicked();
        this.clickType      = event.getClick();
        ItemStack tempCursor = event.getCursor();
        if(tempCursor != null && tempCursor.getType() == Material.AIR) tempCursor = null;
        this.cursor         = tempCursor;
        ItemStack tempSelected = event.getCurrentItem();
        if(tempSelected != null && tempSelected.getType() == Material.AIR) tempSelected = null;
        this.selected       = tempSelected;
        this.slot           = event.getSlot();
        this.rawSlot        = event.getRawSlot();
        this.hotbar         = event.getHotbarButton();
        this.cursorNull     = cursor == null;
        this.selectedNull   = selected == null;
        this.cursorMax      = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        this.selectedMax    = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        this.cursorAmt      = cursorNull ? 0 : cursor.getAmount();
        this.selectedAmt    = selectedNull ? 0 : selected.getAmount();
        this.validSlot      = slot >= 0;
        this.invView        = event.getView();
        this.bottomInv      = invView.getBottomInventory();
        this.topInv         = invView.getTopInventory();
        this.clickedInv     = event.getClickedInventory();
        this.clickedBottom  = clickedInv == bottomInv;
        this.clickedTop     = clickedInv == topInv;
        this.slotType       = event.getSlotType();
        this.clickedBorder  = slot == -1;
        this.clickedOutside = slot == -999;
        this.action = InventoryAction.NOTHING;
    }

    public ItemClickInfo(ItemClickInfo other)
    {
        this.plugin         = other.plugin;
        this.player         = other.player;
        this.clickType      = other.clickType;
        this.cursor         = other.cursor;
        this.selected       = other.selected;
        this.slot           = other.slot;
        this.rawSlot        = other.rawSlot;
        this.hotbar         = other.hotbar;
        this.cursorNull     = other.cursorNull;
        this.selectedNull   = other.selectedNull;
        this.cursorMax      = other.cursorMax;
        this.selectedMax    = other.selectedMax;
        this.cursorAmt      = other.cursorAmt;
        this.selectedAmt    = other.selectedAmt;
        this.validSlot      = other.validSlot;
        this.invView        = other.invView;
        this.bottomInv      = other.bottomInv;
        this.topInv         = other.topInv;
        this.clickedInv     = other.clickedInv;
        this.clickedBottom  = other.clickedBottom;
        this.clickedTop     = other.clickedTop;
        this.slotType       = other.slotType;
        this.clickedBorder  = other.clickedBorder;
        this.clickedOutside = other.clickedOutside;
    }

    public InventoryAction getAction()
    {
        return action;
    }

    public void setAction(InventoryAction action)
    {
        this.action = action;
    }
}
