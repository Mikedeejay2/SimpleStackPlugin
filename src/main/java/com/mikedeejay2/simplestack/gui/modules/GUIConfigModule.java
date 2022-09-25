package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.button.GUIButtonEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.button.GUIButtonToggleableEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUICloseEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.PlaceholderFormatter;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.config.ListMode;
import com.mikedeejay2.simplestack.gui.constructors.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.mikedeejay2.mikedeejay2lib.gui.util.SlotMatcher.*;

/**
 * The <tt>GUIModule</tt> for the main configuration GUI screen
 *
 * @author Mikedeejay2
 */
public class GUIConfigModule implements GUIModule {
    private static final ItemBuilder BLACKLIST_ITEM = ItemBuilder.of(Base64Head.X_BLACK.get())
        .setName(Text.of("&b&l").concat("simplestack.list_type.blacklist"))
        .setLore(
            Text.of("&7").concat("simplestack.gui.item_types.change_mode_whitelist"),
            Text.of(""),
            Text.of("&a&l⊳ ").concat("simplestack.list_type.blacklist"),
            Text.of("&7  ").concat("simplestack.list_type.whitelist"));

    private static final ItemBuilder WHITELIST_ITEM = ItemBuilder.of(Base64Head.CHECKMARK_WHITE.get())
        .setName(Text.of("&b&l").concat("simplestack.list_type.whitelist"))
        .setLore(
            Text.of("&7").concat("simplestack.gui.item_types.change_mode_blacklist"),
            Text.of(""),
            Text.of("&7  ").concat("simplestack.list_type.blacklist"),
            Text.of("&a&l⊳ ").concat("simplestack.list_type.whitelist"));

    private static final ItemBuilder CLOSE_ITEM = ItemBuilder.of(Material.REDSTONE)
        .setName(Text.of("&c&o").concat("simplestack.gui.config.close_select"));

    private static final ItemBuilder ITEM_TYPE_AMOUNT_ITEM = ItemBuilder.of(Material.BARRIER)
        .setName(Text.of("&b&l").concat("simplestack.gui.item_type_amts.title"))
        .setLore(Text.of("&f").concat("simplestack.gui.config.item_type_description"));

    private static final ItemBuilder UNIQUE_ITEM_LIST_ITEM = ItemBuilder.of(Material.BARRIER)
        .setName(Text.of("&b&l").concat("simplestack.gui.unique_items.title"))
        .setLore(
            Text.of("&f").concat("simplestack.gui.config.unique_item_desc_l1"),
            Text.of("&7").concat("simplestack.gui.config.unique_item_desc_l2"),
            Text.of("&7").concat("simplestack.gui.config.unique_item_desc_l3"));

    private static final ItemBuilder ITEM_TYPE_ITEM = ItemBuilder.of(Material.BARRIER)
        .setName(Text.of("&b&l").concat("simplestack.gui.item_types.title"))
        .setLore(
            Text.of("&f").concat("simplestack.gui.config.item_type_desc_l1"),
            Text.of("&7").concat("simplestack.gui.config.item_type_desc_l2"),
            Text.of("&7").concat("simplestack.gui.config.item_type_desc_l3"));

    private static final ItemBuilder STACKED_ARMOR_ON = ItemBuilder.of(Base64Head.GREEN.get())
        .setName(Text.of("&b&l").concat("simplestack.gui.config.stackable_armor_select"))
        .setLore(
            Text.of(""),
            Text.of("&a&l⊳ ").concat("generic.enabled"),
            Text.of("&7  ").concat("generic.disabled"));

    private static final ItemBuilder STACKED_ARMOR_OFF = ItemBuilder.of(Base64Head.RED.get())
        .setName(Text.of("&b&l").concat("simplestack.gui.config.stackable_armor_select"))
        .setLore(
            Text.of(""),
            Text.of("&7  ").concat("generic.enabled"),
            Text.of("&c&l⊳ ").concat("generic.disabled"));

    private final SimpleStack plugin;
    protected final int LIST_ANIM_AMOUNT = 8;

    public GUIConfigModule(SimpleStack plugin) {
        this.plugin = plugin;
    }

