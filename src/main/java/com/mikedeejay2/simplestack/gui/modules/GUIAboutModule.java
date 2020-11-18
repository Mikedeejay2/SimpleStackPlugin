package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.MovementType;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Heads;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemCreator;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.gui.GUICreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class GUIAboutModule extends GUIModule
{
    private final Simplestack plugin;

    public GUIAboutModule(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onClickedHead(InventoryClickEvent event, GUIContainer gui)
    {
        GUILayer clickLayer = gui.getLayer("click");
        int slot = event.getSlot();
        int rows = gui.getRows();
        int cols = gui.getCols();
        int row = gui.getRowFromSlot(slot);
        int col = gui.getColFromSlot(slot);
        int newRow = row;
        int newCol = col;

        AnimatedGUIItem item = null;

        item = new AnimatedGUIItem(null, false, 0);
        item.addFrame(ItemCreator.createItem(Material.WHITE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 1);
        item.addFrame(ItemCreator.createItem(Material.PINK_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 1);
        item.addFrame(ItemCreator.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 1);
        item.addFrame(ItemCreator.createItem(Material.MAGENTA_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 1);
        item.addFrame(null, 1);
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);

        item = new AnimatedGUIItem(null, false, 2);
        item.addFrame(ItemCreator.createItem(Material.PINK_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 1);
        item.addFrame(ItemCreator.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 1);
        item.addFrame(ItemCreator.createItem(Material.MAGENTA_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 1);
        item.addFrame(null, 1);
        newCol = col - 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row - 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col + 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row + 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col - 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);

        item = new AnimatedGUIItem(null, false, 4);
        item.addFrame(ItemCreator.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 1);
        item.addFrame(ItemCreator.createItem(Material.MAGENTA_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 1);
        item.addFrame(null, 1);
        newRow = row; newCol = col - 2;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row - 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col - 1; newRow = row - 2;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col + 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col + 2; newRow = row - 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row + 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row + 2; newCol = col + 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col - 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col - 2; newRow = row + 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);

        item = new AnimatedGUIItem(null, false, 6);
        item.addFrame(ItemCreator.createItem(Material.MAGENTA_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 1);
        item.addFrame(null, 1);
        newRow = row;
        newCol = col - 3;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row - 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row - 2;
        newCol = col - 2;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row - 3;
        newCol = col - 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col + 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col + 2;
        newRow = row - 2;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row - 1;
        newCol = col + 3;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row + 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row + 2;
        newCol = col + 2;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newRow = row + 3;
        newCol = col + 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col - 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col - 2;
        newRow = row + 2;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
        newCol = col - 3;
        newRow = row + 1;
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);
    }

    private boolean validCheck(int rows, int cols, int newRow, int newCol)
    {
        return newRow > 0 && newCol > 0 && newRow <= rows && newCol <= cols;
    }

    @Override
    public void onOpenHead(Player player, GUIContainer gui)
    {
        GUILayer base = gui.getLayer(0);
        GUILayer flyLayer = gui.getLayer("fly");
        GUILayer textLayer = gui.getLayer("text");
        GUILayer aboutLayer = gui.getLayer("about");
        genBackground(base);
        genFly(flyLayer);
        genText(textLayer);
        genAbout(textLayer, aboutLayer);
        genBackButton(player, textLayer);
    }

    private void genBackButton(Player player, GUILayer textLayer)
    {
        AnimatedGUIItem backItem = new AnimatedGUIItem(null, false, 60);
        backItem.addFrame(ItemCreator.createHeadItem(Base64Heads.ARROW_BACKWARD_WHITE, 1, "&f" + plugin.langManager().getTextLib(player, "gui.modules.navigator.backward")), 1);
        backItem.addEvent(new GUIOpenNewEvent(plugin, () -> GUICreator.createMainGUI(plugin)));
        textLayer.setItem(6, 5, backItem);
    }

    private void genAbout(GUILayer textLayer, GUILayer aboutLayer)
    {
        int start = 40;
        AnimatedGUIItem item1 = new AnimatedGUIItem(null, false, start);
        item1.addFrame(ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME), 0, 1 ,MovementType.OVERRIDE_ITEM, true, 2);
        item1.addFrame(0, 1 ,MovementType.OVERRIDE_ITEM, true, 2);
        item1.addFrame(0, 1 ,MovementType.OVERRIDE_ITEM, true, 2);
        item1.addFrame(0, 1 ,MovementType.OVERRIDE_ITEM, true, 2);
        AnimatedGUIItem item2 = new AnimatedGUIItem(null, false, start);
        item2.addFrame(ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME), 0, -1 ,MovementType.OVERRIDE_ITEM, true, 2);
        item2.addFrame(0, -1 ,MovementType.OVERRIDE_ITEM, true, 2);
        item2.addFrame(0, -1 ,MovementType.OVERRIDE_ITEM, true, 2);
        item2.addFrame(0, -1 ,MovementType.OVERRIDE_ITEM, true, 2);
        textLayer.setItem(5, 1, item1);
        textLayer.setItem(5, 9, item2);
        AnimatedGUIItem aboutItem = new AnimatedGUIItem(null, false, start + 10);
        aboutItem.addFrame(ItemCreator.createItem(Material.DRAGON_EGG, 1,
                "&b&lSimple Stack v" + plugin.getDescription().getVersion(),
                "&5Simple Stack is a plugin ",
                "&5that makes unstackable items stack.",
                "",
                "&9Credits:",
                "&5• Code by Mikedeejay2",
                "&5• Translations provided by ",
                "&5  contributors on OneSky and Github",
                "&5• Bug reports submitted by ",
                "&5  contributors on Github"), 1);
        aboutLayer.setItem(5, 5, aboutItem);
    }

    private void genText(GUILayer textLayer)
    {
        AnimatedGUIItem item1 = new AnimatedGUIItem(null, false, 10);
        item1.addFrame(ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item1.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item1.addFrame(ItemCreator.createHeadItem(Base64Heads.S_WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item1.addFrame(ItemCreator.createHeadItem(Base64Heads.S_RED, 1, GUIContainer.EMPTY_NAME), 1);
        item1.addFrame(ItemCreator.createHeadItem(Base64Heads.S_ORANGE, 1, GUIContainer.EMPTY_NAME), 1);
        item1.addFrame(ItemCreator.createHeadItem(Base64Heads.S_YELLOW, 1, GUIContainer.EMPTY_NAME), 1);

        AnimatedGUIItem item2 = new AnimatedGUIItem(null, false, 11);
        item2.addFrame(ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item2.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item2.addFrame(ItemCreator.createHeadItem(Base64Heads.I_WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item2.addFrame(ItemCreator.createHeadItem(Base64Heads.I_RED, 1, GUIContainer.EMPTY_NAME), 1);
        item2.addFrame(ItemCreator.createHeadItem(Base64Heads.I_ORANGE, 1, GUIContainer.EMPTY_NAME), 1);
        item2.addFrame(ItemCreator.createHeadItem(Base64Heads.I_YELLOW, 1, GUIContainer.EMPTY_NAME), 1);

        AnimatedGUIItem item3 = new AnimatedGUIItem(null, false, 12);
        item3.addFrame(ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item3.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item3.addFrame(ItemCreator.createHeadItem(Base64Heads.M_WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item3.addFrame(ItemCreator.createHeadItem(Base64Heads.M_RED, 1, GUIContainer.EMPTY_NAME), 1);
        item3.addFrame(ItemCreator.createHeadItem(Base64Heads.M_ORANGE, 1, GUIContainer.EMPTY_NAME), 1);
        item3.addFrame(ItemCreator.createHeadItem(Base64Heads.M_YELLOW, 1, GUIContainer.EMPTY_NAME), 1);

        AnimatedGUIItem item4 = new AnimatedGUIItem(null, false, 13);
        item4.addFrame(ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item4.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item4.addFrame(ItemCreator.createHeadItem(Base64Heads.P_WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item4.addFrame(ItemCreator.createHeadItem(Base64Heads.P_RED, 1, GUIContainer.EMPTY_NAME), 1);
        item4.addFrame(ItemCreator.createHeadItem(Base64Heads.P_ORANGE, 1, GUIContainer.EMPTY_NAME), 1);
        item4.addFrame(ItemCreator.createHeadItem(Base64Heads.P_YELLOW, 1, GUIContainer.EMPTY_NAME), 1);

        AnimatedGUIItem item5 = new AnimatedGUIItem(null, false, 14);
        item5.addFrame(ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item5.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item5.addFrame(ItemCreator.createHeadItem(Base64Heads.L_WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item5.addFrame(ItemCreator.createHeadItem(Base64Heads.L_RED, 1, GUIContainer.EMPTY_NAME), 1);
        item5.addFrame(ItemCreator.createHeadItem(Base64Heads.L_ORANGE, 1, GUIContainer.EMPTY_NAME), 1);
        item5.addFrame(ItemCreator.createHeadItem(Base64Heads.L_YELLOW, 1, GUIContainer.EMPTY_NAME), 1);

        AnimatedGUIItem item6 = new AnimatedGUIItem(null, false, 15);
        item6.addFrame(ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item6.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item6.addFrame(ItemCreator.createHeadItem(Base64Heads.E_WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item6.addFrame(ItemCreator.createHeadItem(Base64Heads.E_RED, 1, GUIContainer.EMPTY_NAME), 1);
        item6.addFrame(ItemCreator.createHeadItem(Base64Heads.E_ORANGE, 1, GUIContainer.EMPTY_NAME), 1);
        item6.addFrame(ItemCreator.createHeadItem(Base64Heads.E_YELLOW, 1, GUIContainer.EMPTY_NAME), 1);


        AnimatedGUIItem item7 = new AnimatedGUIItem(null, false, 20);
        item7.addFrame(ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item7.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item7.addFrame(ItemCreator.createHeadItem(Base64Heads.S_WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item7.addFrame(ItemCreator.createHeadItem(Base64Heads.S_BLACK, 1, GUIContainer.EMPTY_NAME), 1);
        item7.addFrame(ItemCreator.createHeadItem(Base64Heads.S_RED, 1, GUIContainer.EMPTY_NAME), 1);
        item7.addFrame(ItemCreator.createHeadItem(Base64Heads.S_ORANGE, 1, GUIContainer.EMPTY_NAME), 1);

        AnimatedGUIItem item8 = new AnimatedGUIItem(null, false, 19);
        item8.addFrame(ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item8.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item8.addFrame(ItemCreator.createHeadItem(Base64Heads.T_WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item8.addFrame(ItemCreator.createHeadItem(Base64Heads.T_BLACK, 1, GUIContainer.EMPTY_NAME), 1);
        item8.addFrame(ItemCreator.createHeadItem(Base64Heads.T_RED, 1, GUIContainer.EMPTY_NAME), 1);
        item8.addFrame(ItemCreator.createHeadItem(Base64Heads.T_ORANGE, 1, GUIContainer.EMPTY_NAME), 1);

        AnimatedGUIItem item9 = new AnimatedGUIItem(null, false, 18);
        item9.addFrame(ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item9.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item9.addFrame(ItemCreator.createHeadItem(Base64Heads.A_WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item9.addFrame(ItemCreator.createHeadItem(Base64Heads.A_BLACK, 1, GUIContainer.EMPTY_NAME), 1);
        item9.addFrame(ItemCreator.createHeadItem(Base64Heads.A_RED, 1, GUIContainer.EMPTY_NAME), 1);
        item9.addFrame(ItemCreator.createHeadItem(Base64Heads.A_ORANGE, 1, GUIContainer.EMPTY_NAME), 1);

        AnimatedGUIItem item10 = new AnimatedGUIItem(null, false, 17);
        item10.addFrame(ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item10.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item10.addFrame(ItemCreator.createHeadItem(Base64Heads.C_WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item10.addFrame(ItemCreator.createHeadItem(Base64Heads.C_BLACK, 1, GUIContainer.EMPTY_NAME), 1);
        item10.addFrame(ItemCreator.createHeadItem(Base64Heads.C_RED, 1, GUIContainer.EMPTY_NAME), 1);
        item10.addFrame(ItemCreator.createHeadItem(Base64Heads.C_ORANGE, 1, GUIContainer.EMPTY_NAME), 1);

        AnimatedGUIItem item11 = new AnimatedGUIItem(null, false, 16);
        item11.addFrame(ItemCreator.createHeadItem(Base64Heads.WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item11.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item11.addFrame(ItemCreator.createHeadItem(Base64Heads.K_WHITE, 1, GUIContainer.EMPTY_NAME), 5);
        item11.addFrame(ItemCreator.createHeadItem(Base64Heads.K_BLACK, 1, GUIContainer.EMPTY_NAME), 1);
        item11.addFrame(ItemCreator.createHeadItem(Base64Heads.K_RED, 1, GUIContainer.EMPTY_NAME), 1);
        item11.addFrame(ItemCreator.createHeadItem(Base64Heads.K_ORANGE, 1, GUIContainer.EMPTY_NAME), 1);

        textLayer.setItem(3, 2, item1);
        textLayer.setItem(3, 3, item2);
        textLayer.setItem(3, 4, item3);
        textLayer.setItem(3, 5, item4);
        textLayer.setItem(3, 6, item5);
        textLayer.setItem(3, 7, item6);

        textLayer.setItem(4, 4, item7);
        textLayer.setItem(4, 5, item8);
        textLayer.setItem(4, 6, item9);
        textLayer.setItem(4, 7, item10);
        textLayer.setItem(4, 8, item11);
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
        AnimatedGUIItem item = new AnimatedGUIItem(null, true, random.nextInt(50) + 50);
        String str = null;
        int randNum = random.nextInt(4);
        switch(randNum)
        {
            case 0:
                str = Base64Heads.BLUE;
                break;
            case 1:
                str = Base64Heads.PURPLE;
                break;
            case 2:
                str = Base64Heads.PINK;
                break;
            case 3:
                str = Base64Heads.MAGENTA;
                break;
            case 4:
                str = Base64Heads.LIGHT_BLUE;
                break;
        }
        ItemStack stack = ItemCreator.createHeadItem(str, 1, GUIContainer.EMPTY_NAME);
        item.addFrame(stack, 1, column, MovementType.OVERRIDE_ITEM, false, 1);
        for(int row = 2; row <= 6; ++row)
        {
            item.addFrame(row, column, MovementType.OVERRIDE_ITEM, false, 1);
        }
        item.addFrame(null, random.nextInt(50) + 25);
        return item;
    }
}
