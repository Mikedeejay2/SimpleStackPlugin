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
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Function;

public class GUIUniqueConstructor extends GUIAbstractListConstructor<ItemStack> {
    private static final Function<ItemStack, GUIItem> MAPPER =
        (itemStack) -> new GUIItem(itemStack).setMovable(true);
    private static final Function<GUIItem, ItemStack> UNMAPPER = GUIItem::get;

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
    protected Collection<ItemStack> getUnmappedList() {
        return plugin.config().getUniqueItemList();
    }
}
