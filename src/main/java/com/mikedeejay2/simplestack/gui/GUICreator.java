package com.mikedeejay2.simplestack.gui;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractHandlerDefault;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimStrips;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIOutlineModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemCreator;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;

public class GUICreator
{
    private static AnimatedGUIItem animatedGUIItem = null;

    public static GUIContainer createMainGUI(Simplestack plugin)
    {
        GUIContainer gui = new GUIContainer(plugin, "Configuration GUI", 5);
        GUIAnimationModule animation = new GUIAnimationModule(plugin, 1);
        GUIAnimStrips outlineModule = new GUIAnimStrips(getAnimItem());
        GUINavigatorModule naviModule = new GUINavigatorModule(plugin, "config");
        gui.addModule(animation);
        gui.addModule(outlineModule);
        gui.addModule(naviModule);
        GUIInteractHandler handler = new GUIInteractHandlerDefault(64);
        gui.setInteractionHandler(handler);

        return gui;
    }

    private static AnimatedGUIItem getAnimItem()
    {
        if(animatedGUIItem == null)
        {
            AnimatedGUIItem item = new AnimatedGUIItem(ItemCreator.createItem(Material.BLUE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), true);
            item.addFrame(ItemCreator.createItem(Material.BLUE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 10);
            item.addFrame(ItemCreator.createItem(Material.PURPLE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 10);
            item.addFrame(ItemCreator.createItem(Material.MAGENTA_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 10);
            item.addFrame(ItemCreator.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 10);
            item.addFrame(ItemCreator.createItem(Material.CYAN_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 10);
            item.addFrame(ItemCreator.createItem(Material.PURPLE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 10);
            animatedGUIItem = item;
        }
        return animatedGUIItem;
    }
}
