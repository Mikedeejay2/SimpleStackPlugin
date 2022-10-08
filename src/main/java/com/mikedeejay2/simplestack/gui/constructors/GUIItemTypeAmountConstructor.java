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

import java.util.*;
import java.util.function.Function;

import static com.mikedeejay2.simplestack.config.SimpleStackConfigImpl.*;

public class GUIItemTypeAmountConstructor extends GUIAbstractListConstructor<MaterialAndAmount> {
    private static final Function<MaterialAndAmount, GUIItem> MAPPER =
        (pair) -> new GUIItem(new ItemStack(pair.getMaterial(), pair.getAmount())).setMovable(true);
    private static final Function<GUIItem, MaterialAndAmount> UNMAPPER =
        (item) -> new MaterialAndAmount(item.getType(), item.getAmount());

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
    protected List<MaterialAndAmount> getUnmappedList() {
        return plugin.config().getItemAmountsRef();
    }
}
