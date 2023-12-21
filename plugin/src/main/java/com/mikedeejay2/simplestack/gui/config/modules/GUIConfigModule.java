package com.mikedeejay2.simplestack.gui.config.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.button.GUIButtonEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.button.GUIButtonToggleableEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUICloseEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.sound.GUIPlaySoundEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.PlaceholderFormatter;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.SimpleStackConfig;
import com.mikedeejay2.simplestack.gui.config.constructors.GUIItemListConstructor;
import com.mikedeejay2.simplestack.gui.config.constructors.GUILanguageConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;

/**
 * The <code>GUIModule</code> for the main configuration GUI screen
 *
 * @author Mikedeejay2
 */
public class GUIConfigModule implements GUIModule {
    private static final ItemBuilder CLOSE_ITEM = ItemBuilder.of(Material.REDSTONE)
        .setName(Text.of("&c&o").concat("simplestack.gui.config.close_select"));

    private static final ItemBuilder ITEM_LIST = ItemBuilder.of(Material.MOJANG_BANNER_PATTERN)
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

    private static final ItemFlag[] PREVIEW_ITEM_FLAGS = new ItemFlag[] {
        ItemFlag.HIDE_POTION_EFFECTS,
        ItemFlag.HIDE_ATTRIBUTES,
        ItemFlag.HIDE_UNBREAKABLE,
        ItemFlag.HIDE_ENCHANTS,
        ItemFlag.HIDE_DESTROYS,
        ItemFlag.HIDE_PLACED_ON
    };

    private static final int[] SNAP_MAX_STACK_AMOUNTS = new int[] {1, 8, 16, 32, 48, 64};

    private static final Text[] LORE_MAX_STACK_ON = new Text[] {
        Text.of("&f").concat("simplestack.gui.config.override_max_stack_size.desc_l1"),
        Text.of("&f").concat("simplestack.gui.config.override_max_stack_size.desc_l2"),
        Text.of("&7").concat("simplestack.gui.config.override_max_stack_size.desc_on_l1"),
        Text.of("&7").concat("simplestack.gui.config.override_max_stack_size.desc_on_l2"),
        Text.of("&7").concat("simplestack.gui.config.override_max_stack_size.desc_on_l3"),
        Text.of("&7").concat("simplestack.gui.config.override_max_stack_size.desc_on_l4")
    };

    private static final Text[] LORE_MAX_STACK_OFF = new Text[] {
        Text.of("&f").concat("simplestack.gui.config.override_max_stack_size.desc_l1"),
        Text.of("&f").concat("simplestack.gui.config.override_max_stack_size.desc_l2"),
        Text.of("&7").concat("simplestack.gui.config.override_max_stack_size.desc_off_l1")
    };

    private final SimpleStack plugin;
    protected final int LIST_ANIM_AMOUNT = 8;

    public GUIConfigModule(SimpleStack plugin) {
        this.plugin = plugin;
    }

    /**
     * Overridden <code>onOpenHead</code> method that generates the configuration screen
     * when the GUI is opened
     *
     * @param player The player that opened the GUI
     * @param gui    The <code>GUIContainer</code> that the config GUI is contained in
     */
    @Override
    public void onOpenHead(Player player, GUIContainer gui) {
        GUILayer layer = gui.getLayer(0);

        GUIItem itemList = getGUIItemList();
        GUIItem language = getGUIItemLanguage();
        GUIItem defaultMaxAmount = getGUIItemDefaultMaxAmount();
        GUIItem stackableArmor = getGUIItemStackedArmor();

        layer.setItem(2, 5, itemList);
        layer.setItem(3, 4, language);
        layer.setItem(3, 5, defaultMaxAmount);
        layer.setItem(3, 6, stackableArmor);

        GUIItem closeItem = getGUIItemCloseItem();
        layer.setItem(4, 5, closeItem);
    }

