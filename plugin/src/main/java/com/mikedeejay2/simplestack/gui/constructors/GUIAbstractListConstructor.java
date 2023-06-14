package com.mikedeejay2.simplestack.gui.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIConstructor;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIDecoratorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIMappedListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIMappedListModule.MappingFunction;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIMappedListModule.UnmappingFunction;
import com.mikedeejay2.mikedeejay2lib.gui.modules.util.GUIRuntimeModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.gui.modules.GUIModifiedConfigModule;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

import static com.mikedeejay2.mikedeejay2lib.gui.util.SlotMatcher.inRange;

public abstract class GUIAbstractListConstructor<T> implements GUIConstructor {
    protected final SimpleStack plugin;
    protected final Text title;
    protected final int inventoryRows;
    protected final MappingFunction<T> mapFunction;
    protected final UnmappingFunction<T> unmapFunction;

    public GUIAbstractListConstructor(
        SimpleStack plugin,
        Text title,
        int inventoryRows,
        MappingFunction<T> mapFunction,
        UnmappingFunction<T> unmapFunction) {
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

    protected abstract List<T> getUnmappedList();
}
