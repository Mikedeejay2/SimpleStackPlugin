package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.MovementType;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Heads;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class GUIAboutModule extends GUIModule
{
    @Override
    public void onOpenHead(Player player, GUIContainer gui)
    {
        GUILayer base = gui.getLayer(0);
        GUILayer clickLayer = gui.getLayer("click");
        GUILayer flyLayer = gui.getLayer("fly");
        GUILayer textLayer = gui.getLayer("text");
        int rows = gui.getRows();
        int cols = gui.getCols();
        genBackground(base);
        genFly(flyLayer);
    }

    private void genFly(GUILayer flyLayer)
    {
        for(int col = 1; col <= 9; ++col)
        {
            GUIItem item = getFlyItem(col);
            flyLayer.setItem(1, col, item);
        }
    }

    private void genBackground(GUILayer base)
    {
        AnimatedGUIItem item1 = getBackgroundItem(2);
        AnimatedGUIItem item2 = getBackgroundItem(4);
        AnimatedGUIItem item3 = getBackgroundItem(6);
        AnimatedGUIItem item4 = getBackgroundItem(8);
        for(int col = 4; col <= 6; ++col)
        {
            base.setItem(3, col, item1);
            base.setItem(4, col, item1);
        }
        for(int col = 3; col <= 7; ++col)
        {
            base.setItem(2, col, item2);
            base.setItem(5, col, item2);
        }
        for(int row = 3; row <= 4; ++row)
        {
            base.setItem(row, 3, item2);
            base.setItem(row, 7, item2);
        }
        for(int col = 2; col <= 8; ++col)
        {
            base.setItem(1, col, item3);
            base.setItem(6, col, item3);
        }
        for(int row = 2; row <= 5; ++row)
        {
            base.setItem(row, 2, item3);
            base.setItem(row, 8, item3);
        }
        for(int row = 1; row <= 6; ++row)
        {
            base.setItem(row, 1, item4);
            base.setItem(row, 9, item4);
        }
    }

    private AnimatedGUIItem getBackgroundItem(int delay)
    {
        AnimatedGUIItem item = new AnimatedGUIItem(null, false, delay);
        item.addFrame(ItemCreator.createItem(Material.PINK_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 1);
        item.addFrame(ItemCreator.createItem(Material.MAGENTA_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 1);
        item.addFrame(ItemCreator.createItem(Material.PURPLE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 1);
        return item;
    }

    private AnimatedGUIItem getFlyItem(int column)
    {
        Random random = new Random();
        AnimatedGUIItem item = new AnimatedGUIItem(null, true, random.nextInt(50) + 10);
        ItemStack stack = ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME);
        item.addFrame(stack, 1, column, MovementType.OVERRIDE_ITEM, false, 1);
        for(int row = 2; row <= 6; ++row)
        {
            item.addFrame(row, column, MovementType.OVERRIDE_ITEM, false, 1);
        }
        item.addFrame(null, random.nextInt(50) + 25);
        return item;
    }
}
