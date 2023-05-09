package com.mikedeejay2.simplestack.gui.config.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIClickEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.sound.GUIPlaySoundEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.util.GUIAbstractClickEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.config.ItemConfigValue;
import com.mikedeejay2.simplestack.config.ItemConfigValue.ItemMatcher;
import com.mikedeejay2.simplestack.gui.config.constructors.GUIItemRemoveConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Set;

public class GUIItemConfigModule implements GUIModule {
    private static final ItemBuilder removeItem = ItemBuilder.of(Material.REDSTONE)
        .setName(Text.of("Remove Item"));

    private final ItemConfigValue configValue;
    private final SimpleStack plugin;
    private final GUIItem[] matcherItems;

    public GUIItemConfigModule(SimpleStack plugin, ItemConfigValue configValue) {
        this.configValue = configValue;
        this.plugin = plugin;
        this.matcherItems = new GUIItem[ItemMatcher.values().length];
        genMatcherItems();
    }

    @Override
    public void onUpdateHead(Player player, GUIContainer gui) {
        gui.setItem(1, 5, new GUIItem(configValue.asItemBuilder()));
    }

    @Override
    public void onOpenHead(Player player, GUIContainer gui) {
        gui.setItem(gui.getRows(), 5, getRemoveItem());
        for(int i = 0; i < matcherItems.length; ++i) {
            gui.setItem(2, i + 2, matcherItems[i]);
        }
        updateItems();
    }

    @Override
    public void onClickedTail(InventoryClickEvent event, GUIContainer gui) {
        updateItems();
    }

    private void genMatcherItems() {
        final ItemMatcher[] matchValues = ItemMatcher.values();
        for(int i = 0; i < matchValues.length; ++i) {
            ItemMatcher match = matchValues[i];
            State state = State.getState(match, configValue.getMatchers());
            GUIItem item = new GUIItem(state.getItem())
                .setName(Text.of(match.getNameKey()))
                .addExtraData("state", state)
                .addExtraData("match", match)
                .addEvent(new MatcherEvent(match, configValue));
            updateItemSound(item, state);
            matcherItems[i] = item;
        }
    }

    private void updateItems() {
        for(GUIItem item : matcherItems) {
            ItemMatcher matcher = item.getExtraData("match", ItemMatcher.class);
            State state = State.getState(matcher, configValue.getMatchers());
            if(item.getExtraData("state", State.class) == state) continue;
            item.set(state.getItem())
                .setName(Text.of(matcher.getNameKey()))
                .addExtraData("state", state);
            updateItemSound(item, state);
        }
    }

    private static void updateItemSound(GUIItem item, State state) {
        switch(state) {
            case VALID_DISABLED:
            case VALID_ENABLED:
                if(item.containsEvent(GUIPlaySoundEvent.class)) break;
                item.addEvent(new GUIPlaySoundEvent(Sound.UI_BUTTON_CLICK, 0.3f, 1f));
                break;
            case INVALID_ENABLED:
            case INVALID_DISABLED:
                if(!item.containsEvent(GUIPlaySoundEvent.class)) break;
                item.removeEvent(GUIPlaySoundEvent.class);
                break;
        }
    }

    private GUIItem getRemoveItem() {
        return new GUIItem(ItemBuilder.of(Material.REDSTONE))
            .setName(Text.of("Remove Item"))
            .addEvent(new GUIOpenNewEvent(plugin, new GUIItemRemoveConstructor(plugin, configValue)))
            .addEvent(new GUIPlaySoundEvent(Sound.UI_BUTTON_CLICK, 0.3f, 1f));
    }

    public ItemConfigValue getConfigValue() {
        return configValue;
    }

    private enum State {
        VALID_ENABLED(ItemBuilder.of(Base64Head.CHECKMARK_WHITE.get())),
        VALID_DISABLED(ItemBuilder.of(Base64Head.X_WHITE.get())),
        INVALID_ENABLED(ItemBuilder.of(Base64Head.CHECKMARK_LIGHT_GRAY.get())),
        INVALID_DISABLED(ItemBuilder.of(Base64Head.X_LIGHT_GRAY.get()));

        private final ItemBuilder item;

        State(ItemBuilder item) {
            this.item = item;
        }

        public ItemBuilder getItem() {
            return item;
        }

        public static State getState(ItemMatcher matcher, Set<ItemMatcher> allMatchers) {
            // TODO: ADD INCOMPATIBILITIES
            if(allMatchers.contains(matcher)) return State.VALID_ENABLED;
            return State.VALID_DISABLED;
        }
    }

    private static final class MatcherEvent extends GUIAbstractClickEvent {
        private final ItemMatcher matcher;
        private final ItemConfigValue value;

        public MatcherEvent(ItemMatcher matcher, ItemConfigValue value) {
            super(ClickType.LEFT, ClickType.RIGHT);
            this.matcher = matcher;
            this.value = value;
        }

        @Override
        protected void executeClick(GUIClickEvent info) {
            State state = State.getState(matcher, value.getMatchers());
            updateMatch(state);
        }

        private void updateMatch(State state) {
            switch(state) {
                case VALID_ENABLED:
                    value.removeMatcher(matcher);
                    break;
                case VALID_DISABLED:
                    value.addMatcher(matcher);
                    break;
            }
        }
    }
}
