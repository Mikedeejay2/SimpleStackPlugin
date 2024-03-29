package com.mikedeejay2.simplestack.gui.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractType;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractHandlerList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractExecutorDefaultInv;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIMappedListModule.MappingFunction;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIMappedListModule.UnmappingFunction;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class GUIUniqueConstructor extends GUIAbstractListConstructor<Map.Entry<ItemStack, Integer>> {
    private static final MappingFunction<Map.Entry<ItemStack, Integer>> MAPPER =
        (entry, index, module) -> new GUIItem(entry.getKey()).setMovable(true);
    private static final UnmappingFunction<Map.Entry<ItemStack, Integer>> UNMAPPER =
        (guiItem, index, module) -> new AbstractMap.SimpleEntry<>(guiItem.get(), guiItem.getAmount());

    public static final GUIUniqueConstructor INSTANCE = new GUIUniqueConstructor(SimpleStack.getInstance());

    private GUIUniqueConstructor(SimpleStack plugin) {
        super(plugin, Text.of("simplestack.gui.unique_items.title"), 6, MAPPER, UNMAPPER);
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = super.get();
        GUIInteractHandler interaction = new GUIInteractHandlerList(64);
        interaction.resetExecutors();
        interaction.addExecutor(new GUIInteractExecutorDefaultInv(64));
        interaction.addExecutor(new GUIInteractExecutorList(GUIInteractType.SINGLE_ITEM, 64, false));
        gui.setDefaultMoveState(true);
        gui.setInteractionHandler(interaction);
        return gui;
    }

    @Override
    protected List<Map.Entry<ItemStack, Integer>> getUnmappedList() {
        return ((SimpleStackConfigImpl) SimpleStackAPI.getConfig()).getUniqueItemsRef();
    }
}
