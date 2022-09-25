package com.mikedeejay2.simplestack.gui.debug;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.button.GUIButtonToggleableEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.util.GUIAbstractRuntimeModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.debug.DebugSystem;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public class GUIDebugSettingsModule extends GUIAbstractRuntimeModule {
    private static final ItemBuilder INFO_ITEM = ItemBuilder.of(Base64Head.EXCLAMATION_MARK_RED.get())
        .setName("&a&lINFO")
        .setLore(
            "",
            "&fWelcome to the debug menu!",
            "&fThis menu has options that shouldn't",
            "&fbe modified unless you have been",
            "&fexplicitly told to.",
            "&fOr you can mess with them",
            "&fif you know what you're doing.");
    private static final ItemBuilder VIEW_ENTRIES_ITEM = ItemBuilder.of(Base64Head.QUESTION_MARK_LIME.get())
        .setName("&bView timing entries")
        .setLore(
            "",
            "&fOpens a list of every",
            "&findividual action, up to",
            "&f500 actions.");
    private static final ItemBuilder COLLECT_ON_ITEM = ItemBuilder.of(Base64Head.CHECKMARK_LIME.get())
        .setName("&bCollect timings")
        .setLore(
            "",
            "&a&l⊳ True",
            "&7  False");
    private static final ItemBuilder COLLECT_OFF_ITEM = ItemBuilder.of(Base64Head.X_RED.get())
        .setName("&bCollect timings")
        .setLore(
            "",
            "&7  True",
            "&c&l⊳ False");


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
                statisticsItem.setLore(
                    "",
                    "&7&oTimings aren't enabled.");
            } else {
                statisticsItem.setLore(
                    "",
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
        return new GUIItem(debugSystem.isCollecting() ? COLLECT_ON_ITEM : COLLECT_OFF_ITEM).addEvent(
            new GUIButtonToggleableEvent(
                (info) -> debugSystem.startCollecting(),
                (info) -> debugSystem.stopCollecting(),
                debugSystem.isCollecting())
                .setOnItem(COLLECT_ON_ITEM)
                .setOffItem(COLLECT_OFF_ITEM));
    }

    private static AnimatedGUIItem getInfoItem() {
        AnimatedGUIItem infoItem = new AnimatedGUIItem(INFO_ITEM, true);
        infoItem.addFrame(INFO_ITEM.get(), 20);
        infoItem.addFrame(INFO_ITEM.setHeadBase64(Base64Head.CONCRETE_RED.get()).get(), 20);
        return infoItem;
    }

    private GUIItem getViewEntriesButton() {
        GUIItem viewEntriesButton = new GUIItem(VIEW_ENTRIES_ITEM);
        viewEntriesButton.addEvent(new GUIOpenNewEvent(plugin, GUIDebugEntriesConstructor.INSTANCE));
        return viewEntriesButton;
    }
}
