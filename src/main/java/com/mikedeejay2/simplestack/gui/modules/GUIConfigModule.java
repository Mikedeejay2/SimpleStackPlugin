package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUICloseEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractType;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractHandlerList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractExecutorDefaultInv;
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

        GUIItem itemTypeList = getGUIItemItemTypeList(player);
        GUIItem itemTypeAmountList = getGUIItemItemTypeAmountList(player);
        GUIItem uniqueItemList = getGUIItemUniqueItemList(player);
        GUIItem language = getGUIItemLanguage(player);
        GUIItem defaultMaxAmount = getGUIItemDefaultMaxAmount(config, player);
        GUIItem hopperMovement = getGUIItemHopperMovement(config, player);

        layer.setItem(2, 3, itemTypeList);
        layer.setItem(2, 4, itemTypeAmountList);
        layer.setItem(2, 5, uniqueItemList);
        layer.setItem(2, 6, language);
        layer.setItem(2, 7, defaultMaxAmount);
        layer.setItem(3, 3, hopperMovement);

        GUIItem closeItem = getGUIItemCloseItem(player);
        layer.setItem(5, 5, closeItem);

        GUIItem aboutItem = getGUIItemAboutItem(player);
        layer.setItem(1, 5, aboutItem);
    }

    private GUIItem getGUIItemAboutItem(Player player)
    {
        String name = plugin.langManager().getText(player, "simplestack.gui.config.about_select");
        AnimatedGUIItem aboutItem = new AnimatedGUIItem(ItemCreator.createItem(Material.BOOK, 1, Chat.chat("&f" + name)), true);
        aboutItem.addFrame(ItemCreator.createItem(Material.WRITABLE_BOOK, 1, Chat.chat("&f" + name)), 10);
        aboutItem.addFrame(ItemCreator.createItem(Material.WRITABLE_BOOK, 1, Chat.chat("&f&o" + name)), 10);
        aboutItem.addEvent(new GUIOpenNewEvent(plugin, () -> {
            GUIContainer gui = new GUIContainer(plugin, name, 6);
            GUIAnimationModule animModule = new GUIAnimationModule(plugin, 1);
            gui.addModule(animModule);
            GUIAboutModule aboutModule = new GUIAboutModule(plugin);
            gui.addModule(aboutModule);
            return gui;
        }));
        return aboutItem;
    }

    private GUIItem getGUIItemCloseItem(Player player)
    {
        GUIItem closeItem = new GUIItem(ItemCreator.createHeadItem(Base64Heads.X_RED, 1,
                "&c&o" + plugin.langManager().getText(player, "simplestack.gui.config.close_select")));
        closeItem.addEvent(new GUICloseEvent(plugin));
        return closeItem;
    }

    private GUIItem getGUIItemItemTypeAmountList(Player player)
    {
        AnimatedGUIItem itemTypeAmountList = new AnimatedGUIItem(ItemCreator.createItem(Material.WATER_BUCKET, 23,
                "&e" + plugin.langManager().getText(player, "simplestack.gui.item_type_amts.title"),
                "&f" + plugin.langManager().getText(player, "simplestack.gui.config.item_type_description")), true);
        final Map<Material, Integer> itemAmounts = plugin.config().getItemAmounts();
        if(itemAmounts.size() > 0 && plugin.langManager().getDefaultLang().equals("en_us"))
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
                lore.add(Chat.chat("&7" + plugin.langManager().getText(player, "simplestack.gui.config.preview_list_more",
                        new String[]{"AMT"},
                        new String[]{String.valueOf((itemAmounts.size() - LIST_PREVIEW_AMOUNT))})));
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
            GUIContainer gui = new GUIContainer(plugin, plugin.langManager().getText(player, "simplestack.gui.item_type_amts.title"), 6);
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
            interaction.resetExecutors();
            interaction.addExecutor(new GUIInteractExecutorDefaultInv(64));
            interaction.addExecutor(new GUIInteractExecutorList(GUIInteractType.SINGLE_MATERIAL, 64, false));
            gui.setDefaultMoveState(true);
            gui.setInteractionHandler(interaction);
            GUIItemTypeAmountModule itemTypeAmountModule = new GUIItemTypeAmountModule(plugin);
            gui.addModule(itemTypeAmountModule);
            return gui;
        }));
        return itemTypeAmountList;
    }

    private GUIItem getGUIItemHopperMovement(Config config, Player player)
    {
        GUIItem hopperMovement = new GUIItem(null);
        if(config.isHopperMovement())
        {
            hopperMovement.setItem(ItemCreator.createHeadItem(Base64Heads.GREEN, 1,
                    "&e" + plugin.langManager().getText(player, "simplestack.gui.config.hopper_move_select"),
                    "",
                    "&a&l⊳ " + plugin.langManager().getTextLib(player, "generic.enabled"),
                    "&7  " + plugin.langManager().getTextLib(player, "generic.disabled")));
        }
        else
        {
            hopperMovement.setItem(ItemCreator.createHeadItem(Base64Heads.RED, 1,
                    "&e" + plugin.langManager().getText(player, "simplestack.gui.config.hopper_move_select"),
                    "",
                    "&7  " + plugin.langManager().getTextLib(player, "generic.enabled"),
                    "&c&l⊳ " + plugin.langManager().getTextLib(player, "generic.disabled")));
        }
        GUIHopperMovementEvent hopperMovementEvent = new GUIHopperMovementEvent(plugin);
        hopperMovement.addEvent(hopperMovementEvent);
        return hopperMovement;
    }

    private GUIItem getGUIItemLanguage(Player player)
    {
        GUIItem language = new GUIItem(ItemCreator.createHeadItem(Base64Heads.GLOBE, 1,
                "&e" + plugin.langManager().getText(player, "simplestack.gui.config.language_select"),
                "&f" + plugin.langManager().getText(player, "simplestack.gui.config.language_description"),
                "&7Currently selected: " + plugin.langManager().getText(player, "simplestack.gui.config.language_selected",
                        new String[]{"LANG"},
                        new String[]{plugin.config().getLangLocale()})
        ));
        language.addEvent(new GUIOpenNewEvent(plugin, () -> {
            GUIContainer gui = new GUIContainer(plugin, plugin.langManager().getText(player, "simplestack.gui.language.title"), 5);
            GUIBorderModule border = new GUIBorderModule();
            gui.addModule(border);
            GUINavigatorModule navi = new GUINavigatorModule(plugin, "config");
            gui.addModule(navi);
            GUIListModule langList = new GUIListModule(plugin);
            gui.addModule(langList);
            langList.setGUIItems(GUICreator.getLanguageList(plugin, player));
            GUIAnimationModule animModule = new GUIAnimationModule(plugin, 10);
            gui.addModule(animModule);
            return gui;
        }));
        return language;
    }

    private GUIItem getGUIItemDefaultMaxAmount(Config config, Player player)
    {
        GUIItem defaultMaxAmount = new GUIItem(ItemCreator.createItem(Material.BOOK, config.getMaxAmount(),
                "&e" + plugin.langManager().getText(player, "simplestack.gui.config.default_max_select"),
                "&f" + plugin.langManager().getText(player, "simplestack.gui.config.default_max_desc_l1"),
                "&f" + plugin.langManager().getText(player, "simplestack.gui.config.default_max_desc_l2"),
                "&7" + plugin.langManager().getText(player, "simplestack.gui.config.default_max_desc_l3"),
                "&7" + plugin.langManager().getText(player, "simplestack.gui.config.default_max_desc_l4")));
        GUIMaxStackEvent maxAmountEvent = new GUIMaxStackEvent(plugin);
        defaultMaxAmount.addEvent(maxAmountEvent);
        return defaultMaxAmount;
    }

    private GUIItem getGUIItemUniqueItemList(Player player)
    {
        AnimatedGUIItem uniqueItemList = new AnimatedGUIItem(ItemCreator.createItem(Material.CYAN_CONCRETE_POWDER, 1,
                "&e" + plugin.langManager().getText(player, "simplestack.gui.unique_items.title"),
                "&f" + plugin.langManager().getText(player, "simplestack.gui.config.unique_item_desc_l1"),
                "&7" + plugin.langManager().getText(player, "simplestack.gui.config.unique_item_desc_l2"),
                "&7" + plugin.langManager().getText(player, "simplestack.gui.config.unique_item_desc_l3")), true);
        final List<ItemStack> uniqueItems = plugin.config().getUniqueItemList();
        if(uniqueItems.size() > 0 && plugin.langManager().getDefaultLang().equals("en_us"))
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
                lore.add(Chat.chat("&7" + plugin.langManager().getText(player, "simplestack.gui.config.preview_list_more",
                        new String[]{"AMT"},
                        new String[]{String.valueOf((uniqueItems.size() - LIST_PREVIEW_AMOUNT))})));
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
            GUIContainer gui = new GUIContainer(plugin, plugin.langManager().getText(player, "simplestack.gui.unique_items.title"), 6);
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
            interaction.resetExecutors();
            interaction.addExecutor(new GUIInteractExecutorDefaultInv(64));
            interaction.addExecutor(new GUIInteractExecutorList(GUIInteractType.SINGLE_ITEM, 64, false));
            gui.setDefaultMoveState(true);
            gui.setInteractionHandler(interaction);
            GUIUniqueItemListModule uniqueItemModule = new GUIUniqueItemListModule(plugin);
            gui.addModule(uniqueItemModule);
            return gui;
        }));
        return uniqueItemList;
    }

    private GUIItem getGUIItemItemTypeList(Player player)
    {
        AnimatedGUIItem itemTypeList = new AnimatedGUIItem(ItemCreator.createItem(Material.ENDER_PEARL, 1,
                "&e" + plugin.langManager().getText(player, "simplestack.gui.item_types.title"),
                "&f" + plugin.langManager().getText(player, "simplestack.gui.config.item_type_desc_l1"),
                "&7" + plugin.langManager().getText(player, "simplestack.gui.config.item_type_desc_l2"),
                "&7" + plugin.langManager().getText(player, "simplestack.gui.config.item_type_desc_l3")), true);
        final List<Material> materialItems = plugin.config().getMaterialList();
        if(materialItems.size() > 0 && plugin.langManager().getDefaultLang().equals("en_us"))
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
            GUIContainer gui = new GUIContainer(plugin, plugin.langManager().getText(player, "simplestack.gui.item_types.title"), 6);
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
            GUIInteractHandler interaction = new GUIInteractHandlerList(64);
            interaction.resetExecutors();
            interaction.addExecutor(new GUIInteractExecutorDefaultInv(64));
            interaction.addExecutor(new GUIInteractExecutorList(GUIInteractType.SINGLE_MATERIAL, 1, false));
            gui.setDefaultMoveState(true);
            gui.setInteractionHandler(interaction);
            GUIItemTypeListModule uniqueItemModule = new GUIItemTypeListModule(plugin);
            gui.addModule(uniqueItemModule);
            return gui;
        }));
        return itemTypeList;
    }
}
