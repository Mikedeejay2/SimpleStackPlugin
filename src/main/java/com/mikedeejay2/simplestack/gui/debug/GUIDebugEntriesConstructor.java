package com.mikedeejay2.simplestack.gui.debug;

import com.mikedeejay2.mikedeejay2lib.gui.GUIConstructor;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification;
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
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.SimpleStackTimings;
import com.mikedeejay2.simplestack.debug.SimpleStackTimingsImpl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.Consumer;

public class GUIDebugEntriesConstructor implements GUIConstructor {
    public static final GUIDebugEntriesConstructor INSTANCE = new GUIDebugEntriesConstructor(
        SimpleStack.getInstance(), SimpleStackAPI.getTimings());

    private final SimpleStack plugin;
    private final SimpleStackTimings timings;

    private GUIDebugEntriesConstructor(SimpleStack plugin, SimpleStackTimings timings) {
        this.plugin = plugin;
        this.timings = timings;
    }

    @Override
    public GUIContainer get() {
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
            GUIDebuggerConstructor.OUTLINE_ITEM, new AnimationSpecification(
                AnimationSpecification.Position.TOP_LEFT, AnimationSpecification.Style.COL)));
        newGui.addModule(new GUINavigatorModule(plugin, "config"));
        newGui.addModule(new GUIGetEntriesModule(plugin, list, timings));
        newGui.addModule(list);

        return newGui;
    }

    private static final class GUIGetEntriesModule extends GUIAbstractRuntimeModule {
        private final GUIListModule list;
        private final SimpleStackTimings timings;

        public GUIGetEntriesModule(SimpleStack plugin, GUIListModule list, SimpleStackTimings timings) {
            super(plugin, 0, 100);
            this.list = list;
            this.timings = timings;
        }

        @Override
        public void onOpenHead(Player player, GUIContainer gui) {
            super.onOpenHead(player, gui);
        }

        private void updateList(GUIContainer gui) {
            list.resetList();
            List<SimpleStackTimingsImpl.TimingEntry> detailedTimings;
            if(!this.timings.isCollecting()) {
                gui.setItem(3, 5, new GUIItem(
                    ItemBuilder.of(Base64Head.EXCLAMATION_MARK_RED.get())
                        .setName("&cError!")
                        .setLore(
                            "",
                            "&7&oTimings aren't enabled.",
                            "&7&oThis can be enabled in",
                            "&7&othe previous GUI.")
                        .get()));
                return;
            } else if((detailedTimings = this.timings.getDetailedTimings()).isEmpty()) {
                gui.setItem(3, 5, new GUIItem(
                    ItemBuilder.of(Base64Head.EXCLAMATION_MARK_RED.get())
                        .setName("&cError!")
                        .setLore(
                            "",
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

        private ItemStack getItemStack(SimpleStackTimingsImpl.TimingEntry entry) {
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
