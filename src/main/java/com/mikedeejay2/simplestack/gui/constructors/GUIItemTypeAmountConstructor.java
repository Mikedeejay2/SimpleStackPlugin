package com.mikedeejay2.simplestack.gui.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractType;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractHandlerList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractExecutorDefaultInv;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.gui.modules.GUIItemTypeAmountModule;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Function;

public class GUIItemTypeAmountConstructor extends GUIAbstractListConstructor<Pair<Material, Integer>> {
    private static final Function<Pair<Material, Integer>, GUIItem> MAPPER =
        (pair) -> new GUIItem(new ItemStack(pair.getLeft(), pair.getRight())).setMovable(true);
    public static final GUIItemTypeAmountConstructor INSTANCE = new GUIItemTypeAmountConstructor(SimpleStack.getInstance());

    public GUIItemTypeAmountConstructor(SimpleStack plugin) {
        super(plugin, Text.of("simplestack.gui.item_type_amts.title"), 6, MAPPER);
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

        GUIItemTypeAmountModule itemTypeAmountModule = new GUIItemTypeAmountModule(plugin);
        gui.addModule(itemTypeAmountModule);
        return gui;
    }

    @Override
    protected Collection<Pair<Material, Integer>> getUnmappedList() {
        return plugin.config().getItemAmountsSet();
    }
}
