package com.mikedeejay2.simplestack.gui.debug;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.config.DebugConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class GUIDebugSettingsModule implements GUIModule
{
    private final DebugConfig debugConfig;

    public GUIDebugSettingsModule(DebugConfig debugConfig)
    {
        this.debugConfig = debugConfig;
    }

    @Override
    public void onOpenHead(Player player, GUIContainer gui)
    {
        GUILayer layer = gui.getLayer("settings");

        final GUIItem printTimingsOn = new GUIItem(
                ItemBuilder.of(Base64Head.CHECKMARK_GREEN.get())
                        .setName("&bPrint Timings")
                        .setLore("",
                                 "&a&l⊳ True",
                                 "&7  False")
                        .get());
        final GUIItem printTimingsOff = new GUIItem(
                ItemBuilder.of(Base64Head.X_RED.get())
                        .setName("&bPrint Timings")
                        .setLore("",
                                 "&7  True",
                                 "&a&l⊳ False")
                        .get());

        printTimingsOff.addEvent((event) -> {
            if(event.getClick() != ClickType.LEFT) return;
            int slot = event.getSlot();
            GUIContainer gui1 = event.getGUI();
            int row = gui1.getRowFromSlot(slot);
            int col = gui1.getColFromSlot(slot);
            layer.setItem(row, col, printTimingsOn);
            debugConfig.setPrintTimings(true);
        });

        printTimingsOn.addEvent((event) -> {
            if(event.getClick() != ClickType.LEFT) return;
            int slot = event.getSlot();
            GUIContainer gui1 = event.getGUI();
            int row = gui1.getRowFromSlot(slot);
            int col = gui1.getColFromSlot(slot);
            layer.setItem(row, col, printTimingsOff);
            debugConfig.setPrintTimings(false);
        });

        final GUIItem printActionsOn = new GUIItem(
                ItemBuilder.of(Base64Head.CHECKMARK_GREEN.get())
                        .setName("&bPrint Actions")
                        .setLore("",
                                 "&a&l⊳ True",
                                 "&7  False")
                        .get());
        final GUIItem printActionsOff = new GUIItem(
                ItemBuilder.of(Base64Head.X_RED.get())
                        .setName("&bPrint Actions")
                        .setLore("",
                                 "&7  True",
                                 "&a&l⊳ False")
                        .get());

        printActionsOn.addEvent((event) -> {
            if(event.getClick() != ClickType.LEFT) return;
            int slot = event.getSlot();
            GUIContainer gui1 = event.getGUI();
            int row = gui1.getRowFromSlot(slot);
            int col = gui1.getColFromSlot(slot);
            layer.setItem(row, col, printActionsOff);
            debugConfig.setPrintAction(false);
        });

        printActionsOff.addEvent((event) -> {
            if(event.getClick() != ClickType.LEFT) return;
            int slot = event.getSlot();
            GUIContainer gui1 = event.getGUI();
            int row = gui1.getRowFromSlot(slot);
            int col = gui1.getColFromSlot(slot);
            layer.setItem(row, col, printActionsOn);
            debugConfig.setPrintAction(true);
        });

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

        layer.setItem(2, 3, debugConfig.isPrintTimings() ? printTimingsOn : printTimingsOff);
        layer.setItem(2, 7, debugConfig.isPrintAction() ? printActionsOn : printActionsOff);
        layer.setItem(2, 5, infoItem);
    }
}
