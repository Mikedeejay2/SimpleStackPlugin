package com.mikedeejay2.simplestack.gui.debug;

import com.mikedeejay2.mikedeejay2lib.gui.GUIConstructor;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.GUIAnimPattern;
import com.mikedeejay2.mikedeejay2lib.gui.event.button.GUIButtonToggleableEvent;
import com.mikedeejay2.mikedeejay2lib.gui.event.navigation.GUIOpenNewEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimDecoratorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimOutlineModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimStripsModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIDecoratorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.util.GUIRuntimeModule;
import com.mikedeejay2.mikedeejay2lib.gui.util.SlotMatcher;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.debug.DebugSystem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class GUIDebugSettingsModule implements GUIModule {
    private final SimpleStack plugin;
    private final DebugSystem debugSystem;

    public GUIDebugSettingsModule(SimpleStack plugin) {
        this.plugin = plugin;
        this.debugSystem = plugin.getDebugSystem();
    }

    @Override
    public void onOpenHead(Player player, GUIContainer gui) {
        GUILayer layer = gui.getLayer("settings");

        final ItemStack collectOn = ItemBuilder.of(Base64Head.CHECKMARK_GREEN.get())
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

        GUIItem collectTimingsButton = new GUIItem(debugSystem.isCollecting() ? collectOn : collectOff)
            .addEvent(new GUIButtonToggleableEvent((info) -> {
                info.getGUIItem().setItem(collectOn);
                debugSystem.startCollecting();
            }, (info) -> {
                info.getGUIItem().setItem(collectOff);
                debugSystem.stopCollecting();
            }, debugSystem.isCollecting()));

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

        layer.setItem(2, 2, infoItem);
        layer.setItem(2, 3, collectTimingsButton);

        GUIItem viewEntriesButton = new GUIItem(
            ItemBuilder.of(Base64Head.FOLDER.get())
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
                GUIDebuggerConstructor.OUTLINE_ITEM, GUIAnimPattern.LEFT_RIGHT));
            newGui.addModule(new GUINavigatorModule(plugin, "config"));
            newGui.addModule(new GUIGetEntriesModule(list, debugSystem));
            newGui.addModule(list);

            return newGui;
        }));

        layer.setItem(2, 4, viewEntriesButton);
    }

    private static final class GUIGetEntriesModule implements GUIModule {
        private final GUIListModule list;
        private final DebugSystem debugSystem;

        public GUIGetEntriesModule(GUIListModule list, DebugSystem debugSystem) {
            this.list = list;
            this.debugSystem = debugSystem;
        }

        @Override
        public void onOpenHead(Player player, GUIContainer gui) {
            list.resetList();
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
            } else if(this.debugSystem.getDetailedTimings().isEmpty()) {
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

            for(DebugSystem.TimingEntry entry : this.debugSystem.getDetailedTimings()) {
                list.addListItem(new GUIItem(getItemStack(entry)));
            }
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
                .setLore(String.format("%s%.4fms", entry.color, (entry.nanoTime / 1000000.0)))
                .get();
        }
    }
}