    /**
     * Get the <code>GUIItem</code> for the close button
     *
     * @return The close button
     */
    private GUIItem getGUIItemCloseItem() {
        GUIItem closeItem = new GUIItem(CLOSE_ITEM);
        closeItem.addEvent(new GUICloseEvent(plugin));
        closeItem.addEvent(new GUIPlaySoundEvent(Sound.UI_BUTTON_CLICK, 0.3f, 1f));
        return closeItem;
    }

//    /**
//     * Get the <code>GUIItem</code> for the item type amount list button
//     *
//     * @return The item type amount list button
//     */
//    private GUIItem getGUIItemItemTypeAmountList() {
//        AnimatedGUIItem itemTypeAmountList = new AnimatedGUIItem(ITEM_TYPE_AMOUNT_ITEM, true);
//        final Map<Material, Integer> itemAmounts = SimpleStackAPI.getConfig().getItemAmounts();
//        final Iterator<Map.Entry<Material, Integer>> iterator = itemAmounts.entrySet().iterator();
//        for(int i = 0; i < Math.min(LIST_ANIM_AMOUNT, itemAmounts.size()); ++i) {
//            Map.Entry<Material, Integer> entry = iterator.next();
//            Material material = entry.getKey();
//            if(material == null) continue;
//            itemTypeAmountList.addFrame(
//                ItemBuilder.of(ITEM_TYPE_AMOUNT_ITEM)
//                    .setType(material)
//                    .addItemFlags(PREVIEW_ITEM_FLAGS),
//                20);
//        }
//        itemTypeAmountList.addEvent(new GUIOpenNewEvent(plugin, GUIItemTypeAmountConstructor.INSTANCE));
//        itemTypeAmountList.addEvent(new GUIPlaySoundEvent(Sound.UI_BUTTON_CLICK, 0.3f, 1f));
//        return itemTypeAmountList;
//    }

    /**
     * Get the <code>GUIItem</code> for the language select button
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
                            .placeholder(PlaceholderFormatter.of("lang", SimpleStackAPI.getConfig().getLocale())))));

        language.addEvent(new GUIOpenNewEvent(plugin, GUILanguageConstructor.INSTANCE));
        language.addEvent(new GUIPlaySoundEvent(Sound.UI_BUTTON_CLICK, 0.3f, 1f));
        return language;
    }

    /**
     * Get the <code>GUIItem</code> for the default max amount button
     *
     * @return The default max amount button
     */
    private GUIItem getGUIItemDefaultMaxAmount() {
        final SimpleStackConfig config = SimpleStackAPI.getConfig();
        GUIItem defaultMaxAmount = new GUIItem(
            ItemBuilder.of(config.overrideDefaultStackSizes() ? Material.KNOWLEDGE_BOOK : Material.BOOK)
                .setAmount(config.overrideDefaultStackSizes() ? config.getMaxStackOverride() : 1)
                .setName(Text.of("&b&l").concat("simplestack.gui.config.override_max_stack_size"))
                .setLore(config.overrideDefaultStackSizes() ? LORE_MAX_STACK_ON : LORE_MAX_STACK_OFF));

        GUIButtonEvent button = new GUIButtonEvent((info) -> {
            final GUIItem item = info.getGUIItem();
            if(!config.overrideDefaultStackSizes()) {
                if(!info.isLeftClick()) return;
                item.setType(Material.KNOWLEDGE_BOOK)
                    .setAmount(config.getMaxStackOverride())
                    .setLore(LORE_MAX_STACK_ON);
                config.setOverrideDefaultStackSizes(true);
                return;
            }
            if(info.isShiftClick() && info.isLeftClick()) {
                item.setType(Material.BOOK)
                    .setAmount(1)
                    .setLore(LORE_MAX_STACK_OFF);
                config.setOverrideDefaultStackSizes(false);
                return;
            }
            if(info.isShiftClick() && info.isRightClick()) {
                if(item.getAmount() == 64) item.setAmount(1);
                else for(int i : SNAP_MAX_STACK_AMOUNTS) {
                    if(i <= item.getAmount()) continue;
                    item.setAmount(i);
                    break;
                }
            } else if(info.isLeftClick()) {
                item.setAmount(item.getAmount() > 1 ? item.getAmount() - 1 : 1);
            } else if(info.isRightClick()) {
                item.setAmount(item.getAmount() < 64 ? item.getAmount() + 1 : 64);
            }
            int amount = item.getAmount();
            SimpleStackAPI.getConfig().setMaxStackOverride(amount);
        });
        button.setSound(Sound.UI_BUTTON_CLICK).setVolume(0.3f);
        defaultMaxAmount.addEvent(button);
        return defaultMaxAmount;
    }

//    /**
//     * Get the <code>GUIItem</code> for the unique item list button
//     *
//     * @return The unique item list button
//     */
//    private GUIItem getGUIItemUniqueItemList() {
//        AnimatedGUIItem uniqueItemList = new AnimatedGUIItem(UNIQUE_ITEM_LIST_ITEM, true);
//        final Set<ItemStack> uniqueItems = SimpleStackAPI.getConfig().getUniqueItems();
//        final Iterator<ItemStack> iterator = uniqueItems.iterator();
//        for(int i = 0; i < Math.min(LIST_ANIM_AMOUNT, uniqueItems.size()); ++i) {
//            uniqueItemList.addFrame(
//                ItemBuilder.of(UNIQUE_ITEM_LIST_ITEM)
//                    .setType(iterator.next().getType())
//                    .addItemFlags(PREVIEW_ITEM_FLAGS),
//                20);
//        }
//        uniqueItemList.addEvent(new GUIOpenNewEvent(plugin, GUIUniqueConstructor.INSTANCE));
//        uniqueItemList.addEvent(new GUIPlaySoundEvent(Sound.UI_BUTTON_CLICK, 0.3f, 1f));
//        return uniqueItemList;
//    }

//    /**
//     * Get the <code>GUIItem</code> for the item type list button
//     *
//     * @return The item type list button
//     */
//    private GUIItem getGUIItemItemTypeList() {
//        AnimatedGUIItem itemTypeList = new AnimatedGUIItem(ITEM_TYPE_ITEM, true);
//        final Set<Material> materialItems = SimpleStackAPI.getConfig().getMaterials();
//        final Iterator<Material> iterator = materialItems.iterator();
//        for(int i = 0; i < Math.min(LIST_ANIM_AMOUNT, materialItems.size()); ++i) {
//            itemTypeList.addFrame(
//                ItemBuilder.of(ITEM_TYPE_ITEM)
//                    .setType(iterator.next())
//                    .addItemFlags(PREVIEW_ITEM_FLAGS),
//                20);
//        }
//        itemTypeList.addEvent(new GUIOpenNewEvent(plugin, GUIItemTypeConstructor.INSTANCE));
//        itemTypeList.addEvent(new GUIPlaySoundEvent(Sound.UI_BUTTON_CLICK, 0.3f, 1f));
//        return itemTypeList;
//    }

