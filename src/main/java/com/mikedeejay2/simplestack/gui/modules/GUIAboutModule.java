package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.MovementType;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.gui.GUICreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * The <tt>GUIModule</tt> for the about screen in the GUI.
 *
 * @author Mikedeejay2
 */
public class GUIAboutModule implements GUIModule
{
    private final SimpleStack plugin;

    public GUIAboutModule(SimpleStack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Overridden <tt>onClickedHead</tt> that creates a click effect on screen.
     * Why? Because I was bored and wanted to show off what my GUI library can
     * do.
     *
     * @param event The original <tt>InventoryClickEvent</tt>
     * @param gui   The <tt>GUIContainer</tt> that was clicked
     */
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
        item.addFrame(ItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).setEmptyName().get(), 1);
        item.addFrame(ItemBuilder.of(Material.PINK_STAINED_GLASS_PANE).setEmptyName().get(), 1);
        item.addFrame(ItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setEmptyName().get(), 1);
        item.addFrame(ItemBuilder.of(Material.MAGENTA_STAINED_GLASS_PANE).setEmptyName().get(), 1);
        item.addFrame(null, 1);
        if(validCheck(rows, cols, newRow, newCol)) clickLayer.setItem(newRow, newCol, item);

        item = new AnimatedGUIItem(null, false, 2);
        item.addFrame(ItemBuilder.of(Material.PINK_STAINED_GLASS_PANE).setEmptyName().get(), 1);
        item.addFrame(ItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setEmptyName().get(), 1);
        item.addFrame(ItemBuilder.of(Material.MAGENTA_STAINED_GLASS_PANE).setEmptyName().get(), 1);
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
        item.addFrame(ItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setEmptyName().get(), 1);
        item.addFrame(ItemBuilder.of(Material.MAGENTA_STAINED_GLASS_PANE).setEmptyName().get(), 1);
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
        item.addFrame(ItemBuilder.of(Material.MAGENTA_STAINED_GLASS_PANE).setEmptyName().get(), 1);
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

    /**
     * Helper method for making sure that an item is actually in the GUI
     *
     * @param rows   The maximum rows of the GUI
     * @param cols   The maximum columns of the GUI
     * @param curRow The current row of the item
     * @param curCol The current column of the item
     * @return Whether the item placement is valid or not
     */
    private boolean validCheck(int rows, int cols, int curRow, int curCol)
    {
        return curRow > 0 && curCol > 0 && curRow <= rows && curCol <= cols;
    }

    /**
     * Overridden method <tt>onOpenHead</tt> that generates the about screen
     *
     * @param player The player the opened the GUI
     * @param gui The <tt>GUIContainer</tt> that the about screen is located in
     */
    @Override
    public void onOpenHead(Player player, GUIContainer gui)
    {
        GUILayer base = gui.getLayer(0);
        GUILayer flyLayer = gui.getLayer("fly");
        gui.addLayer(2, "click", false);
        GUILayer textLayer = gui.getLayer("text");
        GUILayer aboutLayer = gui.getLayer("about");
        genBackground(base);
        genFly(flyLayer);
        genText(textLayer);
        genAbout(textLayer, aboutLayer, player);
        genBackButton(player, textLayer);
    }

    /**
     * Generate the back button for the about screen
     *
     * @param player    The player that opened the GUI (For the player's locale)
     * @param textLayer The text <tt>GUILayer</tt> that will be used
     */
    private void genBackButton(Player player, GUILayer textLayer)
    {
        AnimatedGUIItem backItem = new AnimatedGUIItem(null, false, 60);
        backItem.addFrame(ItemBuilder.of(Base64Head.ARROW_BACKWARD_WHITE.get())
            .setName("&f" + plugin.getLibLangManager().getText(player, "gui.modules.navigator.backward"))
            .get(), 1);
        backItem.addEvent(new GUIOpenNewEvent(plugin, () -> GUICreator.createMainGUI(plugin, player)));
        textLayer.setItem(6, 5, backItem);
    }

