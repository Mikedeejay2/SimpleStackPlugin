package com.mikedeejay2.simplestack.gui.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractType;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractHandlerList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractExecutorDefaultInv;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIMappedListModule.MappingFunction;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIMappedListModule.UnmappingFunction;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;

public class GUIItemTypeAmountConstructor extends GUIAbstractListConstructor<Map.Entry<Material, Integer>> {
    private static final MappingFunction<Map.Entry<Material, Integer>> MAPPER =
        (pair, index, module) -> new GUIItem(new ItemStack(pair.getKey(), pair.getValue())).setMovable(true);
    private static final UnmappingFunction<Map.Entry<Material, Integer>> UNMAPPER =
        (item, index, module) -> new AbstractMap.SimpleEntry<>(item.getType(), item.getAmount());

    public static final GUIItemTypeAmountConstructor INSTANCE = new GUIItemTypeAmountConstructor(SimpleStack.getInstance());

    private GUIItemTypeAmountConstructor(SimpleStack plugin) {
        super(plugin, Text.of("simplestack.gui.item_type_amts.title"), 6, MAPPER, UNMAPPER);
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = super.get();
        GUIInteractHandler interaction = new GUIInteractHandlerList(64);
        interaction.resetExecutors();
        interaction.addExecutor(new GUIInteractExecutorDefaultInv(64));
        interaction.addExecutor(new GUIInteractExecutorList(GUIInteractType.SINGLE_MATERIAL, 64, false));
        gui.setDefaultMoveState(true);
        gui.setInteractionHandler(interaction);
        return gui;
    }

    @Override
    protected List<Map.Entry<Material, Integer>> getUnmappedList() {
        return ((SimpleStackConfigImpl) SimpleStackAPI.getConfig()).getItemAmountsRef();
    }
}
