package com.mikedeejay2.simplestack.gui.config.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractType;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractHandlerList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractExecutorDefaultInv;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

// TODO: REMOVE
@Deprecated
public class GUIUniqueConstructor extends GUIAbstractListConstructor<Map.Entry<ItemStack, Integer>> {
    private static final Function<Map.Entry<ItemStack, Integer>, GUIItem> MAPPER =
        (entry) -> new GUIItem(entry.getKey()).setMovable(true);
    private static final Function<GUIItem, Map.Entry<ItemStack, Integer>> UNMAPPER =
        (guiItem) -> new AbstractMap.SimpleEntry<>(guiItem.get(), guiItem.getAmount());

    public static final GUIUniqueConstructor INSTANCE = new GUIUniqueConstructor(SimpleStack.getInstance());

    private GUIUniqueConstructor(SimpleStack plugin) {
        super(plugin, Text.of("simplestack.gui.unique_items.title"), 6, null, null);
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
//        return ((SimpleStackConfigImpl) SimpleStackAPI.getConfig()).getUniqueItemsRef();
        return null;
    }
}
