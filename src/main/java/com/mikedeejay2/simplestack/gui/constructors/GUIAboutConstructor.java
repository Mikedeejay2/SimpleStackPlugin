package com.mikedeejay2.simplestack.gui.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIConstructor;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.gui.modules.GUIAboutModule;

public class GUIAboutConstructor implements GUIConstructor {
    public static final GUIAboutConstructor INSTANCE = new GUIAboutConstructor(SimpleStack.getInstance());

    private final SimpleStack plugin;

    private GUIAboutConstructor(SimpleStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = new GUIContainer(plugin, Text.of("simplestack.gui.config.about_select"), 6);
        GUIAnimationModule animModule = new GUIAnimationModule(plugin, 1);
        gui.addModule(animModule);
        GUIAboutModule aboutModule = new GUIAboutModule(plugin);
        gui.addModule(aboutModule);
        return gui;
    }
}
