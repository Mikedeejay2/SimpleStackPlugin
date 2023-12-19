package com.mikedeejay2.simplestack.gui.config.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimDecoratorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimOutlineModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.config.ItemConfigValue;
import com.mikedeejay2.simplestack.gui.config.modules.GUIItemConfigModule;
import com.mikedeejay2.simplestack.gui.config.modules.GUIModifiedConfigModule;

public class GUIItemConfigConstructor extends GUIBaseConstructor {
    private final ItemConfigValue configValue;

    public GUIItemConfigConstructor(SimpleStack plugin, ItemConfigValue configValue) {
        super(plugin, Text.of("Item Configuration"), 5);
        this.configValue = configValue;
    }

    @Override
    public GUIContainer get() {
        final GUIContainer gui = super.get();
        GUIAnimationModule animation = new GUIAnimationModule(plugin, 1);
        GUIAnimDecoratorModule outlineModule = new GUIAnimOutlineModule(
            GUIConfigConstructor.ANIMATED_GUI_ITEM, new AnimationSpecification(
                AnimationSpecification.Position.of(1, 5), AnimationSpecification.Style.CIRCULAR));
        GUIListModule listModule = new GUIListModule(plugin, GUIListModule.ListViewMode.SCROLL, 2, 4, 2, 8);
        listModule.addForward(inventoryRows, 6).addForward(inventoryRows, 7).addForward(inventoryRows, 8);
        listModule.addBack(inventoryRows, 4).addBack(inventoryRows, 3).addBack(inventoryRows, 2);
        GUIItemConfigModule configModule = new GUIItemConfigModule(plugin, configValue, listModule);
        gui.addModule(animation);
        gui.addModule(outlineModule);
        gui.addModule(listModule);
        gui.addModule(configModule);
        gui.addModule(GUIModifiedConfigModule.INSTANCE);
        return gui;
    }
}
