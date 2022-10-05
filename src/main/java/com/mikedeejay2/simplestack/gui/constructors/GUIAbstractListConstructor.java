package com.mikedeejay2.simplestack.gui.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIConstructor;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractType;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractHandlerList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractExecutorDefaultInv;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIDecoratorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIMappedListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.util.GUIRuntimeModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.gui.modules.GUIModifiedConfigModule;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Function;

import static com.mikedeejay2.mikedeejay2lib.gui.util.SlotMatcher.inRange;

public abstract class GUIAbstractListConstructor<T> implements GUIConstructor {
    protected final SimpleStack plugin;
    protected final Text title;
    protected final int inventoryRows;
    protected final Function<T, GUIItem> mapFunction;
    protected final Function<GUIItem, T> unmapFunction;

    public GUIAbstractListConstructor(
        SimpleStack plugin,
        Text title,
        int inventoryRows,
        Function<T, GUIItem> mapFunction,
        Function<GUIItem, T> unmapFunction) {
        this.plugin = plugin;
        this.title = title;
        this.inventoryRows = inventoryRows;
        this.mapFunction = mapFunction;
        this.unmapFunction = unmapFunction;
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = new GUIContainer(plugin, title, inventoryRows);
        GUIDecoratorModule border = new GUIDecoratorModule(
            inRange(1, 1, 1, 9).or(inRange(inventoryRows, 1, inventoryRows, 9)),
            new GUIItem(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get()));
        gui.addModule(border);
        GUINavigatorModule navi = new GUINavigatorModule(plugin, "config");
        gui.addModule(navi);
        GUIMappedListModule<T> listModule = new GUIMappedListModule<>(
            plugin, GUIListModule.ListViewMode.PAGED,
            getUnmappedList(), mapFunction,
            2, inventoryRows - 1, 1, 9);
        listModule.setUnmapFunction(unmapFunction);
        GUIItem padItem = new GUIItem((ItemStack) null);
        padItem.setMovable(true);
        listModule.addEndItem(padItem);
        listModule.addForward(inventoryRows, 6).addForward(inventoryRows, 7).addForward(inventoryRows, 8);
        listModule.addBack(inventoryRows, 4).addBack(inventoryRows, 3).addBack(inventoryRows, 2);
        gui.addModule(listModule);
        GUIRuntimeModule listRuntime = new GUIRuntimeModule(plugin, 0, 10, (info) -> {
            if(listModule.hasChanged()) info.getGui().update(info.getPlayer());
        });
        gui.addModule(listRuntime);
        GUIModifiedConfigModule modifiedConfigModule = GUIModifiedConfigModule.INSTANCE;
        gui.addModule(modifiedConfigModule);
        return gui;
    }

    protected abstract Collection<T> getUnmappedList();
}
