package com.mikedeejay2.simplestack.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public interface SimpleStackConfig {
    int getAmount(@NotNull Material type);
    int getAmount(@NotNull ItemStack item);
    int getUniqueItemAmount(@NotNull ItemStack item);

    boolean containsMaterial(@NotNull Material material);
    boolean containsCustomAmount(@NotNull Material material);
    boolean containsUniqueItem(@NotNull ItemStack item);

    @NotNull Set<Material> getMaterials();
    @NotNull Map<Material, Integer> getItemAmounts();
    @NotNull Set<ItemStack> getUniqueItems();

    void addMaterial(@NotNull Material material);
    void addCustomAmount(@NotNull Material material, int amount);
    void addUniqueItem(@NotNull ItemStack item);

    void removeMaterial(@NotNull Material material);
    void removeCustomAmount(@NotNull Material material);
    void removeUniqueItem(@NotNull ItemStack item);

    boolean isWhitelist();
    void setListMode(boolean whitelist);

    boolean isStackedArmorWearable();
    void setStackedArmorWearable(boolean stackedArmorWearable);

    int getMaxAmount();
    void setMaxAmount(int maxAmount);

    @NotNull String getLocale();
    void setLocale(@NotNull String newLocale);

    boolean isModified();
    void setModified(boolean modified);
}
