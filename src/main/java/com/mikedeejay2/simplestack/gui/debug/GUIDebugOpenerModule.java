package com.mikedeejay2.simplestack.gui.debug;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.simplestack.SimpleStack;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashSet;
import java.util.Set;

public class GUIDebugOpenerModule implements GUIModule {
    private final SimpleStack plugin;
    private static final int entranceAmt = 4;
    private static final Set<Player> PLAYERS = new HashSet<>();
    private int clickAmt;

    public GUIDebugOpenerModule(SimpleStack plugin) {
        this.plugin = plugin;
        this.clickAmt = 0;
    }

    @Override
    public void onClickedHead(InventoryClickEvent event, GUIContainer gui) {
        ClickType clickType = event.getClick();
        if(clickType != ClickType.NUMBER_KEY) return;
        int number = event.getHotbarButton();
        if(number != 0) return;
        Player player = (Player) event.getWhoClicked();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1 + ((float) clickAmt / entranceAmt));
        ++clickAmt;
        if(clickAmt < entranceAmt && !PLAYERS.contains(player)) return;
        PLAYERS.add(player);
        GUIContainer newGUI = new GUIDebuggerConstructor(plugin).get();
        newGUI.open(player);
    }
}
