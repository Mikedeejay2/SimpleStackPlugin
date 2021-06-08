package com.mikedeejay2.simplestack.system.itemdrag.preprocess.dragtype;

import com.mikedeejay2.mikedeejay2lib.util.debug.DebugUtil;
import com.mikedeejay2.simplestack.system.itemdrag.ItemDragInfo;
import com.mikedeejay2.simplestack.system.itemdrag.ItemDragType;
import com.mikedeejay2.simplestack.system.itemdrag.preprocess.ItemDragPreprocess;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

public class PreprocessClone implements ItemDragPreprocess
{
    @Override
    public void invoke(ItemDragInfo data)
    {
//        System.out.println("Stack size: " + data.cursorAmount);
        if(data.player.getGameMode() != GameMode.CREATIVE) return;
        if(data.newItems.size() < 2) return;
        int stackSize = data.oldCursor.getMaxStackSize();
        int totalStackSize = 0;
        for(ItemStack itemStack : data.newItems.values())
        {
            if(itemStack.getAmount() != stackSize) return;
            totalStackSize += itemStack.getAmount();
        }
        DebugUtil.printLineNumber();
        boolean equalAmounts = totalStackSize + data.cursorAmount == data.oldCursorAmount;
        boolean cursorNotEmpty = data.cursorAmount != 0;
        boolean cursorLargerThan = data.cursorAmount > stackSize;
//        System.out.println("equalAmounts: " + equalAmounts);
//        System.out.println("cursorNotEmpty: " + cursorNotEmpty);
//        System.out.println("cursorLargerThan: " + cursorLargerThan);
        if(equalAmounts && cursorNotEmpty && cursorLargerThan) return;
        System.out.println("Success");
        data.setType(ItemDragType.CLONE);
    }
}
