package com.mikedeejay2.simplestack.system.itemclick;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.SimpleStackProcess;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public abstract class ItemClickContainer
{
    protected final Simplestack plugin;
    protected Player player;
    protected ClickType clickType;
    protected ItemStack cursor;
    protected ItemStack selected;
    protected int slot;
    protected int rawSlot;
    protected int hotbar;
    protected boolean cursorNull;
    protected boolean selectedNull;
    protected int cursorMax;
    protected int selectedMax;
    protected int cursorAmt;
    protected int selectedAmt;
    protected boolean validSlot;
    protected InventoryView invView;
    protected Inventory bottomInv;
    protected Inventory topInv;
    protected Inventory clickedInv;
    protected boolean clickedBottom;
    protected boolean clickedTop;
    protected InventoryType.SlotType slotType;
    protected boolean clickedBorder;
    protected boolean clickedOutside;

    public ItemClickContainer(InventoryClickEvent event, Simplestack plugin)
    {
        this.plugin = plugin;
        this.player         = (Player) event.getWhoClicked();
        this.clickType      = event.getClick();
        this.cursor         = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        this.selected       = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
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
        this.slotType       = invView.getSlotType(rawSlot);
        this.clickedBorder  = slot == -1;
        this.clickedOutside = slot == -999;
    }
}