    /**
     * Overridden <tt>onOpenHead</tt> method that generates the configuration screen
     * when the GUI is opened
     *
     * @param player The player that opened the GUI
     * @param gui    The <tt>GUIContainer</tt> that the config GUI is contained in
     */
    @Override
    public void onOpenHead(Player player, GUIContainer gui) {
        GUILayer layer = gui.getLayer(0);

        GUIItem itemTypeList = getGUIItemItemTypeList();
        GUIItem itemTypeAmountList = getGUIItemItemTypeAmountList();
        GUIItem uniqueItemList = getGUIItemUniqueItemList();
        GUIItem language = getGUIItemLanguage();
        GUIItem defaultMaxAmount = getGUIItemDefaultMaxAmount();
        GUIItem switchListMode = getGUIItemSwitchListMode();
        GUIItem stackableArmor = getGUIItemStackedArmor();

        layer.setItem(2, 4, itemTypeList);
        layer.setItem(2, 5, itemTypeAmountList);
        layer.setItem(2, 6, uniqueItemList);
        layer.setItem(3, 4, language);
        layer.setItem(3, 5, defaultMaxAmount);
        layer.setItem(3, 6, stackableArmor);
        layer.setItem(4, 5, switchListMode);

        GUIItem closeItem = getGUIItemCloseItem();
        layer.setItem(5, 5, closeItem);
    }

    /**
     * Get the <tt>GUIItem</tt> for the "Switch List Mode" button
     *
     * @return The switch list mode item
     */
    private GUIItem getGUIItemSwitchListMode() {
        GUIItem switchListMode = new GUIItem(plugin.config().getListMode() == ListMode.WHITELIST ? WHITELIST_ITEM : BLACKLIST_ITEM);
        GUIButtonToggleableEvent button = new GUIButtonToggleableEvent(
            (info) -> plugin.config().setListMode(ListMode.WHITELIST),
            (info) -> plugin.config().setListMode(ListMode.BLACKLIST),
            plugin.config().getListMode() == ListMode.WHITELIST)
            .setOnItem(WHITELIST_ITEM)
            .setOffItem(BLACKLIST_ITEM);
        switchListMode.addEvent(button);
        return switchListMode;
    }

    /**
     * Get the <tt>GUIItem</tt> for the close button
     *
     * @return The close button
     */
    private GUIItem getGUIItemCloseItem() {
        GUIItem closeItem = new GUIItem(CLOSE_ITEM);
        closeItem.addEvent(new GUICloseEvent(plugin));
        return closeItem;
    }

