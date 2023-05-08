package com.mikedeejay2.simplestack.gui.config.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIClickEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.sound.GUIPlaySoundEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.util.GUIAbstractClickEvent;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractType;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractHandlerList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractExecutorDefaultInv;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIMappedListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIMappedListModule.MappingFunction;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIMappedListModule.UnmappingFunction;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.config.ItemConfigValue;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class GUIItemListConstructor extends GUIAbstractListConstructor<ItemConfigValue> {
    private static final SimpleStack plugin = SimpleStack.getInstance();

    private static final MappingFunction<ItemConfigValue> MAPPER = (value, i, module) ->
        new GUIItem(value.asItemBuilder()).addExtraData(ItemConfigValue.DATA_KEY, value)
            .addEvent(new GUIOpenNewEvent(plugin, new GUIItemConfigConstructor(plugin, value), ClickType.LEFT))
            .addEvent(new GUIOpenNewEvent(plugin, new GUIItemRemoveConstructor(plugin, value), ClickType.RIGHT))
            .addEvent(new GUIPlaySoundEvent(Sound.UI_BUTTON_CLICK, 0.3f, 1f, ClickType.LEFT, ClickType.RIGHT));
    private static final UnmappingFunction<ItemConfigValue> UNMAPPER = (item, i, module) ->
        item.containsExtraData(ItemConfigValue.DATA_KEY) ?
        item.getExtraData(ItemConfigValue.DATA_KEY, ItemConfigValue.class) :
        new ItemConfigValue(item.get());

    public static final GUIItemListConstructor INSTANCE = new GUIItemListConstructor(SimpleStack.getInstance());

    private GUIItemListConstructor(SimpleStack plugin) {
        super(plugin, Text.of("All Items"), 6, MAPPER, UNMAPPER);
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = super.get();
        GUIInteractHandler interaction = new GUIInteractHandlerList(64);
        interaction.resetExecutors();
        interaction.addExecutor(new GUIInteractExecutorDefaultInv(64));
        interaction.addExecutor(new GUIInteractExecutorList(GUIInteractType.DEFAULT, 64, false));
        gui.setDefaultMoveState(true);
        gui.setInteractionHandler(interaction);
        return gui;
    }

    @Override
    protected List<ItemConfigValue> getUnmappedList() {
        return ((SimpleStackConfigImpl) SimpleStackAPI.getConfig()).getItemsRef();
    }
}
