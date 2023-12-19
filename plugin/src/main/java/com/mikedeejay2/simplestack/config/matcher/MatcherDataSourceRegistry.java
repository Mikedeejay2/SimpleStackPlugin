package com.mikedeejay2.simplestack.config.matcher;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mikedeejay2.simplestack.api.MatcherDataType;
import com.mikedeejay2.simplestack.util.MultimapClassCollector;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public final class MatcherDataSourceRegistry {
    static final Multimap<Class<? extends ItemMeta>, MatcherDataSource<?, ?>> REGISTRY_BY_CLASS = LinkedHashMultimap.create();
    static final Map<MatcherDataType, MatcherDataSource<?, ?>> REGISTRY_BY_TYPE = new EnumMap<>(MatcherDataType.class);

    public static final MatcherDataSource<ItemMeta, Material> MATERIAL            = new MatcherDataSource<>(MatcherDataType.MATERIAL, ItemMeta.class, Material.class, itemMeta -> null); // Function will never be called, this is a placeholder
    public static final MatcherDataSource<ItemMeta, ItemMeta> ITEM_META           = new MatcherDataSource<>(MatcherDataType.ITEM_META, ItemMeta.class, ItemMeta.class, itemMeta -> itemMeta);
    public static final MatcherDataSource<ItemMeta, String>   NAME                = new MatcherDataSource<>(MatcherDataType.NAME, ItemMeta.class, String.class, itemMeta -> itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : null);
    public static final MatcherDataSource<ItemMeta, List>     LORE                = new MatcherDataSource<>(MatcherDataType.LORE, ItemMeta.class, List.class, itemMeta -> itemMeta.hasLore() ? itemMeta.getLore() : null);
    public static final MatcherDataSource<ItemMeta, Map>      ENCHANTMENTS        = new MatcherDataSource<>(MatcherDataType.ENCHANTMENTS, ItemMeta.class, Map.class, itemMeta -> itemMeta.hasEnchants() ? itemMeta.getEnchants() : null);
    public static final MatcherDataSource<ItemMeta, Set>      ITEM_FLAGS          = new MatcherDataSource<>(MatcherDataType.ITEM_FLAGS, ItemMeta.class, Set.class, ItemMeta::getItemFlags);
    public static final MatcherDataSource<ItemMeta, Boolean>  UNBREAKABLE         = new MatcherDataSource<>(MatcherDataType.UNBREAKABLE, ItemMeta.class, Boolean.class, ItemMeta::isUnbreakable);
    public static final MatcherDataSource<ItemMeta, Multimap> ATTRIBUTE_MODIFIERS = new MatcherDataSource<>(MatcherDataType.ATTRIBUTE_MODIFIERS, ItemMeta.class, Multimap.class, itemMeta -> itemMeta.hasAttributeModifiers() ? itemMeta.getAttributeModifiers() : null);

    public static MatcherDataSource<?, ?> get(MatcherDataType type) {
        return REGISTRY_BY_TYPE.get(type);
    }

    public static Set<MatcherDataSource<?, ?>> getDataSources(ItemMeta itemMeta) {
        Validate.notNull(itemMeta, "ItemMeta is null");
        return MultimapClassCollector.collect(REGISTRY_BY_CLASS, itemMeta.getClass());
    }

    private MatcherDataSourceRegistry() {
        throw new UnsupportedOperationException("MatcherDataSourceRegistry cannot be instantiated as an object");
    }
}
