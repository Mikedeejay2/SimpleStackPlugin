package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIBorderModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Heads;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemCreator;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.gui.GUICreator;
import com.mikedeejay2.simplestack.gui.events.GUIHopperMovementEvent;
import com.mikedeejay2.simplestack.gui.events.GUIMaxStackEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GUIConfigModule extends GUIModule
{
    private final Simplestack plugin;

    public GUIConfigModule(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onOpenHead(Player player, GUIContainer gui)
    {
        GUILayer layer = gui.getLayer(0);
        Config config = plugin.config();

        GUIItem itemTypeList = getGUIItemItemTypeList();
        GUIItem uniqueItemList = getGUIItemUniqueItemList();
        GUIItem language = getGUIItemLanguage();
        GUIItem defaultMaxAmount = getGUIItemDefaultMaxAmount(config);
        GUIItem hopperMovement = getGUIItemHopperMovement(config);

        layer.setItem(3, 3, itemTypeList);
        layer.setItem(3, 4, uniqueItemList);
        layer.setItem(3, 5, language);
        layer.setItem(3, 6, defaultMaxAmount);
        layer.setItem(3, 7, hopperMovement);
    }

    private GUIItem getGUIItemHopperMovement(Config config)
    {
        GUIItem hopperMovement = new GUIItem(null);
        if(config.isHopperMovement())
        {
            hopperMovement.setItem(ItemCreator.createHeadItem(Base64Heads.GREEN, 1,
                    "&fStack Hopper Movements",
                    "&aEnabled"));
        }
        else
        {
            hopperMovement.setItem(ItemCreator.createHeadItem(Base64Heads.RED, 1,
                    "&fStack Hopper Movements",
                    "&cDisabled"));
        }
        GUIHopperMovementEvent hopperMovementEvent = new GUIHopperMovementEvent(plugin);
        hopperMovement.addEvent(hopperMovementEvent);
        return hopperMovement;
    }

    private GUIItem getGUIItemLanguage()
    {
        GUIItem language = new GUIItem(ItemCreator.createHeadItem(Base64Heads.GLOBE, 1,
                "&fDefault Language"));
        language.addEvent(new GUIOpenNewEvent(plugin, () -> {
            GUIContainer langListGUI = new GUIContainer(plugin, "Change Language...", 5);
            GUIBorderModule border = new GUIBorderModule();
            langListGUI.addModule(border);
            GUINavigatorModule navi = new GUINavigatorModule(plugin, "config");
            langListGUI.addModule(navi);
            GUIListModule langList = new GUIListModule(plugin);
            langListGUI.addModule(langList);
            langList.setGUIItems(GUICreator.getLanguageList(plugin));
            GUIAnimationModule animModule = new GUIAnimationModule(plugin, 10);
            langListGUI.addModule(animModule);
            return langListGUI;
        }));
        return language;
    }

    private GUIItem getGUIItemDefaultMaxAmount(Config config)
    {
        GUIItem defaultMaxAmount = new GUIItem(ItemCreator.createItem(Material.BOOK, config.getMaxAmount(),
                "&fDefault Max Amount",
                "&7Sets the default maximum stack amount",
                "&7for ALL items in Minecraft",
                "&7Left click to decrease the max amount",
                "&7Right click to increase the max amount"));
        GUIMaxStackEvent maxAmountEvent = new GUIMaxStackEvent(plugin);
        defaultMaxAmount.addEvent(maxAmountEvent);
        return defaultMaxAmount;
    }

    private GUIItem getGUIItemUniqueItemList()
    {
        return new GUIItem(ItemCreator.createItem(Material.DIAMOND_AXE, 1,
                "&fUnique Item List"));
    }

    private GUIItem getGUIItemItemTypeList()
    {
        return new GUIItem(ItemCreator.createItem(Material.ENDER_PEARL, 1,
                "&fItem Type List"));
    }
}
