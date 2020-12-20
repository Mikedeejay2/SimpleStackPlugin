package com.mikedeejay2.simplestack.handlers;

import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemClickHandler implements SimpleStackHandler<InventoryClickEvent>
{
    protected final Simplestack plugin;

    public ItemClickHandler(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void handle(InventoryClickEvent event)
    {
        Player player           = (Player) event.getWhoClicked();
        ClickType clickType     = event.getClick();
        ItemStack cursor        = event.getCursor();
        if(cursor != null && cursor.getType() == Material.AIR) cursor = null;
        ItemStack selected      = event.getCurrentItem();
        if(selected != null && selected.getType() == Material.AIR) selected = null;
        int slot                = event.getSlot();
        int hotbar              = event.getHotbarButton();
        InventoryAction action  = event.getAction();
        boolean cursorNull      = cursor == null;
        boolean selectedNull    = selected == null;
        int cursorMax           = cursorNull ? 0 : StackUtils.getMaxAmount(plugin, cursor);
        int selectedMax         = selectedNull ? 0 : StackUtils.getMaxAmount(plugin, selected);
        int cursorAmt           = cursorNull ? 0 : cursor.getAmount();
        int selectedAmt         = selectedNull ? 0 : selected.getAmount();
        boolean validSlot       = slot >= 0;
        InventoryView invView   = event.getView();
        Inventory bottomInv     = invView.getBottomInventory();
        Inventory topInv        = invView.getTopInventory();
        Inventory clickedInv    = event.getClickedInventory();
        boolean clickedBottom   = clickedInv == bottomInv;
        boolean clickedTop      = clickedInv == topInv;
        boolean clickedBorder   = slot == -1;
        boolean clickedOutside  = slot == -999;

        switch(clickType)
        {
            case LEFT:                                                                                                  // LEFT CLICK ////////////////////////////
            {
                if(!cursorNull && !selectedNull)
                {
                    if(ItemComparison.equalsEachOther(cursor, selected))
                    {
                        if(selectedAmt >= selectedMax)
                        {
                            action = InventoryAction.NOTHING;
                        }
                        else
                        {
                            int totalAmt = cursorAmt + selectedAmt;
                            if(totalAmt == selectedAmt + 1)
                            {
                                action = InventoryAction.PLACE_ONE;
                            }
                            else if(totalAmt > selectedMax)
                            {
                                action = InventoryAction.PLACE_SOME;
                            }
                            else
                            {
                                action = InventoryAction.PLACE_ALL;
                            }
                        }
                    }
                    else if(selectedAmt <= selectedMax)
                    {
                        action = InventoryAction.SWAP_WITH_CURSOR;
                    }
                    else
                    {
                        action = InventoryAction.NOTHING;
                    }
                }
                else if(!cursorNull)
                {
                    if(validSlot)
                    {
                        if(cursorAmt <= cursorMax)
                        {
                            action = InventoryAction.PLACE_ALL;
                        }
                        else if(cursorMax == 1)
                        {
                            action = InventoryAction.PLACE_ONE;
                        }
                        else
                        {
                            action = InventoryAction.PLACE_SOME;
                        }
                    }
                    else if(clickedOutside)
                    {
                        action = InventoryAction.DROP_ALL_CURSOR;
                    }
                    else
                    {
                        action = InventoryAction.NOTHING;
                    }
                }
                else if(!selectedNull)
                {
                    if(selectedAmt <= selectedMax)
                    {
                        action = InventoryAction.PICKUP_ALL;
                    }
                    else if(selectedMax == 1)
                    {
                        action = InventoryAction.PICKUP_ONE;
                    }
                    else
                    {
                        action = InventoryAction.PICKUP_SOME;
                    }
                }
            } break;
            case RIGHT:                                                                                                 // RIGHT CLICK ///////////////////////////
            {
                if(!cursorNull && !selectedNull)
                {
                    if(ItemComparison.equalsEachOther(cursor, selected))
                    {
                        if(selectedAmt >= selectedMax)
                        {
                            action = InventoryAction.NOTHING;
                        }
                        else
                        {
                            int totalAmt = selectedAmt + 1;
                            if(totalAmt > selectedMax)
                            {
                                action = InventoryAction.NOTHING;
                            }
                            else
                            {
                                action = InventoryAction.PLACE_ONE;
                            }
                        }
                    }
                    else if(selectedAmt <= selectedMax)
                    {
                        action = InventoryAction.SWAP_WITH_CURSOR;
                    }
                    else
                    {
                        action = InventoryAction.NOTHING;
                    }
                }
                else if(!cursorNull)
                {
                    if(validSlot)
                    {
                        action = InventoryAction.PLACE_ONE;
                    }
                    else if(clickedOutside)
                    {
                        action = InventoryAction.DROP_ONE_CURSOR;
                    }
                }
                else if(!selectedNull)
                {
                    action = InventoryAction.PICKUP_HALF;
                }
            } break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:                                                                                           // SHIFT CLICK ///////////////////////////
            {
                if(!selectedNull)
                {
                    action = InventoryAction.MOVE_TO_OTHER_INVENTORY;
                }
                else
                {
                    action = InventoryAction.NOTHING;
                }
            } break;
            case MIDDLE:                                                                                                // MIDDLE CLICK //////////////////////////
            {
                GameMode gamemode = player.getGameMode();
                boolean creative = gamemode == GameMode.CREATIVE;
                if(!creative)
                {
                    action = InventoryAction.NOTHING;
                }
                else
                {
                    if(!cursorNull)
                    {
                        action = InventoryAction.NOTHING;
                    }
                    else if(!selectedNull)
                    {
                        action = InventoryAction.CLONE_STACK;
                    }
                }
            } break;
            case NUMBER_KEY:                                                                                            // NUMBER KEY ////////////////////////////
            {
                ItemStack hotBarItem = bottomInv.getItem(hotbar);
                boolean hotBarNull = hotBarItem == null;
                if(hotBarItem != null && hotBarItem.getType() == Material.AIR) hotBarItem = null;
                if(clickedTop)
                {
                    if(!hotBarNull && !selectedNull)
                    {
                        action = InventoryAction.HOTBAR_MOVE_AND_READD;
                    }
                    else if(!hotBarNull || !selectedNull)
                    {
                        action = InventoryAction.HOTBAR_SWAP;
                    }
                    else
                    {
                        action = InventoryAction.NOTHING;
                    }
                }
                else
                {
                    if(!hotBarNull || !selectedNull)
                    {
                        action = InventoryAction.HOTBAR_SWAP;
                    }
                    else
                    {
                        action = InventoryAction.NOTHING;
                    }
                }
            } break;
            case DOUBLE_CLICK:                                                                                          // DOUBLE CLICK //////////////////////////
            {
                action = InventoryAction.COLLECT_TO_CURSOR;
            } break;
            case DROP:                                                                                                  // DROP ONE //////////////////////////////
            {
                action = InventoryAction.DROP_ONE_SLOT;
            } break;
            case CONTROL_DROP:                                                                                          // DROP ALL //////////////////////////////
            {
                action = InventoryAction.DROP_ALL_SLOT;
            } break;
            case SWAP_OFFHAND:                                                                                          // SWAP OFFHAND (F) //////////////////////
            {
                ItemStack offhand = bottomInv.getItem(40);
                if(offhand != null && offhand.getType() == Material.AIR) offhand = null;
                int offhandMax = offhand == null ? 0 : StackUtils.getMaxAmount(plugin, offhand);
                int offhandAmt = offhand == null ? 0 : offhand.getAmount();
                boolean offhandNull = offhand == null;
                if(!selectedNull || !offhandNull)
                {
                    if(selectedAmt <= selectedMax && offhandAmt <= offhandMax)
                    {
                        action = InventoryAction.HOTBAR_SWAP;
                    }
                    else
                    {
                        action = InventoryAction.NOTHING;
                    }
                }
                else
                {
                    action = InventoryAction.NOTHING;
                }
            } break;
        }

        player.sendMessage("Action NEW: " + action);
    }
}
