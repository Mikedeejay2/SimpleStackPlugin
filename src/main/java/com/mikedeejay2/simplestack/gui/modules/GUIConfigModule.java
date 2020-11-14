package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorListSI;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorListSM;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractHandlerList;
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
import org.bukkit.inventory.ItemStack;

import java.util.List;

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
            GUIContainer gui = new GUIContainer(plugin, "Change Language...", 5);
            GUIBorderModule border = new GUIBorderModule();
            gui.addModule(border);
            GUINavigatorModule navi = new GUINavigatorModule(plugin, "config");
            gui.addModule(navi);
            GUIListModule langList = new GUIListModule(plugin);
            gui.addModule(langList);
            langList.setGUIItems(GUICreator.getLanguageList(plugin));
            GUIAnimationModule animModule = new GUIAnimationModule(plugin, 10);
            gui.addModule(animModule);
            return gui;
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
        GUIItem uniqueItemList = new GUIItem(ItemCreator.createItem(Material.DIAMOND_AXE, 1,
                "&fUnique Item List"));
        uniqueItemList.addEvent(new GUIOpenNewEvent(plugin, () -> {
            GUIContainer gui = new GUIContainer(plugin, "Unique Item List", 6);
            GUIBorderModule border = new GUIBorderModule();
            gui.addModule(border);
            GUINavigatorModule navi = new GUINavigatorModule(plugin, "config");
            gui.addModule(navi);
            GUIListModule listModule = new GUIListModule(plugin);
            GUIItem padItem = new GUIItem(null);
            padItem.setMovable(true);
            listModule.addEndItem(padItem);
            List<ItemStack> uniqueItems = plugin.config().getUniqueItemList();
            for(ItemStack item : uniqueItems)
            {
                GUIItem guiItem = new GUIItem(item);
                guiItem.setMovable(true);
                listModule.addListItem(guiItem);
            }
            gui.addModule(listModule);
            GUIInteractHandler interaction = new GUIInteractHandlerList(64);
            interaction.removeExecutor(GUIInteractExecutorList.class);
            interaction.addExecutor(new GUIInteractExecutorListSI(64));
            gui.setDefaultMoveState(true);
            gui.setInteractionHandler(interaction);
            GUIUniqueItemListModule uniqueItemModule = new GUIUniqueItemListModule(plugin);
            gui.addModule(uniqueItemModule);
            return gui;
        }));
        return uniqueItemList;
    }

    private GUIItem getGUIItemItemTypeList()
    {
        GUIItem itemTypeList = new GUIItem(ItemCreator.createItem(Material.ENDER_PEARL, 1,
                "&fItem Type List"));
        itemTypeList.addEvent(new GUIOpenNewEvent(plugin, () -> {
            GUIContainer gui = new GUIContainer(plugin, "Item Type List", 6);
            GUIBorderModule border = new GUIBorderModule();
            gui.addModule(border);
            GUINavigatorModule navi = new GUINavigatorModule(plugin, "config");
            gui.addModule(navi);
            GUIListModule listModule = new GUIListModule(plugin);
            GUIItem padItem = new GUIItem(null);
            padItem.setMovable(true);
            listModule.addEndItem(padItem);
            List<Material> materialItems = plugin.config().getMaterialList();
            for(Material material : materialItems)
            {
                GUIItem guiItem = new GUIItem(new ItemStack(material));
                guiItem.setMovable(true);
                listModule.addListItem(guiItem);
            }
            gui.addModule(listModule);
            GUIInteractHandler interaction = new GUIInteractHandlerList(1);
            interaction.removeExecutor(GUIInteractExecutorList.class);
            interaction.addExecutor(new GUIInteractExecutorListSM(1));
            gui.setDefaultMoveState(true);
            gui.setInteractionHandler(interaction);
            GUIItemTypeListModule uniqueItemModule = new GUIItemTypeListModule(plugin);
            gui.addModule(uniqueItemModule);
            return gui;
        }));
        return itemTypeList;
    }
}
