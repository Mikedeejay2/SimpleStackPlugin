package com.mikedeejay2.simplestack.gui.config.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIConstructor;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIClickEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.sound.GUIPlaySoundEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.util.GUIAbstractClickEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.manager.PlayerGUI;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimDecoratorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimOutlineModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.util.GUIConfirmationModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.mikedeejay2lib.util.structure.NavigationHolder;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.config.ItemConfigValue;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl;
import com.mikedeejay2.simplestack.gui.config.modules.GUIItemConfigModule;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GUIItemRemoveConstructor implements GUIConstructor {
    private final SimpleStack plugin;

    private static final ItemBuilder confirmItem = ItemBuilder.of(Base64Head.CHECKMARK_LIME.get())
        .setName(Text.of("Remove Item"));
    private static final ItemBuilder denyItem = ItemBuilder.of(Base64Head.X_RED.get())
        .setName(Text.of("Cancel"));

    private final ItemConfigValue value;

    public GUIItemRemoveConstructor(SimpleStack plugin, ItemConfigValue value) {
        this.value = value;
        this.plugin = plugin;
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = new GUIContainer(plugin, "Remove Item Confirmation", 3);
        GUIAnimationModule animationModule = new GUIAnimationModule(plugin, 1);
        GUIAnimDecoratorModule outlineModule = new GUIAnimOutlineModule(
            GUIConfigConstructor.ANIMATED_GUI_ITEM, new AnimationSpecification(
            AnimationSpecification.Position.of(2, 5), AnimationSpecification.Style.CIRCULAR));
        GUIConfirmationModule confirmModule = new GUIConfirmationModule(getConfirmItem(), getDenyItem());
        gui.setItem(2, 5, new GUIItem(value.asItemBuilder()));
        gui.addModule(animationModule);
        gui.addModule(outlineModule);
        gui.addModule(confirmModule);
        return gui;
    }

    private GUIItem getConfirmItem() {
        return new GUIItem(confirmItem)
            .addEvent(new RemoveValueEvent(value))
            .addEvent(new NavigateBackEvent())
            .addEvent(new GUIPlaySoundEvent(Sound.UI_BUTTON_CLICK, 0.3f, 1f));
    }

    private GUIItem getDenyItem() {
        return new GUIItem(denyItem)
            .addEvent(new NavigateBackEvent())
            .addEvent(new GUIPlaySoundEvent(Sound.UI_BUTTON_CLICK, 0.3f, 1f));
    }

    private static final class RemoveValueEvent extends GUIAbstractClickEvent {
        private final ItemConfigValue value;

        public RemoveValueEvent(ItemConfigValue value) {
            this.value = value;
        }

        @Override
        protected void executeClick(GUIClickEvent info) {
            SimpleStackConfigImpl config = (SimpleStackConfigImpl) SimpleStackAPI.getConfig();
            config.removeItem(value);
        }
    }

    private static final class NavigateBackEvent extends GUIAbstractClickEvent {
        @Override
        protected void executeClick(GUIClickEvent info) {
            Player player = info.getPlayer();
            PlayerGUI playerGUI = SimpleStack.getInstance().getGUIManager().getPlayer(player);
            NavigationHolder<GUIContainer> system = playerGUI.getNavigation("config");
            GUIContainer backGUI = system.popBack();
            if(backGUI.containsModule(GUIItemConfigModule.class)) {
                GUIItemConfigModule module = backGUI.getModule(GUIItemConfigModule.class);
                if(!((SimpleStackConfigImpl) SimpleStackAPI.getConfig()).containsItem(module.getConfigValue())) {
                    backGUI = system.popBack();
                }
            }
            backGUI.open(player);
        }
    }
}