    private GUIItem getGUIItemList() {
        AnimatedGUIItem itemTypeList = new AnimatedGUIItem(ITEM_LIST, true);
        // TODO: THIS
//        final Set<Material> materialItems = SimpleStackAPI.getConfig().getMaterials();
//        final Iterator<Material> iterator = materialItems.iterator();
//        for(int i = 0; i < Math.min(LIST_ANIM_AMOUNT, materialItems.size()); ++i) {
//            itemTypeList.addFrame(
//                ItemBuilder.of(ITEM_TYPE_ITEM)
//                    .setType(iterator.next())
//                    .addItemFlags(PREVIEW_ITEM_FLAGS),
//                20);
//        }
        itemTypeList.addEvent(new GUIOpenNewEvent(plugin, GUIItemListConstructor.INSTANCE));
        itemTypeList.addEvent(new GUIPlaySoundEvent(Sound.UI_BUTTON_CLICK, 0.3f, 1f));
        return itemTypeList;
    }

    /**
     * Get the <code>GUIItem</code> for the "Stacked Armor Wearable" button
     *
     * @return The creative drag mode item
     */
    private GUIItem getGUIItemStackedArmor() {
        boolean buttonState = SimpleStackAPI.getConfig().isStackedArmorWearable();
        final GUIItem guiItem = new GUIItem(
            buttonState ? STACKED_ARMOR_ON : STACKED_ARMOR_OFF);
        guiItem.addEvent(
            new GUIButtonToggleableEvent(
                info -> SimpleStackAPI.getConfig().setStackedArmorWearable(true),
                info -> SimpleStackAPI.getConfig().setStackedArmorWearable(false), buttonState)
                .setOnItem(STACKED_ARMOR_ON).setOffItem(STACKED_ARMOR_OFF)
                .setSound(Sound.UI_BUTTON_CLICK).setVolume(0.3f));

        return guiItem;
    }
}
