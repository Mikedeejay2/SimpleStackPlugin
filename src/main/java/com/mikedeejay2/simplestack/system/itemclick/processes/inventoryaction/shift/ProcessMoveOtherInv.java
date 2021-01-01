package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction.shift;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessMoveOtherInv implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        info.player.sendMessage("Move to Other Inv");


//        Inventory   otherInv    = info.clickedBottom ? info.topInv : info.bottomInv;
//        ItemStack[] topItems    = otherInv.getStorageContents();
//        Material    selectedMat = info.selected.getType();
//        int selectedAmt = info.selectedAmt;
//        for(int i = 0; i < topItems.length; ++i)
//        {
//            ItemStack item = topItems[i];
//            if(item == null) continue;
//            if(item.getType() != selectedMat) continue;
//            int itemAmt = item.getAmount();
//            if(itemAmt == info.selectedMax) continue;
//            int newAmt = itemAmt + info.selectedAmt;
//            if(newAmt > info.selectedMax)
//            {
//                selectedAmt = newAmt - info.selectedMax;
//                newAmt = info.selectedMax;
//            }
//            else
//            {
//                selectedAmt = 0;
//            }
//            item.setAmount(newAmt);
//            if(selectedAmt <= 0)
//            {
//                info.selected.setAmount(0);
//                return;
//            }
//        }
//        for(int i = 0; i < topItems.length; ++i)
//        {
//            ItemStack item = topItems[i];
//            if(item != null) continue;
//            item = info.selected.clone();
//            int newAmt = selectedAmt;
//            if(newAmt > info.selectedMax)
//            {
//                selectedAmt = newAmt - info.selectedMax;
//                newAmt = info.selectedMax;
//            }
//            else
//            {
//                selectedAmt -= newAmt;
//            }
//            item.setAmount(newAmt);
//            otherInv.setItem(i, item);
//            info.player.sendMessage("selectedAmt: " + selectedAmt);
//            if(selectedAmt <= 0)
//            {
//                info.selected.setAmount(0);
//                return;
//            }
//        }
//        info.selected.setAmount(selectedAmt);
    }
}
