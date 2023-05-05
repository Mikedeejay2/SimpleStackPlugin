package com.mikedeejay2.simplestack.gui.config.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.config.ItemConfigValue;

public class GUIItemConfigConstructor extends GUIBaseConstructor {
    private final ItemConfigValue configValue;

    public GUIItemConfigConstructor(SimpleStack plugin, ItemConfigValue configValue) {
        super(plugin, Text.of("Item Configuration"), 6);
        this.configValue = configValue;
    }

    @Override
    public GUIContainer get() {
        final GUIContainer gui = super.get();

        return gui;
    }
}
