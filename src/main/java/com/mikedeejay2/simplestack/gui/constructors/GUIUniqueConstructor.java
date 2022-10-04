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
import com.mikedeejay2.simplestack.gui.modules.GUIUniqueItemListModule;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Function;

public class GUIUniqueConstructor extends GUIAbstractListConstructor<ItemStack> {
    private static final Function<ItemStack, GUIItem> MAPPER =
        (itemStack) -> new GUIItem(itemStack).setMovable(true);
    public static final GUIUniqueConstructor INSTANCE = new GUIUniqueConstructor(SimpleStack.getInstance());

    public GUIUniqueConstructor(SimpleStack plugin) {
        super(plugin, Text.of("simplestack.gui.unique_items.title"), 6, MAPPER);
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

        GUIUniqueItemListModule uniqueItemModule = new GUIUniqueItemListModule(plugin);
        gui.addModule(uniqueItemModule);
        return gui;
    }

    @Override
    protected Collection<ItemStack> getUnmappedList() {
        return plugin.config().getUniqueItemList();
    }
}
