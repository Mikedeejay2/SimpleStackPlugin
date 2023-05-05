package com.mikedeejay2.simplestack.gui.config.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIConstructor;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;

public abstract class GUIBaseConstructor implements GUIConstructor {
    protected final SimpleStack plugin;
    protected final Text title;
    protected final int inventoryRows;

    public GUIBaseConstructor(SimpleStack plugin, Text title, int inventoryRows) {
        this.plugin = plugin;
        this.title = title;
        this.inventoryRows = inventoryRows;
    }

    @Override
    public GUIContainer get() {
        final GUIContainer gui = new GUIContainer(plugin, title, inventoryRows);
        GUINavigatorModule navi = new GUINavigatorModule(plugin, "config");
        gui.addModule(navi);
        return gui;
    }
}
