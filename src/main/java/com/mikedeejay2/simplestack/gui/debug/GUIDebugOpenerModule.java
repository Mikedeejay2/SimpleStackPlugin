package com.mikedeejay2.simplestack.gui.debug;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIDebugOpenerModule implements GUIModule
{
    private final Simplestack plugin;
    private static final int entranceAmt = 4;
    private int clickAmt;

    public GUIDebugOpenerModule(Simplestack plugin)
    {
        this.plugin = plugin;
        this.clickAmt = 0;
    }

    @Override
    public void onClickedHead(InventoryClickEvent event, GUIContainer gui)
    {
        ClickType clickType = event.getClick();
        if(clickType != ClickType.NUMBER_KEY) return;
        int number = event.getHotbarButton();
        if(number != 0) return;
        Player player = (Player) event.getWhoClicked();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1 + ((float)clickAmt / entranceAmt));
        ++clickAmt;
        if(clickAmt < entranceAmt) return;
        GUIContainer newGUI = new GUIDebuggerConstructor(plugin).get();
        newGUI.open(player);
    }
}
