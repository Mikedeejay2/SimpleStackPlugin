package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype.*;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;

public class ProcessInvType implements ItemClickProcess
{
    protected final Simplestack plugin;

    protected List<ItemClickProcess> chestProcesses;
    protected List<ItemClickProcess> dispenserProcesses;
    protected List<ItemClickProcess> furnaceProcesses;
    protected List<ItemClickProcess> craftingProcesses;
    protected List<ItemClickProcess> enchantingProcesses;
    protected List<ItemClickProcess> brewingProcesses;
    protected List<ItemClickProcess> merchantProcesses;
    protected List<ItemClickProcess> anvilProcesses;
    protected List<ItemClickProcess> smithingProcesses;
    protected List<ItemClickProcess> beaconProcesses;
    protected List<ItemClickProcess> loomProcesses;
    protected List<ItemClickProcess> cartographyProcesses;
    protected List<ItemClickProcess> grindstoneProcesses;
    protected List<ItemClickProcess> stonecutterProcesses;

    public ProcessInvType(Simplestack plugin)
    {
        this.plugin = plugin;
        chestProcesses = new ArrayList<>(1);
        dispenserProcesses = new ArrayList<>(1);
        furnaceProcesses = new ArrayList<>(1);
        craftingProcesses = new ArrayList<>(1);
        enchantingProcesses = new ArrayList<>(1);
        brewingProcesses = new ArrayList<>(1);
        merchantProcesses = new ArrayList<>(1);
        anvilProcesses = new ArrayList<>(1);
        smithingProcesses = new ArrayList<>(1);
        beaconProcesses = new ArrayList<>(1);
        loomProcesses = new ArrayList<>(1);
        cartographyProcesses = new ArrayList<>(1);
        grindstoneProcesses = new ArrayList<>(1);
        stonecutterProcesses = new ArrayList<>(1);
    }

    public void initDefault()
    {
        addProcess(InventoryType.CRAFTING, new ProcessCrafting());
        addProcess(InventoryType.MERCHANT, new ProcessMerchant());
        addProcess(InventoryType.ANVIL, new ProcessAnvil());
        addProcess(InventoryType.SMITHING, new ProcessSmithing());
        addProcess(InventoryType.CARTOGRAPHY, new ProcessCartography());
        addProcess(InventoryType.GRINDSTONE, new ProcessGrindstone());
        addProcess(InventoryType.STONECUTTER, new ProcessStonecutter());
    }

    public ProcessInvType addProcess(InventoryType type, ItemClickProcess process)
    {
        switch(type)
        {
            case CHEST:
            case ENDER_CHEST:
            case BARREL:
            case SHULKER_BOX:
            case HOPPER:
                chestProcesses.add(process);
                break;
            case DISPENSER:
            case DROPPER:
                dispenserProcesses.add(process);
                break;
            case FURNACE:
            case BLAST_FURNACE:
            case SMOKER:
                furnaceProcesses.add(process);
                break;
            case WORKBENCH:
            case CRAFTING:
                craftingProcesses.add(process);
                break;
            case ENCHANTING:
                enchantingProcesses.add(process);
                break;
            case BREWING:
                brewingProcesses.add(process);
                break;
            case MERCHANT:
                merchantProcesses.add(process);
                break;
            case ANVIL:
                anvilProcesses.add(process);
                break;
            case SMITHING:
                smithingProcesses.add(process);
                break;
            case BEACON:
                beaconProcesses.add(process);
                break;
            case LOOM:
                loomProcesses.add(process);
                break;
            case CARTOGRAPHY:
                cartographyProcesses.add(process);
                break;
            case GRINDSTONE:
                grindstoneProcesses.add(process);
                break;
            case STONECUTTER:
                stonecutterProcesses.add(process);
                break;
        }
        return this;
    }

    @Override
    public void invoke(ItemClickInfo info)
    {
        switch(info.invView.getType())
        {
            case CHEST:
            case ENDER_CHEST:
            case BARREL:
            case SHULKER_BOX:
            case HOPPER:
                chestProcesses.forEach(process -> process.invoke(info));
                break;
            case DISPENSER:
            case DROPPER:
                dispenserProcesses.forEach(process -> process.invoke(info));
                break;
            case FURNACE:
            case BLAST_FURNACE:
            case SMOKER:
                furnaceProcesses.forEach(process -> process.invoke(info));
                break;
            case WORKBENCH:
            case CRAFTING:
                craftingProcesses.forEach(process -> process.invoke(info));
                break;
            case ENCHANTING:
                enchantingProcesses.forEach(process -> process.invoke(info));
                break;
            case BREWING:
                brewingProcesses.forEach(process -> process.invoke(info));
                break;
            case MERCHANT:
                merchantProcesses.forEach(process -> process.invoke(info));
                break;
            case ANVIL:
                anvilProcesses.forEach(process -> process.invoke(info));
                break;
            case SMITHING:
                smithingProcesses.forEach(process -> process.invoke(info));
                break;
            case BEACON:
                beaconProcesses.forEach(process -> process.invoke(info));
                break;
            case LOOM:
                loomProcesses.forEach(process -> process.invoke(info));
                break;
            case CARTOGRAPHY:
                cartographyProcesses.forEach(process -> process.invoke(info));
                break;
            case GRINDSTONE:
                grindstoneProcesses.forEach(process -> process.invoke(info));
                break;
            case STONECUTTER:
                stonecutterProcesses.forEach(process -> process.invoke(info));
                break;
        }
    }
}
