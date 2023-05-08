package com.mikedeejay2.simplestack.gui.config.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimDecoratorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimOutlineModule;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.config.ItemConfigValue;
import com.mikedeejay2.simplestack.gui.config.modules.GUIItemConfigModule;

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
        GUIItemConfigModule configModule = new GUIItemConfigModule(plugin, configValue);
        gui.addModule(animation);
        gui.addModule(outlineModule);
        gui.addModule(configModule);
        return gui;
    }
}
