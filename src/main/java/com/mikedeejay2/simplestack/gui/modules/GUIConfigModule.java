package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.button.GUIButtonToggleableEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUICloseEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractType;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractHandlerList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractExecutorDefaultInv;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIBorderModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.config.ListMode;
import com.mikedeejay2.simplestack.gui.GUICreator;
import com.mikedeejay2.simplestack.gui.events.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The <tt>GUIModule</tt> for the main configuration GUI screen
 *
 * @author Mikedeejay2
 */
public class GUIConfigModule implements GUIModule
{
    private final Simplestack plugin;
    protected final int LIST_ANIM_AMOUNT = 8;
    protected final int LIST_PREVIEW_AMOUNT = 5;

    public GUIConfigModule(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Overridden <tt>onOpenHead</tt> method that generates the configuration screen
     * when the GUI is opened
     *
     * @param player The player that opened the GUI
     * @param gui The <tt>GUIContainer</tt> that the config GUI is contained in
     */
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
        GUIItem groundStacking = getGUIItemGroundStacking(config, player);
        GUIItem switchListMode = getGUIItemSwitchListMode(player);
        GUIItem creativeDrag = getGUIItemCreativeDrag(player);
        GUIItem stackableArmor = getGUIItemStackableArmor(player);

        layer.setItem(2, 3, itemTypeList);
        layer.setItem(2, 4, itemTypeAmountList);
        layer.setItem(2, 5, uniqueItemList);
        layer.setItem(2, 6, language);
        layer.setItem(2, 7, defaultMaxAmount);
        layer.setItem(3, 3, hopperMovement);
        layer.setItem(3, 4, groundStacking);
        layer.setItem(3, 5, switchListMode);
        layer.setItem(3, 6, creativeDrag);
        layer.setItem(3, 7, stackableArmor);

        GUIItem closeItem = getGUIItemCloseItem(player);
        layer.setItem(5, 5, closeItem);

        GUIItem aboutItem = getGUIItemAboutItem(player);
        layer.setItem(1, 5, aboutItem);
    }

    /**
     * Get the <tt>GUIItem</tt> for the "Creative Item Dragging Mode" button
     *
     * @param player The player (For localization)
     * @return The creative drag mode item
     */
    private GUIItem getGUIItemCreativeDrag(Player player)
    {
        GUIItem creativeDrag = new GUIItem(null);
        if(plugin.config().shouldCreativeDrag())
        {
            creativeDrag.setItem(ItemBuilder.of(Base64Head.GREEN.get())
                                     .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.config.creative_drag_select"))
                                     .setLore(
                                         "",
                                         "&a&l⊳ " + plugin.getLangManager().getText(player, "simplestack.gui.config.creative_drag_duplicate"),
                                         "&7  " + plugin.getLangManager().getText(player, "simplestack.gui.config.creative_drag_normal"))
                                     .get());
        }
        else
        {
            creativeDrag.setItem(ItemBuilder.of(Base64Head.RED.get())
                                     .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.config.creative_drag_select"))
                                     .setLore(
                                         "",
                                         "&7  " + plugin.getLangManager().getText(player, "simplestack.gui.config.creative_drag_duplicate"),
                                         "&c&l⊳ " + plugin.getLangManager().getText(player, "simplestack.gui.config.creative_drag_normal"))
                                     .get());
        }
        creativeDrag.addEvent(new GUICreativeDragEvent(plugin));
        return creativeDrag;
    }

    /**
     * Get the <tt>GUIItem</tt> for the "Stackable Armor Wearable" button
     *
     * @param player The player (For localization)
     * @return The creative drag mode item
     */
    private GUIItem getGUIItemStackableArmor(Player player)
    {
        final ItemStack onItem =
            ItemBuilder.of(Base64Head.GREEN.get())
                .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.config.stackable_armor_select"))
                .setLore(
                    "",
                    "&a&l⊳ " + plugin.getLibLangManager().getText(player, "generic.enabled"),
                    "&7  " + plugin.getLibLangManager().getText(player, "generic.disabled"))
                .get();

        final ItemStack offItem =
            ItemBuilder.of(Base64Head.RED.get())
                .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.config.stackable_armor_select"))
                .setLore(
                    "",
                    "&7  " + plugin.getLibLangManager().getText(player, "generic.enabled"),
                    "&c&l⊳ " + plugin.getLibLangManager().getText(player, "generic.disabled"))
                .get();

        final GUIItem guiItem = new GUIItem(offItem);
        guiItem.addEvent(new GUIButtonToggleableEvent(info -> {
            info.getGUIItem().setItem(onItem);
            plugin.config().setStackableArmor(true);
        }, info -> {
            info.getGUIItem().setItem(offItem);
            plugin.config().setStackableArmor(false);
        }, plugin.config().shouldStackArmor()));

        return guiItem;
    }

