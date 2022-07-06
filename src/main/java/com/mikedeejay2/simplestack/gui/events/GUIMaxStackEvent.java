package com.mikedeejay2.simplestack.gui.events;

import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIEventInfo;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.config.Config;
import org.bukkit.event.inventory.ClickType;

/**
 * A <tt>GUIEvent</tt> that modifies the max stack amount for all items in Minecraft.
 * Value is changed in the Simple Stack config.
 *
 * @author Mikedeejay2
 */
public class GUIMaxStackEvent implements GUIEvent {
    private final SimpleStack plugin;

    public GUIMaxStackEvent(SimpleStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(GUIEventInfo event) {
        ClickType type = event.getClick();
        if(type == ClickType.DOUBLE_CLICK) return;
        GUILayer layer = event.getGUI().getLayer(0);
        Config config = plugin.config();
        int slot = event.getSlot();
        int row = layer.getRowFromSlot(slot);
        int col = layer.getColFromSlot(slot);
        GUIItem item = layer.getItem(row, col);
        boolean leftClick = type.isLeftClick();
        boolean rightClick = type.isRightClick();
        boolean shiftClick = type.isShiftClick();

        if(shiftClick) {
            if(leftClick) {
                item.setAmount(1);
            } else if(rightClick) {
                item.setAmount(64);
            }
        } else if(leftClick) {
            item.setAmount(item.getAmount() > 1 ? item.getAmount() - 1 : 1);
        } else if(rightClick) {
            item.setAmount(item.getAmount() < 64 ? item.getAmount() + 1 : 64);
        }
        int amount = item.getAmount();
        config.setMaxAmount(amount);
    }
}
