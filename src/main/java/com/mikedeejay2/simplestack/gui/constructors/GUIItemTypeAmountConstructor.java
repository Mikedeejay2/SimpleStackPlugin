package com.mikedeejay2.simplestack.gui.constructors;

import com.mikedeejay2.mikedeejay2lib.gui.GUIConstructor;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractType;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractHandlerList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractExecutorDefaultInv;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIDecoratorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.gui.modules.GUIItemTypeAmountModule;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static com.mikedeejay2.mikedeejay2lib.gui.util.SlotMatcher.inRange;

public class GUIItemTypeAmountConstructor implements GUIConstructor {
    public static final GUIItemTypeAmountConstructor INSTANCE = new GUIItemTypeAmountConstructor(SimpleStack.getInstance());

    private final SimpleStack plugin;

    private GUIItemTypeAmountConstructor(SimpleStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = new GUIContainer(plugin, Text.of("simplestack.gui.item_type_amts.title"), 6);
        GUIDecoratorModule border = new GUIDecoratorModule(
            inRange(1, 1, 1, 9).or(inRange(6, 1, 6, 9)),
            new GUIItem(ItemBuilder.of(Base64Head.WHITE.get()).setEmptyName().get()));
        gui.addModule(border);
        GUINavigatorModule navi = new GUINavigatorModule(plugin, "config");
        gui.addModule(navi);
        GUIListModule listModule = new GUIListModule(plugin, GUIListModule.ListViewMode.PAGED, 2, 5, 1, 9);
        GUIItem padItem = new GUIItem((ItemStack) null);
        padItem.setMovable(true);
        listModule.addEndItem(padItem);
        for(Map.Entry<Material, Integer> entry : plugin.config().getItemAmounts().entrySet()) {
            Material material = entry.getKey();
            if(material == null) continue;
            int amount = entry.getValue();
            ItemStack item = new ItemStack(material, amount);
            GUIItem guiItem = new GUIItem(item);
            guiItem.setMovable(true);
            listModule.addListItem(guiItem);
        }
        listModule.addForward(6, 6).addForward(6, 7).addForward(6, 8);
        listModule.addBack(6, 4).addBack(6, 3).addBack(6, 2);
        gui.addModule(listModule);
        GUIInteractHandler interaction = new GUIInteractHandlerList(64);
        interaction.resetExecutors();
        interaction.addExecutor(new GUIInteractExecutorDefaultInv(64));
        interaction.addExecutor(new GUIInteractExecutorList(GUIInteractType.SINGLE_MATERIAL, 64, false));
        gui.setDefaultMoveState(true);
        gui.setInteractionHandler(interaction);
        GUIItemTypeAmountModule itemTypeAmountModule = new GUIItemTypeAmountModule(plugin);
        gui.addModule(itemTypeAmountModule);
        return gui;
    }
}