    /**
     * Get the <tt>GUIItem</tt> for the item type amount list button
     *
     * @return The item type amount list button
     */
    private GUIItem getGUIItemItemTypeAmountList() {
        AnimatedGUIItem itemTypeAmountList = new AnimatedGUIItem(ITEM_TYPE_AMOUNT_ITEM, true);
        final Map<Material, Integer> itemAmounts = plugin.config().getItemAmounts();
        Iterator<Map.Entry<Material, Integer>> iter2 = itemAmounts.entrySet().iterator();
        for(int i = 0; i < Math.min(LIST_ANIM_AMOUNT, itemAmounts.size()); ++i) {
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
        itemTypeAmountList.addEvent(new GUIOpenNewEvent(plugin, GUIItemTypeAmountConstructor.INSTANCE));
        return itemTypeAmountList;
    }

    /**
     * Get the <tt>GUIItem</tt> for the language select button
     *
     * @return The language button
     */
    private GUIItem getGUIItemLanguage() {
        GUIItem language = new GUIItem(
            ItemBuilder.of(Base64Head.GLOBE.get())
                .setName(Text.of("&b&l").concat("simplestack.gui.config.language_select"))
                .setLore(
                    Text.of("&f").concat("simplestack.gui.config.language_description"),
                    Text.of("&7").concat(
                        Text.of("simplestack.gui.config.language_selected")
                            .placeholder(PlaceholderFormatter.of("lang", plugin.config().getLangLocale())))));

        language.addEvent(new GUIOpenNewEvent(plugin, GUILanguageConstructor.INSTANCE));
        return language;
    }

    /**
     * Get the <tt>GUIItem</tt> for the default max amount button
     *
     * @return The default max amount button
     */
    private GUIItem getGUIItemDefaultMaxAmount() {
        GUIItem defaultMaxAmount = new GUIItem(
            ItemBuilder.of(Material.BOOK)
                .setAmount(plugin.config().getMaxAmount())
                .setName(Text.of("&b&l").concat("simplestack.gui.config.default_max_select"))
                .setLore(
                    Text.of("&f").concat("simplestack.gui.config.default_max_desc_l1"),
                    Text.of("&f").concat("simplestack.gui.config.default_max_desc_l2"),
                    Text.of("&7").concat("simplestack.gui.config.default_max_desc_l3"),
                    Text.of("&7").concat("simplestack.gui.config.default_max_desc_l4")
                ));

        GUIButtonEvent button = new GUIButtonEvent((info) -> {
            final GUIItem item = info.getGUIItem();
            if(info.isShiftClick()) {
                if(info.isLeftClick()) {
                    item.setAmount(1);
                } else if(info.isLeftClick()) {
                    item.setAmount(64);
                }
            } else if(info.isLeftClick()) {
                item.setAmount(item.getAmount() > 1 ? item.getAmount() - 1 : 1);
            } else if(info.isRightClick()) {
                item.setAmount(item.getAmount() < 64 ? item.getAmount() + 1 : 64);
            }
            int amount = item.getAmount();
            plugin.config().setMaxAmount(amount);
        });
        defaultMaxAmount.addEvent(button);
        return defaultMaxAmount;
    }

    /**
     * Get the <tt>GUIItem</tt> for the unique item list button
     *
     * @return The unique item list button
     */
    private GUIItem getGUIItemUniqueItemList() {
        AnimatedGUIItem uniqueItemList = new AnimatedGUIItem(UNIQUE_ITEM_LIST_ITEM, true);
        final List<ItemStack> uniqueItems = plugin.config().getUniqueItemList();
        for(int i = 0; i < Math.min(LIST_ANIM_AMOUNT, uniqueItems.size()); ++i) {
            ItemStack item = uniqueItems.get(i);
            ItemStack newItem = item.clone();
            ItemMeta itemMeta = newItem.getItemMeta();
            itemMeta.setDisplayName(uniqueItemList.getName());
            itemMeta.setLore(uniqueItemList.getLore());
            newItem.setItemMeta(itemMeta);
            uniqueItemList.addFrame(newItem, 20);
        }
        uniqueItemList.addEvent(new GUIOpenNewEvent(plugin, GUIUniqueConstructor.INSTANCE));
        return uniqueItemList;
    }

    /**
     * Get the <tt>GUIItem</tt> for the item type list button
     *
     * @return The item type list button
     */
    private GUIItem getGUIItemItemTypeList() {
        AnimatedGUIItem itemTypeList = new AnimatedGUIItem(ITEM_TYPE_ITEM, true);
        final List<Material> materialItems = plugin.config().getMaterialList();
        for(int i = 0; i < Math.min(LIST_ANIM_AMOUNT, materialItems.size()); ++i) {
            Material material = materialItems.get(i);
            ItemStack item = new ItemStack(material);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(itemTypeList.getName());
            itemMeta.setLore(itemTypeList.getLore());
            item.setItemMeta(itemMeta);
            itemTypeList.addFrame(item, 20);
        }
        itemTypeList.addEvent(new GUIOpenNewEvent(plugin, GUIItemTypeConstructor.INSTANCE));
        return itemTypeList;
    }

    /**
     * Get the <tt>GUIItem</tt> for the "Stacked Armor Wearable" button
     *
     * @return The creative drag mode item
     */
    private GUIItem getGUIItemStackedArmor()
    {
        boolean buttonState = plugin.config().isStackedArmorWearable();
        final GUIItem guiItem = new GUIItem(buttonState ? STACKED_ARMOR_ON : STACKED_ARMOR_OFF);
        guiItem.addEvent(new GUIButtonToggleableEvent(info -> {
            plugin.config().setStackedArmorWearable(true);
        }, info -> {
            plugin.config().setStackedArmorWearable(false);
        }, buttonState).setOnItem(STACKED_ARMOR_ON).setOffItem(STACKED_ARMOR_OFF));

        return guiItem;
    }
}
