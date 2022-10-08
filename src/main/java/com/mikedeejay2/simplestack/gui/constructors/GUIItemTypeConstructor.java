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
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

public class GUIItemTypeConstructor extends GUIAbstractListConstructor<Material> {
    private static final Function<Material, GUIItem> MAPPER =
        (material) -> new GUIItem(new ItemStack(material)).setMovable(true);
    private static final Function<GUIItem, Material> UNMAPPER = GUIItem::getType;
    public static final GUIItemTypeConstructor INSTANCE = new GUIItemTypeConstructor(SimpleStack.getInstance());

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

        return gui;
    }

    @Override
    protected List<Material> getUnmappedList() {
        return ((SimpleStackConfigImpl) SimpleStackAPI.getConfig()).getMaterialsRef();
    }
}