    /**
     * Get the <tt>GUIItem</tt> for the "Switch List Mode" button
     *
     * @param player The player (For localization)
     * @return The switch list mode item
     */
    private GUIItem getGUIItemSwitchListMode(Player player)
    {
        GUIItem switchListMode = new GUIItem(null);
        if(plugin.config().getListMode() == ListMode.BLACKLIST)
        {
            switchListMode.setItem(ItemBuilder.of(Base64Head.X_BLACK.get())
                                       .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.list_type.blacklist"))
                                       .setLore(
                                           "&7" + plugin.getLangManager().getText(player, "simplestack.gui.item_types.change_mode_whitelist"),
                                           "",
                                           "&a&l⊳ " + plugin.getLangManager().getText(player, "simplestack.list_type.blacklist"),
                                           "&7  " + plugin.getLangManager().getText(player, "simplestack.list_type.whitelist"))
                                       .get());
        }
        else
        {
            switchListMode.setItem(ItemBuilder.of(Base64Head.CHECKMARK_WHITE.get())
                                       .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.list_type.whitelist"))
                                       .setLore(
                                           "&7" + plugin.getLangManager().getText(player, "simplestack.gui.item_types.change_mode_blacklist"),
                                           "",
                                           "&7  " + plugin.getLangManager().getText(player, "simplestack.list_type.blacklist"),
                                           "&a&l⊳ " + plugin.getLangManager().getText(player, "simplestack.list_type.whitelist"))
                                       .get());
        }
        switchListMode.addEvent(new GUISwitchListModeEvent(plugin));
        return switchListMode;
    }

