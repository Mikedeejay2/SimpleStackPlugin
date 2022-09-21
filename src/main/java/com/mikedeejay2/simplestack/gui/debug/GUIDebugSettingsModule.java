package com.mikedeejay2.simplestack.gui.debug;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification.Position;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification.Style;
import com.mikedeejay2.mikedeejay2lib.gui.event.button.GUIButtonToggleableEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimDecoratorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.util.GUIAbstractRuntimeModule;
import com.mikedeejay2.mikedeejay2lib.gui.util.SlotMatcher;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.debug.DebugSystem;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.Consumer;

public class GUIDebugSettingsModule extends GUIAbstractRuntimeModule {
    private final SimpleStack plugin;
    private final DebugSystem debugSystem;

    private GUIItem statisticsItem;

    public GUIDebugSettingsModule(SimpleStack plugin) {
        super(plugin, 0, 20);
        this.plugin = plugin;
        this.debugSystem = plugin.getDebugSystem();
    }

    @Override
    public void onOpenHead(Player player, GUIContainer gui) {
        GUILayer layer = gui.getLayer("settings");

        GUIItem collectTimingsButton = getCollectTimingsButton();
        GUIItem infoItem = getInfoItem();
        GUIItem viewEntriesButton = getViewEntriesButton();
        statisticsItem = new GUIItem(ItemBuilder.of(Base64Head.QUESTION_MARK_LIME.get()).get())
            .setName("&bTimings Statistics");
        layer.setItem(2, 2, infoItem);
        layer.setItem(2, 4, collectTimingsButton);
        layer.setItem(2, 6, viewEntriesButton);
        layer.setItem(2, 8, statisticsItem);

        super.onOpenHead(player, gui);
    }

    @Override
    protected Consumer<RunInfo> getConsumer() {
        return info -> {
            if(statisticsItem == null) return;
            List<String> oldLore = statisticsItem.getLore();
            if(!debugSystem.isCollecting()) {
                statisticsItem.setLore("",
                                       "&7&oTimings aren't enabled.");
            } else {
                statisticsItem.setLore("",
                                       "&fMs per tick (min/med/avg/95%ile/max ms):",
                                       "&f5s:  " + debugSystem.getTimingString(100),
                                       "&f10s: " + debugSystem.getTimingString(200),
                                       "&f1m:  " + debugSystem.getTimingString(1200),
                                       "&f5m:  " + debugSystem.getTimingString(6000),
                                       "&f15m: " + debugSystem.getTimingString(18000));
            }
            if(oldLore == null || !oldLore.equals(statisticsItem.getLore())) {
                info.getGui().update(info.getPlayer());
            }
        };
    }

    private GUIItem getCollectTimingsButton() {
        final ItemStack collectOn = ItemBuilder.of(Base64Head.CHECKMARK_LIME.get())
                .setName("&bCollect timings")
                .setLore("",
                         "&a&l⊳ True",
                         "&7  False")
                .get();
        final ItemStack collectOff = ItemBuilder.of(Base64Head.X_RED.get())
                .setName("&bCollect timings")
                .setLore("",
                         "&7  True",
                         "&c&l⊳ False")
                .get();

        return new GUIItem(debugSystem.isCollecting() ? collectOn : collectOff)
            .addEvent(new GUIButtonToggleableEvent((info) -> {
                info.getGUIItem().set(collectOn);
                debugSystem.startCollecting();
            }, (info) -> {
                info.getGUIItem().set(collectOff);
                debugSystem.stopCollecting();
            }, debugSystem.isCollecting()));
    }

    private static AnimatedGUIItem getInfoItem() {
        ItemBuilder infoBuilder = ItemBuilder.of(Base64Head.EXCLAMATION_MARK_RED.get())
            .setName("&a&lINFO")
            .setLore("",
                     "&fWelcome to the debug menu!",
                     "&fThis menu has options that shouldn't",
                     "&fbe modified unless you have been",
                     "&fexplicitly told to.",
                     "&fOr you can mess with them",
                     "&fif you know what you're doing.");

        AnimatedGUIItem infoItem = new AnimatedGUIItem(infoBuilder.get(), true);
        infoItem.addFrame(infoBuilder.get(), 20);
        infoItem.addFrame(infoBuilder.setHeadBase64(Base64Head.CONCRETE_RED.get()).get(), 20);
        return infoItem;
    }

