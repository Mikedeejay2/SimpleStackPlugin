package com.mikedeejay2.simplestack.gui.config.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIClickEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.sound.GUIPlaySoundEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.util.GUIAbstractClickEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.MatcherDataType;
import com.mikedeejay2.simplestack.api.MatcherOperatorType;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.config.ItemConfigValue;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl;
import com.mikedeejay2.simplestack.config.matcher.MatcherDataSource;
import com.mikedeejay2.simplestack.config.matcher.MatcherDataSourceRegistry;
import com.mikedeejay2.simplestack.gui.config.constructors.GUIItemRemoveConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GUIItemConfigModule implements GUIModule {
    private static final ItemBuilder removeItem = ItemBuilder.of(Material.REDSTONE)
        .setName(Text.of("Remove Item"));
    private static final SimpleStackConfigImpl config = ((SimpleStackConfigImpl) SimpleStackAPI.getConfig());

    private final ItemConfigValue configValue;
    private final SimpleStack plugin;
    private final GUIListModule listModule;

    public GUIItemConfigModule(SimpleStack plugin, ItemConfigValue configValue, GUIListModule listModule) {
        this.configValue = configValue;
        this.plugin = plugin;
        this.listModule = listModule;
        genMatcherItems();
    }

    @Override
    public void onUpdateHead(Player player, GUIContainer gui) {
        gui.setItem(1, 5, new GUIItem(configValue.asItemBuilder()));
    }

    @Override
    public void onOpenHead(Player player, GUIContainer gui) {
        gui.setItem(gui.getRows(), 5, getRemoveItem());
        updateItems();
    }

    @Override
    public void onClickedTail(InventoryClickEvent event, GUIContainer gui) {
        updateItems();
    }

    private void genMatcherItems() {
        final Set<MatcherDataSource<?, ?>> dataSources = MatcherDataSourceRegistry.getDataSources(configValue.getItem().asItemStack().getItemMeta()); // asItemStack so ItemMeta is never null
        for(MatcherDataSource<?, ?> dataSource : dataSources) {
            State state = State.getState(dataSource.getDataType(), configValue.getMatcherDataTypes());
            GUIItem item = new GUIItem(state.getItem())
                .setName(Text.of(dataSource.getNameKey()))
                .addExtraData("state", state)
                .addExtraData("datasource", dataSource)
                .addEvent(new MatcherEvent(dataSource.getDataType(), configValue));
            updateItemSound(item, state);
            listModule.addItem(item);
        }
    }

    private void updateItems() {
        for(GUIItem item : listModule.getList()) {
            MatcherDataSource<?, ?> matcher = item.getExtraData("datasource", MatcherDataSource.class);
            State state = State.getState(matcher.getDataType(), configValue.getMatcherDataTypes());
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

        public static State getState(MatcherDataType dataType, Set<MatcherDataType> allTypes) {
            // TODO: ADD INCOMPATIBILITIES
            if(allTypes.contains(dataType)) return State.VALID_ENABLED;
            return State.VALID_DISABLED;
        }
    }

    private static final class MatcherEvent extends GUIAbstractClickEvent {
        private final MatcherDataType dataType;
        private final ItemConfigValue value;

        public MatcherEvent(MatcherDataType dataType, ItemConfigValue value) {
            super(ClickType.LEFT, ClickType.RIGHT);
            this.dataType = dataType;
            this.value = value;
        }

        @Override
        protected void executeClick(GUIClickEvent info) {
            State state = State.getState(dataType, value.getMatcherDataTypes());
            updateMatch(state);
            config.setItemsModified(true);
        }

        private void updateMatch(State state) {
            switch(state) {
                case VALID_ENABLED:
                    value.removeMatcher(dataType);
                    break;
                case VALID_DISABLED:
                    value.addMatcher(dataType, MatcherOperatorType.EQUALS);
                    break;
            }
        }
    }
}
