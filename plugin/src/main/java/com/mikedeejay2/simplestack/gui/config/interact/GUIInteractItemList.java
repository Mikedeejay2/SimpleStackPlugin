package com.mikedeejay2.simplestack.gui.config.interact;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractType;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GUIInteractItemList extends GUIInteractExecutorList {
    public GUIInteractItemList(GUIInteractType type, int limit, boolean consume) {
        super(type, limit, consume);
    }

    @Override
    public void executePickupAll(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executePickupAll(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executePickupSome(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executePickupSome(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executePickupHalf(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executePickupHalf(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executePickupOne(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executePickupOne(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executePlaceAll(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executePlaceAll(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executePlaceSome(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executePlaceSome(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executePlaceOne(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executePlaceOne(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executeSwapWithCursor(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executeSwapWithCursor(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executeDropAllSlot(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executeDropAllSlot(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executeDropOneSlot(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executeDropOneSlot(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executeMoveToOtherInventory(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executeMoveToOtherInventory(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executeHotbarMoveAndReadd(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executeHotbarMoveAndReadd(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executeHotbarSwap(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executeHotbarSwap(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executeCloneStack(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executeCloneStack(player, inventory, slot, event, gui, layer);
    }

    @Override
    public void executeCollectToCursor(Player player, Inventory inventory, int slot, InventoryClickEvent event, GUIContainer gui, GUILayer layer) {
        super.executeCollectToCursor(player, inventory, slot, event, gui, layer);
    }
}