    /**
     * Generate the about section with the about text and intro animation
     *
     * @param textLayer  The text <tt>GUILayer</tt> that will be used
     * @param aboutLayer The about <tt>GUILayer</tt> that will be used
     * @param player     The player that opened the GUI (For the player's locale)
     */
    private void genAbout(GUILayer textLayer, GUILayer aboutLayer, Player player)
    {
        int start = 40;
        AnimatedGUIItem item1 = new AnimatedGUIItem(null, false, start);
        item1.addFrame(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get(), 0, 1 ,MovementType.OVERRIDE_ITEM, true, 2);
        item1.addFrame(0, 1 ,MovementType.OVERRIDE_ITEM, true, 2);
        item1.addFrame(0, 1 ,MovementType.OVERRIDE_ITEM, true, 2);
        item1.addFrame(0, 1 ,MovementType.OVERRIDE_ITEM, true, 2);
        AnimatedGUIItem item2 = new AnimatedGUIItem(null, false, start);
        item2.addFrame(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get(), 0, -1 ,MovementType.OVERRIDE_ITEM, true, 2);
        item2.addFrame(0, -1 ,MovementType.OVERRIDE_ITEM, true, 2);
        item2.addFrame(0, -1 ,MovementType.OVERRIDE_ITEM, true, 2);
        item2.addFrame(0, -1 ,MovementType.OVERRIDE_ITEM, true, 2);
        textLayer.setItem(5, 1, item1);
        textLayer.setItem(5, 9, item2);
        AnimatedGUIItem aboutItem = new AnimatedGUIItem(null, false, start + 10);
        aboutItem.addFrame(ItemBuilder.of(Material.DRAGON_EGG).setName("&b&lSimple Stack v" + plugin.getDescription().getVersion())
            .setLore(
                "&5" + plugin.getLangManager().getText(player, "simplestack.gui.about.desc_l1"),
                "&5" + plugin.getLangManager().getText(player, "simplestack.gui.about.desc_l2"),
                "",
                "&9" + plugin.getLangManager().getText(player, "simplestack.gui.about.desc_l3"),
                "&5• " + plugin.getLangManager().getText(player, "simplestack.gui.about.desc_l4"),
                "&5• " + plugin.getLangManager().getText(player, "simplestack.gui.about.desc_l5"),
                "&5  " + plugin.getLangManager().getText(player, "simplestack.gui.about.desc_l6"),
                "&5• " + plugin.getLangManager().getText(player, "simplestack.gui.about.desc_l7"),
                "&5  " + plugin.getLangManager().getText(player, "simplestack.gui.about.desc_l8"))
            .get(), 1);
        aboutLayer.setItem(5, 5, aboutItem);
    }