    private GUIItem getViewEntriesButton() {
        GUIItem viewEntriesButton = new GUIItem(
            ItemBuilder.of(Base64Head.QUESTION_MARK_LIME.get())
                .setName("&bView timing entries")
                .setLore("",
                         "&fOpens a list of every",
                         "&findividual action, up to",
                         "&f500 actions.")
                .get());

        viewEntriesButton.addEvent(new GUIOpenNewEvent(plugin, () -> {
            GUIContainer newGui = new GUIContainer(plugin, "&cTiming Entries", 6);
            GUIListModule list = new GUIListModule(plugin, GUIListModule.ListViewMode.PAGED, 2, 6, 1, 9, "list");
            list.addBack(1, 4);
            list.addBack(1, 3);
            list.addBack(1, 2);
            list.addForward(1, 6);
            list.addForward(1, 7);
            list.addForward(1, 8);
            newGui.addModule(new GUIAnimationModule(plugin, 1));
            newGui.addModule(new GUIAnimDecoratorModule(
                SlotMatcher.inRange(1, 1, 1, 9),
                GUIDebuggerConstructor.OUTLINE_ITEM, new AnimationSpecification(Position.TOP_LEFT, Style.COL)));
            newGui.addModule(new GUINavigatorModule(plugin, "config"));
            newGui.addModule(new GUIGetEntriesModule(plugin, list, debugSystem));
            newGui.addModule(list);

            return newGui;
        }));
        return viewEntriesButton;
    }

    private static final class GUIGetEntriesModule extends GUIAbstractRuntimeModule {
        private final GUIListModule list;
        private final DebugSystem debugSystem;

        public GUIGetEntriesModule(SimpleStack plugin, GUIListModule list, DebugSystem debugSystem) {
            super(plugin, 0, 100);
            this.list = list;
            this.debugSystem = debugSystem;
        }

        @Override
        public void onOpenHead(Player player, GUIContainer gui) {
            super.onOpenHead(player, gui);
        }

        private void updateList(GUIContainer gui) {
            list.resetList();
            List<DebugSystem.TimingEntry> detailedTimings;
            if(!this.debugSystem.isCollecting()) {
                gui.setItem(3, 5, new GUIItem(
                    ItemBuilder.of(Base64Head.EXCLAMATION_MARK_RED.get())
                        .setName("&cError!")
                        .setLore("",
                                 "&7&oTimings aren't enabled.",
                                 "&7&oThis can be enabled in",
                                 "&7&othe previous GUI.")
                        .get()));
                return;
            } else if((detailedTimings = this.debugSystem.getDetailedTimings()).isEmpty()) {
                gui.setItem(3, 5, new GUIItem(
                    ItemBuilder.of(Base64Head.EXCLAMATION_MARK_RED.get())
                        .setName("&cError!")
                        .setLore("",
                                 "&7&oThere isn't any timing",
                                 "&7&oinformation currently recorded.",
                                 "&7&oTry moving some items around.")
                        .get()));
                return;
            }
            gui.removeItem(3, 5);
            for(int i = detailedTimings.size() - 1; i >= 0; --i) {
                list.addListItem(new GUIItem(getItemStack(detailedTimings.get(i))));
            }
        }

        @Override
        protected Consumer<RunInfo> getConsumer() {
            return info -> {
                updateList(info.getGui());
                info.getGui().update(info.getPlayer());
            };
        }

        private ItemStack getItemStack(DebugSystem.TimingEntry entry) {
            String base64Head;
            switch(entry.color) {
                case GREEN: base64Head = Base64Head.LIME.get();break;
                case YELLOW: base64Head = Base64Head.YELLOW.get();break;
                case RED: base64Head = Base64Head.RED.get();break;
                default: base64Head = Base64Head.EXCLAMATION_MARK_RED.get();break;
            }
            return ItemBuilder.of(base64Head)
                .setName(entry.color + entry.name)
                .setLore(
                    String.format("%s%.4fms", entry.color, (entry.nanoTime / 1000000.0)),
                    "&7&o" + new SimpleDateFormat("HH:mm:ss.SSS").format(entry.msTime))
                .get();
        }
    }
}
