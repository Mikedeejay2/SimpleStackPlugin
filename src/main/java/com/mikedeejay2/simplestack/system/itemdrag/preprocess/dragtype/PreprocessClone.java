package com.mikedeejay2.simplestack.system.itemdrag.preprocess.dragtype;

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
        if(data.player.getGameMode() != GameMode.CREATIVE) return;
        System.out.println("Ending stack size: " + (!data.cursorNull ? 0 : data.cursor.getAmount()));
        int stackSize = data.oldCursor.getMaxStackSize();
        int totalStackSize = 0;
        for(ItemStack itemStack : data.newItems.values())
        {
            if(itemStack.getAmount() != stackSize)
            {
                System.out.println("Item: " + itemStack);
                return;
            }
            totalStackSize += itemStack.getAmount();
        }
        if(totalStackSize == data.oldCursor.getAmount()) return;
        if(!data.cursorNull && data.cursor.getAmount() != stackSize)
        {
            System.out.println("Cursor: " + data.cursor);
            return;
        }
        data.setType(ItemDragType.CLONE);
    }
}
