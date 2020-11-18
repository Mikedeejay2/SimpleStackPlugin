package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUICloseEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorListSI;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorListSM;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractHandlerList;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIBorderModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.util.chat.Chat;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Heads;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemCreator;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.gui.GUICreator;
import com.mikedeejay2.simplestack.gui.events.GUIHopperMovementEvent;
import com.mikedeejay2.simplestack.gui.events.GUIMaxStackEvent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GUIConfigModule extends GUIModule
{
    private final Simplestack plugin;
    protected final int LIST_ANIM_AMOUNT = 8;
    protected final int LIST_PREVIEW_AMOUNT = 5;

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
        GUIItem itemTypeAmountList = getGUIItemItemTypeAmountList();
        GUIItem uniqueItemList = getGUIItemUniqueItemList();
        GUIItem language = getGUIItemLanguage();
        GUIItem defaultMaxAmount = getGUIItemDefaultMaxAmount(config);
        GUIItem hopperMovement = getGUIItemHopperMovement(config);

        layer.setItem(2, 3, itemTypeList);
        layer.setItem(2, 4, itemTypeAmountList);
        layer.setItem(2, 5, uniqueItemList);
        layer.setItem(2, 6, language);
        layer.setItem(2, 7, defaultMaxAmount);
        layer.setItem(3, 3, hopperMovement);

        GUIItem closeItem = getGUIItemCloseItem();
        layer.setItem(5, 5, closeItem);

        GUIItem aboutItem = getGUIItemAboutItem();
        layer.setItem(1, 5, aboutItem);
    }

    private GUIItem getGUIItemAboutItem()
    {
        String name = "About...";
        AnimatedGUIItem aboutItem = new AnimatedGUIItem(ItemCreator.createItem(Material.BOOK, 1, Chat.chat("&f" + name)), true);
        aboutItem.addFrame(ItemCreator.createItem(Material.WRITABLE_BOOK, 1, Chat.chat("&f" + name)), 10);
        aboutItem.addFrame(ItemCreator.createItem(Material.WRITABLE_BOOK, 1, Chat.chat("&f&o" + name)), 10);
        aboutItem.addEvent(new GUIOpenNewEvent(plugin, () -> {
            GUIContainer gui = new GUIContainer(plugin, "About", 6);
            GUIAnimationModule animModule = new GUIAnimationModule(plugin, 1);
            gui.addModule(animModule);
            GUIAboutModule aboutModule = new GUIAboutModule(plugin);
            gui.addModule(aboutModule);
            return gui;
        }));
        return aboutItem;
    }

    private GUIItem getGUIItemCloseItem()
    {
        GUIItem closeItem = new GUIItem(ItemCreator.createHeadItem(Base64Heads.X_RED, 1, "&c&oClose this menu"));
        closeItem.addEvent(new GUICloseEvent(plugin));
        return closeItem;
    }

    private GUIItem getGUIItemItemTypeAmountList()
    {
        AnimatedGUIItem itemTypeAmountList = new AnimatedGUIItem(ItemCreator.createItem(Material.WATER_BUCKET, 23,
                "&eItem Type Amounts List",
                "&fSet the max amounts of item types"), true);
        final Map<Material, Integer> itemAmounts = plugin.config().getItemAmounts();
        if(itemAmounts.size() > 0)
        {
            Iterator<Map.Entry<Material, Integer>> iter1 = itemAmounts.entrySet().iterator();
            List<String> lore = itemTypeAmountList.getLore() == null ? new ArrayList<>() : itemTypeAmountList.getLore();
            lore.add("");
            for(int i = 0; i < Math.min(LIST_PREVIEW_AMOUNT, itemAmounts.size()); ++i)
            {
                Map.Entry<Material, Integer> entry = iter1.next();
                Material material = entry.getKey();
                if(material == null) continue;
                int amount = entry.getValue();
                String name = WordUtils.capitalize(material.toString().replace("_", " ").toLowerCase());
                lore.add(Chat.chat("&7" + name + " x" + amount));
            }
            if(itemAmounts.size() > LIST_PREVIEW_AMOUNT)
            {
                lore.add(Chat.chat("&7and " + (itemAmounts.size() - LIST_PREVIEW_AMOUNT) + " more..."));
            }
            itemTypeAmountList.setLore(lore);
            Iterator<Map.Entry<Material, Integer>> iter2 = itemAmounts.entrySet().iterator();
            for(int i = 0; i < Math.min(LIST_ANIM_AMOUNT, itemAmounts.size()); ++i)
            {
                Map.Entry<Material, Integer> entry = iter2.next();
                Material material = entry.getKey();
                if(material == null) continue;
                int amount = entry.getValue();
                ItemStack item = new ItemStack(material, amount);
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(itemTypeAmountList.getName());
                itemMeta.setLore(itemTypeAmountList.getLore());
                item.setItemMeta(itemMeta);
                itemTypeAmountList.addFrame(item, 20);
            }
        }
        itemTypeAmountList.addEvent(new GUIOpenNewEvent(plugin, () -> {
            GUIContainer gui = new GUIContainer(plugin, "Item Type Amounts List", 6);
            GUIBorderModule border = new GUIBorderModule();
            gui.addModule(border);
            GUINavigatorModule navi = new GUINavigatorModule(plugin, "config");
            gui.addModule(navi);
            GUIListModule listModule = new GUIListModule(plugin);
            GUIItem padItem = new GUIItem(null);
            padItem.setMovable(true);
            listModule.addEndItem(padItem);
            for(Map.Entry<Material, Integer> entry : itemAmounts.entrySet())
            {
                Material material = entry.getKey();
                if(material == null) continue;
                int amount = entry.getValue();
                ItemStack item = new ItemStack(material, amount);
                GUIItem guiItem = new GUIItem(item);
                guiItem.setMovable(true);
                listModule.addListItem(guiItem);
            }
            gui.addModule(listModule);
            GUIInteractHandler interaction = new GUIInteractHandlerList(64);
            interaction.removeExecutor(GUIInteractExecutorList.class);
            interaction.addExecutor(new GUIInteractExecutorListSM(64));
            gui.setDefaultMoveState(true);
            gui.setInteractionHandler(interaction);
            GUIItemTypeAmountModule itemTypeAmountModule = new GUIItemTypeAmountModule(plugin);
            gui.addModule(itemTypeAmountModule);
            return gui;
        }));
        return itemTypeAmountList;
    }

    private GUIItem getGUIItemHopperMovement(Config config)
    {
        GUIItem hopperMovement = new GUIItem(null);
        if(config.isHopperMovement())
        {
            hopperMovement.setItem(ItemCreator.createHeadItem(Base64Heads.GREEN, 1,
                    "&eStack Hopper Movements",
                    "",
                    "&a&l⊳ Enabled",
                    "&7  Disabled"));
        }
        else
        {
            hopperMovement.setItem(ItemCreator.createHeadItem(Base64Heads.RED, 1,
                    "&eStack Hopper Movements",
                    "",
                    "&7  Enabled",
                    "&c&l⊳ Disabled"));
        }
        GUIHopperMovementEvent hopperMovementEvent = new GUIHopperMovementEvent(plugin);
        hopperMovement.addEvent(hopperMovementEvent);
        return hopperMovement;
    }

    private GUIItem getGUIItemLanguage()
    {
        GUIItem language = new GUIItem(ItemCreator.createHeadItem(Base64Heads.GLOBE, 1,
                "&eDefault Language",
                "&fClick to select a new language",
                "&7Currently selected: " + plugin.config().getLangLocale()));
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
                "&eDefault Max Amount",
                "&fSets the default maximum stack amount",
                "&ffor ALL items in Minecraft",
                "&7Left click to decrease the max amount",
                "&7Right click to increase the max amount"));
        GUIMaxStackEvent maxAmountEvent = new GUIMaxStackEvent(plugin);
        defaultMaxAmount.addEvent(maxAmountEvent);
        return defaultMaxAmount;
    }

    private GUIItem getGUIItemUniqueItemList()
    {
        AnimatedGUIItem uniqueItemList = new AnimatedGUIItem(ItemCreator.createItem(Material.CYAN_CONCRETE_POWDER, 1,
                "&eUnique Item List",
                "&fA list for unique items",
                "&7Unique items are matched exactly!",
                "&7(All item data must match)"), true);
        final List<ItemStack> uniqueItems = plugin.config().getUniqueItemList();
        if(uniqueItems.size() > 0)
        {
            List<String> lore = uniqueItemList.getLore() == null ? new ArrayList<>() : uniqueItemList.getLore();
            lore.add("");
            for(int i = 0; i < Math.min(LIST_PREVIEW_AMOUNT, uniqueItems.size()); ++i)
            {
                ItemStack item = uniqueItems.get(i);
                String name = WordUtils.capitalize(item.getType().toString().replace("_", " ").toLowerCase());
                lore.add(Chat.chat("&7" + name));
            }
            if(uniqueItems.size() > LIST_PREVIEW_AMOUNT)
            {
                lore.add(Chat.chat("&7and " + (uniqueItems.size() - LIST_PREVIEW_AMOUNT) + " more..."));
            }
            uniqueItemList.setLore(lore);
            for(int i = 0; i < Math.min(LIST_ANIM_AMOUNT, uniqueItems.size()); ++i)
            {
                ItemStack item = uniqueItems.get(i);
                ItemStack newItem = item.clone();
                ItemMeta itemMeta = newItem.getItemMeta();
                itemMeta.setDisplayName(uniqueItemList.getName());
                itemMeta.setLore(uniqueItemList.getLore());
                newItem.setItemMeta(itemMeta);
                uniqueItemList.addFrame(newItem, 20);
            }
        }
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
        AnimatedGUIItem itemTypeList = new AnimatedGUIItem(ItemCreator.createItem(Material.ENDER_PEARL, 1,
                "&eItem Type List",
                "&fA list of item types",
                "&7Items in this list can either be",
                "&7Blacklist or whitelist."), true);
        final List<Material> materialItems = plugin.config().getMaterialList();
        if(materialItems.size() > 0)
        {
            List<String> lore = itemTypeList.getLore() == null ? new ArrayList<>() : itemTypeList.getLore();
            lore.add("");
            for(int i = 0; i < Math.min(LIST_PREVIEW_AMOUNT, materialItems.size()); ++i)
            {
                Material material = materialItems.get(i);
                String name = WordUtils.capitalize(material.toString().replace("_", " ").toLowerCase());
                lore.add(Chat.chat("&7" + name));
            }
            if(materialItems.size() > LIST_PREVIEW_AMOUNT)
            {
                lore.add(Chat.chat("&7and " + (materialItems.size() - LIST_PREVIEW_AMOUNT) + " more..."));
            }
            itemTypeList.setLore(lore);
            for(int i = 0; i < Math.min(LIST_ANIM_AMOUNT, materialItems.size()); ++i)
            {
                Material material = materialItems.get(i);
                ItemStack item = new ItemStack(material);
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(itemTypeList.getName());
                itemMeta.setLore(itemTypeList.getLore());
                item.setItemMeta(itemMeta);
                itemTypeList.addFrame(item, 20);
            }
        }
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