    /**
     * Get the <tt>GUIItem</tt> for the "about" button
     *
     * @param player The player (For localization)
     * @return The about item
     */
    private GUIItem getGUIItemAboutItem(Player player)
    {
        String name = plugin.getLangManager().getText(player, "simplestack.gui.config.about_select");
        AnimatedGUIItem aboutItem = new AnimatedGUIItem(ItemBuilder.of(Material.BOOK).setName("&f" + name).get(), true);
        aboutItem.addFrame(ItemBuilder.of(Material.WRITABLE_BOOK).setName("&f" + name).get(), 10);
        aboutItem.addFrame(ItemBuilder.of(Material.WRITABLE_BOOK).setName("&f&o" + name).get(), 10);
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

    /**
     * Get the <tt>GUIItem</tt> for the close button
     *
     * @param player The player (For localization)
     * @return The close button
     */
    private GUIItem getGUIItemCloseItem(Player player)
    {
        GUIItem closeItem = new GUIItem(ItemBuilder.of(Base64Head.X_RED.get())
                                            .setName("&c&o" + plugin.getLangManager().getText(player, "simplestack.gui.config.close_select"))
                                            .get());
        closeItem.addEvent(new GUICloseEvent(plugin));
        return closeItem;
    }

    /**
     * Get the <tt>GUIItem</tt> for the item type amount list button
     *
     * @param player The player (For localization)
     * @return The item type amount list button
     */
    private GUIItem getGUIItemItemTypeAmountList(Player player)
    {
        AnimatedGUIItem itemTypeAmountList = new AnimatedGUIItem(ItemBuilder.of(Material.BARRIER)
                                                                     .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.item_type_amts.title"))
                                                                     .setLore(
                                                                         "&f" + plugin.getLangManager().getText(player, "simplestack.gui.config.item_type_description"))
                                                                     .get(), true);
        final Map<Material, Integer> itemAmounts = plugin.config().getItemAmounts();
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
        itemTypeAmountList.addEvent(new GUIOpenNewEvent(plugin, () -> {
            GUIContainer gui = new GUIContainer(plugin, plugin.getLangManager().getText(player, "simplestack.gui.item_type_amts.title"), 6);
            GUIBorderModule border = new GUIBorderModule(new GUIItem(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get()));
            gui.addModule(border);
            GUINavigatorModule navi = new GUINavigatorModule(plugin, "config");
            gui.addModule(navi);
            GUIListModule listModule = new GUIListModule(plugin, GUIListModule.ListViewMode.PAGED, 2, 5, 1, 9);
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

    /**
     * Get the <tt>GUIItem</tt> for the hopper movement button
     *
     * @param config The <tt>Config</tt> reference
     * @param player The player (For localization)
     * @return The hopper movement button
     */
    private GUIItem getGUIItemHopperMovement(Config config, Player player)
    {
        GUIItem hopperMovement = new GUIItem(null);
        if(config.shouldProcessHoppers())
        {
            hopperMovement.setItem(ItemBuilder.of(Base64Head.GREEN.get())
                                       .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.config.hopper_move_select"))
                                       .setLore(
                                           "",
                                           "&a&l⊳ " + plugin.getLibLangManager().getText(player, "generic.enabled"),
                                           "&7  " + plugin.getLibLangManager().getText(player, "generic.disabled"))
                                       .get());
        }
        else
        {
            hopperMovement.setItem(ItemBuilder.of(Base64Head.RED.get())
                                       .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.config.hopper_move_select"))
                                       .setLore(
                                           "",
                                           "&7  " + plugin.getLibLangManager().getText(player, "generic.enabled"),
                                           "&c&l⊳ " + plugin.getLibLangManager().getText(player, "generic.disabled"))
                                       .get());
        }
        GUIHopperMovementEvent hopperMovementEvent = new GUIHopperMovementEvent(plugin);
        hopperMovement.addEvent(hopperMovementEvent);
        return hopperMovement;
    }

    /**
     * Get the <tt>GUIItem</tt> for the ground stacking button
     *
     * @param config The <tt>Config</tt> reference
     * @param player The player (For localization)
     * @return The ground stacking button
     */
    private GUIItem getGUIItemGroundStacking(Config config, Player player)
    {
        GUIItem groundStacking = new GUIItem(null);
        if(config.processGroundItems())
        {
            groundStacking.setItem(ItemBuilder.of(Base64Head.GREEN.get())
                                       .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.config.ground_stacking_select"))
                                       .setLore(
                                           "",
                                           "&a&l⊳ " + plugin.getLibLangManager().getText(player, "generic.enabled"),
                                           "&7  " + plugin.getLibLangManager().getText(player, "generic.disabled"))
                                       .get());
        }
        else
        {
            groundStacking.setItem(ItemBuilder.of(Base64Head.RED.get())
                                       .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.config.ground_stacking_select"))
                                       .setLore(
                                           "",
                                           "&7  " + plugin.getLibLangManager().getText(player, "generic.enabled"),
                                           "&c&l⊳ " + plugin.getLibLangManager().getText(player, "generic.disabled"))
                                       .get());
        }
        GUIGroundStackingEvent groundStackingEvent = new GUIGroundStackingEvent(plugin);
        groundStacking.addEvent(groundStackingEvent);
        return groundStacking;
    }

    /**
     * Get the <tt>GUIItem</tt> for the language select button
     *
     * @param player The player (For localization)
     * @return The language button
     */
    private GUIItem getGUIItemLanguage(Player player)
    {
        GUIItem language = new GUIItem(ItemBuilder.of(Base64Head.GLOBE.get())
                                           .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.config.language_select"))
                                           .setLore(
                                               "&f" + plugin.getLangManager().getText(player, "simplestack.gui.config.language_description"),
                                               "&7Currently selected: " + plugin.getLangManager().getText(player, "simplestack.gui.config.language_selected",
                                                                                                          new String[]{"LANG"},
                                                                                                          new String[]{plugin.config().getLangLocale()}))
                                           .get());
        language.addEvent(new GUIOpenNewEvent(plugin, () -> {
            GUIContainer gui = new GUIContainer(plugin, plugin.getLangManager().getText(player, "simplestack.gui.language.title"), 5);
            GUIBorderModule border = new GUIBorderModule(new GUIItem(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get()));
            gui.addModule(border);
            GUINavigatorModule navi = new GUINavigatorModule(plugin, "config");
            gui.addModule(navi);
            GUIListModule langList = new GUIListModule(plugin, GUIListModule.ListViewMode.PAGED, 2, 5, 1, 9);
            gui.addModule(langList);
            langList.setGUIItems(GUICreator.getLanguageList(plugin, player));
            GUIAnimationModule animModule = new GUIAnimationModule(plugin, 10);
            gui.addModule(animModule);
            return gui;
        }));
        return language;
    }

    /**
     * Get the <tt>GUIItem</tt> for the default max amount button
     *
     * @param config A reference to the Simple Stack config
     * @param player The player (For Localization)
     * @return The default max amount button
     */
    private GUIItem getGUIItemDefaultMaxAmount(Config config, Player player)
    {
        GUIItem defaultMaxAmount = new GUIItem(ItemBuilder.of(Material.BOOK)
                                                   .setAmount(config.getMaxAmount())
                                                   .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.config.default_max_select"))
                                                   .setLore(
                                                       "&f" + plugin.getLangManager().getText(player, "simplestack.gui.config.default_max_desc_l1"),
                                                       "&f" + plugin.getLangManager().getText(player, "simplestack.gui.config.default_max_desc_l2"),
                                                       "&7" + plugin.getLangManager().getText(player, "simplestack.gui.config.default_max_desc_l3"),
                                                       "&7" + plugin.getLangManager().getText(player, "simplestack.gui.config.default_max_desc_l4")
                                                           ).get());
        GUIMaxStackEvent maxAmountEvent = new GUIMaxStackEvent(plugin);
        defaultMaxAmount.addEvent(maxAmountEvent);
        return defaultMaxAmount;
    }

    /**
     * Get the <tt>GUIItem</tt> for the unique item list button
     *
     * @param player The player (For localization)
     * @return The unique item list button
     */
    private GUIItem getGUIItemUniqueItemList(Player player)
    {
        AnimatedGUIItem uniqueItemList = new AnimatedGUIItem(ItemBuilder.of(Material.BARRIER)
                                                                 .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.unique_items.title"))
                                                                 .setLore(
                                                                     "&f" + plugin.getLangManager().getText(player, "simplestack.gui.config.unique_item_desc_l1"),
                                                                     "&7" + plugin.getLangManager().getText(player, "simplestack.gui.config.unique_item_desc_l2"),
                                                                     "&7" + plugin.getLangManager().getText(player, "simplestack.gui.config.unique_item_desc_l3")
                                                                         ).get(), true);
        final List<ItemStack> uniqueItems = plugin.config().getUniqueItemList();
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
        uniqueItemList.addEvent(new GUIOpenNewEvent(plugin, () -> {
            GUIContainer gui = new GUIContainer(plugin, plugin.getLangManager().getText(player, "simplestack.gui.unique_items.title"), 6);
            GUIBorderModule border = new GUIBorderModule(new GUIItem(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get()));
            gui.addModule(border);
            GUINavigatorModule navi = new GUINavigatorModule(plugin, "config");
            gui.addModule(navi);
            GUIListModule listModule = new GUIListModule(plugin, GUIListModule.ListViewMode.PAGED, 2, 5, 1, 9);
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

    /**
     * Get the <tt>GUIItem</tt> for the item type list button
     *
     * @param player The player (For localization)
     * @return The item type list button
     */
    private GUIItem getGUIItemItemTypeList(Player player)
    {
        AnimatedGUIItem itemTypeList = new AnimatedGUIItem(ItemBuilder.of(Material.BARRIER)
                                                               .setName("&b&l" + plugin.getLangManager().getText(player, "simplestack.gui.item_types.title"))
                                                               .setLore(
                                                                   "&f" + plugin.getLangManager().getText(player, "simplestack.gui.config.item_type_desc_l1"),
                                                                   "&7" + plugin.getLangManager().getText(player, "simplestack.gui.config.item_type_desc_l2"),
                                                                   "&7" + plugin.getLangManager().getText(player, "simplestack.gui.config.item_type_desc_l3"))
                                                               .get(), true);
        final List<Material> materialItems = plugin.config().getMaterialList();
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
        itemTypeList.addEvent(new GUIOpenNewEvent(plugin, () -> {
            GUIContainer gui = new GUIContainer(plugin, plugin.getLangManager().getText(player, "simplestack.gui.item_types.title"), 6);
            GUIBorderModule border = new GUIBorderModule(new GUIItem(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get()));
            gui.addModule(border);
            GUINavigatorModule navi = new GUINavigatorModule(plugin, "config");
            gui.addModule(navi);
            GUIListModule listModule = new GUIListModule(plugin, GUIListModule.ListViewMode.PAGED, 2, 5, 1, 9);
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