    /**
     * Generate the Simple Stack title text and animation
     *
     * @param textLayer The text <tt>GUILayer</tt> that will be used
     */
    private void genText(GUILayer textLayer)
    {
        AnimatedGUIItem item1 = new AnimatedGUIItem(null, false, 10);
        item1.addFrame(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get(), 5);
        item1.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item1.addFrame(ItemBuilder.of(Base64Head.S_WHITE.get()).setEmptyName().get(), 5);
        item1.addFrame(ItemBuilder.of(Base64Head.S_RED.get()).setEmptyName().get(), 1);
        item1.addFrame(ItemBuilder.of(Base64Head.S_ORANGE.get()).setEmptyName().get(), 1);
        item1.addFrame(ItemBuilder.of(Base64Head.S_YELLOW.get()).setEmptyName().get(), 1);

        AnimatedGUIItem item2 = new AnimatedGUIItem(null, false, 11);
        item2.addFrame(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get(), 5);
        item2.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item2.addFrame(ItemBuilder.of(Base64Head.I_WHITE.get()).setEmptyName().get(), 5);
        item2.addFrame(ItemBuilder.of(Base64Head.I_RED.get()).setEmptyName().get(), 1);
        item2.addFrame(ItemBuilder.of(Base64Head.I_ORANGE.get()).setEmptyName().get(), 1);
        item2.addFrame(ItemBuilder.of(Base64Head.I_YELLOW.get()).setEmptyName().get(), 1);

        AnimatedGUIItem item3 = new AnimatedGUIItem(null, false, 12);
        item3.addFrame(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get(), 5);
        item3.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item3.addFrame(ItemBuilder.of(Base64Head.M_WHITE.get()).setEmptyName().get(), 5);
        item3.addFrame(ItemBuilder.of(Base64Head.M_RED.get()).setEmptyName().get(), 1);
        item3.addFrame(ItemBuilder.of(Base64Head.M_ORANGE.get()).setEmptyName().get(), 1);
        item3.addFrame(ItemBuilder.of(Base64Head.M_YELLOW.get()).setEmptyName().get(), 1);

        AnimatedGUIItem item4 = new AnimatedGUIItem(null, false, 13);
        item4.addFrame(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get(), 5);
        item4.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item4.addFrame(ItemBuilder.of(Base64Head.P_WHITE.get()).setEmptyName().get(), 5);
        item4.addFrame(ItemBuilder.of(Base64Head.P_RED.get()).setEmptyName().get(), 1);
        item4.addFrame(ItemBuilder.of(Base64Head.P_ORANGE.get()).setEmptyName().get(), 1);
        item4.addFrame(ItemBuilder.of(Base64Head.P_YELLOW.get()).setEmptyName().get(), 1);

        AnimatedGUIItem item5 = new AnimatedGUIItem(null, false, 14);
        item5.addFrame(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get(), 5);
        item5.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item5.addFrame(ItemBuilder.of(Base64Head.L_WHITE.get()).setEmptyName().get(), 5);
        item5.addFrame(ItemBuilder.of(Base64Head.L_RED.get()).setEmptyName().get(), 1);
        item5.addFrame(ItemBuilder.of(Base64Head.L_ORANGE.get()).setEmptyName().get(), 1);
        item5.addFrame(ItemBuilder.of(Base64Head.L_YELLOW.get()).setEmptyName().get(), 1);

        AnimatedGUIItem item6 = new AnimatedGUIItem(null, false, 15);
        item6.addFrame(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get(), 5);
        item6.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item6.addFrame(ItemBuilder.of(Base64Head.E_WHITE.get()).setEmptyName().get(), 5);
        item6.addFrame(ItemBuilder.of(Base64Head.E_RED.get()).setEmptyName().get(), 1);
        item6.addFrame(ItemBuilder.of(Base64Head.E_ORANGE.get()).setEmptyName().get(), 1);
        item6.addFrame(ItemBuilder.of(Base64Head.E_YELLOW.get()).setEmptyName().get(), 1);


        AnimatedGUIItem item7 = new AnimatedGUIItem(null, false, 20);
        item7.addFrame(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get(), 5);
        item7.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item7.addFrame(ItemBuilder.of(Base64Head.S_WHITE.get()).setEmptyName().get(), 5);
        item7.addFrame(ItemBuilder.of(Base64Head.S_BLACK.get()).setEmptyName().get(), 1);
        item7.addFrame(ItemBuilder.of(Base64Head.S_RED.get()).setEmptyName().get(), 1);
        item7.addFrame(ItemBuilder.of(Base64Head.S_ORANGE.get()).setEmptyName().get(), 1);

        AnimatedGUIItem item8 = new AnimatedGUIItem(null, false, 19);
        item8.addFrame(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get(), 5);
        item8.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item8.addFrame(ItemBuilder.of(Base64Head.T_WHITE.get()).setEmptyName().get(), 5);
        item8.addFrame(ItemBuilder.of(Base64Head.T_BLACK.get()).setEmptyName().get(), 1);
        item8.addFrame(ItemBuilder.of(Base64Head.T_RED.get()).setEmptyName().get(), 1);
        item8.addFrame(ItemBuilder.of(Base64Head.T_ORANGE.get()).setEmptyName().get(), 1);

        AnimatedGUIItem item9 = new AnimatedGUIItem(null, false, 18);
        item9.addFrame(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get(), 5);
        item9.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item9.addFrame(ItemBuilder.of(Base64Head.A_WHITE.get()).setEmptyName().get(), 5);
        item9.addFrame(ItemBuilder.of(Base64Head.A_BLACK.get()).setEmptyName().get(), 1);
        item9.addFrame(ItemBuilder.of(Base64Head.A_RED.get()).setEmptyName().get(), 1);
        item9.addFrame(ItemBuilder.of(Base64Head.A_ORANGE.get()).setEmptyName().get(), 1);

        AnimatedGUIItem item10 = new AnimatedGUIItem(null, false, 17);
        item10.addFrame(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get(), 5);
        item10.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item10.addFrame(ItemBuilder.of(Base64Head.C_WHITE.get()).setEmptyName().get(), 5);
        item10.addFrame(ItemBuilder.of(Base64Head.C_BLACK.get()).setEmptyName().get(), 1);
        item10.addFrame(ItemBuilder.of(Base64Head.C_RED.get()).setEmptyName().get(), 1);
        item10.addFrame(ItemBuilder.of(Base64Head.C_ORANGE.get()).setEmptyName().get(), 1);

        AnimatedGUIItem item11 = new AnimatedGUIItem(null, false, 16);
        item11.addFrame(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get(), 5);
        item11.addFrame(-1, 0, MovementType.OVERRIDE_ITEM, true, 5);
        item11.addFrame(ItemBuilder.of(Base64Head.K_WHITE.get()).setEmptyName().get(), 5);
        item11.addFrame(ItemBuilder.of(Base64Head.K_BLACK.get()).setEmptyName().get(), 1);
        item11.addFrame(ItemBuilder.of(Base64Head.K_RED.get()).setEmptyName().get(), 1);
        item11.addFrame(ItemBuilder.of(Base64Head.K_ORANGE.get()).setEmptyName().get(), 1);

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

    /**
     * Generate the background flying block animation
     *
     * @param flyLayer The fly <tt>GUILayer</tt> that will be used
     */
    private void genFly(GUILayer flyLayer)
    {
        for(int col = 1; col <= 9; ++col)
        {
            GUIItem item = getFlyItem(col);
            flyLayer.setItem(1, col, item);
        }
    }

    /**
     * Generate the background of the about screen. This includes
     * the cool transition from nothing to purple.
     *
     * @param base The base <tt>GUILayer</tt> that will be used
     */
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

    /**
     * Get the background <tt>AnimatedGUIItem</tt> of the background with the transition
     * animation
     *
     * @param delay The delay of the animation
     * @return The new background item
     */
    private AnimatedGUIItem getBackgroundItem(int delay)
    {
        AnimatedGUIItem item = new AnimatedGUIItem(null, false, delay);
        item.addFrame(ItemBuilder.of(Material.PINK_STAINED_GLASS_PANE).setEmptyName().get(), 1);
        item.addFrame(ItemBuilder.of(Material.MAGENTA_STAINED_GLASS_PANE).setEmptyName().get(), 1);
        item.addFrame(ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE).setEmptyName().get(), 1);
        return item;
    }

    /**
     * Get the item that flies around in the background on the about screen.
     *
     * @param column The column that the item is located on
     * @return The new flying item
     */
    private AnimatedGUIItem getFlyItem(int column)
    {
        Random random = new Random();
        AnimatedGUIItem item = new AnimatedGUIItem(null, true, random.nextInt(50) + 50);
        String str = null;
        int randNum = random.nextInt(4);
        switch(randNum)
        {
            case 0:
                str = Base64Head.BLUE.get();
                break;
            case 1:
                str = Base64Head.PURPLE.get();
                break;
            case 2:
                str = Base64Head.PINK.get();
                break;
            case 3:
                str = Base64Head.MAGENTA.get();
                break;
            case 4:
                str = Base64Head.LIGHT_BLUE.get();
                break;
        }
        ItemStack stack = ItemBuilder.of(str).setEmptyName().get();
        item.addFrame(stack, 1, column, MovementType.OVERRIDE_ITEM, false, 1);
        for(int row = 2; row <= 6; ++row)
        {
            item.addFrame(row, column, MovementType.OVERRIDE_ITEM, false, 1);
        }
        item.addFrame(null, random.nextInt(50) + 25);
        return item;
    }
}
