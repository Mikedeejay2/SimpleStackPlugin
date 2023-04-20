package com.mikedeejay2.simplestack.gui.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.event.button.GUIButtonToggleableEvent;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractType;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractHandlerList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractExecutorDefaultInv;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.SimpleStackConfig;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

public class GUIItemTypeConstructor extends GUIAbstractListConstructor<Material> {
    private static final Function<Material, GUIItem> MAPPER =
        (material) -> new GUIItem(new ItemStack(material)).setMovable(true);
    private static final Function<GUIItem, Material> UNMAPPER = GUIItem::getType;
    public static final GUIItemTypeConstructor INSTANCE = new GUIItemTypeConstructor(SimpleStack.getInstance());

    private static final ItemBuilder BLACKLIST_ITEM = ItemBuilder.of(Base64Head.X_WHITE.get())
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


    private GUIItemTypeConstructor(SimpleStack plugin) {
        super(plugin, Text.of("simplestack.gui.item_types.title"), 6, MAPPER, UNMAPPER);
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = super.get();
        GUIInteractHandler interaction = new GUIInteractHandlerList(64);
        interaction.resetExecutors();
        interaction.addExecutor(new GUIInteractExecutorDefaultInv(64));
        interaction.addExecutor(new GUIInteractExecutorList(GUIInteractType.SINGLE_MATERIAL, 1, false));
        gui.setDefaultMoveState(true);
        gui.setInteractionHandler(interaction);

        GUIItem switchListMode = getGUIItemSwitchListMode();
        gui.getLayer("Switch List Layer").setItem(1, 2, switchListMode);

        return gui;
    }

    /**
     * Get the <code>GUIItem</code> for the "Switch List Mode" button
     *
     * @return The switch list mode item
     */
    private GUIItem getGUIItemSwitchListMode() {
        SimpleStackConfig config = SimpleStackAPI.getConfig();
        GUIItem switchListMode = new GUIItem(config.isWhitelist() ? WHITELIST_ITEM : BLACKLIST_ITEM);
        GUIButtonToggleableEvent button = new GUIButtonToggleableEvent(
            (info) -> config.setListMode(true),
            (info) -> config.setListMode(false),
            config.isWhitelist())
            .setOnItem(WHITELIST_ITEM).setOffItem(BLACKLIST_ITEM);
        button.setSound(Sound.UI_BUTTON_CLICK).setVolume(0.3f);
        switchListMode.addEvent(button);
        return switchListMode;
    }

    @Override
    protected List<Material> getUnmappedList() {
        return ((SimpleStackConfigImpl) SimpleStackAPI.getConfig()).getMaterialsRef();
    }
}
