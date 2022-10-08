package com.mikedeejay2.simplestack.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public interface SimpleStackConfig {
    int getAmount(Material type);
    int getAmount(ItemStack item);
    int getUniqueItemAmount(ItemStack item);

    boolean containsMaterial(Material material);
    boolean containsCustomAmount(Material material);
    boolean containsUniqueItem(ItemStack item);

    Set<Material> getMaterials();
    Map<Material, Integer> getItemAmounts();
    Set<ItemStack> getUniqueItems();

    void addMaterial(Material material);
    void addCustomAmount(Material material, int amount);
    void addUniqueItem(ItemStack item);

    void removeMaterial(Material material);
    void removeCustomAmount(Material material);
    void removeUniqueItem(ItemStack item);

    boolean isWhitelist();
    void setListMode(boolean whitelist);

    boolean isStackedArmorWearable();
    void setStackedArmorWearable(boolean stackedArmorWearable);

    int getMaxAmount();
    void setMaxAmount(int maxAmount);

    String getLocale();
    void setLocale(String newLocale);

    boolean isModified();
    void setModified(boolean modified);
}
