package com.mikedeejay2.simplestack.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.PlaceholderFormatter;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.mikedeejay2lib.util.item.FastItemMeta;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.ItemMatcher;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class ItemConfigValue {
    public static final String DATA_KEY = "item_config_value";

    protected final Map<ItemMatcher, ItemMatcherImpl> matchers = new EnumMap<>(ItemMatcher.class);
    protected final Set<ItemMatcherImpl> optimizedMatchers = new ReferenceLinkedOpenHashSet<>();

    protected final ItemProperties properties;

    protected ItemConfigValue(ItemProperties item, Set<ItemMatcherImpl> matchers) {
        this.properties = item;
        this.matchers.putAll(matchers.stream().collect(
            Collectors.toMap(ItemMatcherImpl::getMatcherType, value -> value)));
        buildOptimizedMatchers();
    }

    public ItemConfigValue(ItemStack properties, ItemMatcherImpl... matchers) {
        this(new ItemProperties(properties), ImmutableSet.copyOf(matchers));
    }

    public ItemConfigValue(ItemStack properties, Collection<ItemMatcherImpl> matchers) {
        this(new ItemProperties(properties), ImmutableSet.copyOf(matchers));
    }

    public boolean matchItem(ItemStack item) {
        final Material material = item.getType();
        final ItemMeta itemMeta = FastItemMeta.getItemMeta(item);
        for(ItemMatcherImpl check : optimizedMatchers) {
            if(!check.check(material, itemMeta, properties)) return false;
        }
        return true;
    }

    public ItemBuilder asItemBuilder() {
        ItemBuilder builder = ItemBuilder.of(Base64Head.QUESTION_MARK_CYAN.get())
            .setAmount(properties.getAmount())
            .setName(Text.of("&r&f").concat("Any Item").color()); // TODO: Localization;
        for(ItemMatcherImpl matcher : matchers.values()) {
            builder = matcher.addToItem(builder, properties);
        }
        builder.addLore("");
        if(!matchers.isEmpty()) {
            builder = builder.addLore(Text.of("&7").concat("Matches").concat(": ").color()) // TODO: Localization
                .addLoreText(
                    matchers.values().stream()
                        .map(ItemMatcherImpl::getNameKey)
                        .map(Text::of)
                        .map(text -> Text.of("&a • ").concat(text).color())
                        .collect(Collectors.toList()));
        } else {
            builder = builder.addLore(Text.of("&c⚠ ").concat("No matchers on this item").color()); // TODO: Localization
        }
        return builder;
    }

    public ItemConfigValue addMatcher(ItemMatcher matcher) {
        matchers.put(matcher, ItemMatcherRegistry.ALL_MATCHERS.get(matcher));
        buildOptimizedMatchers();
        return this;
    }

    public ItemConfigValue removeMatcher(ItemMatcher matcher) {
        matchers.remove(matcher);
        buildOptimizedMatchers();
        return this;
    }

    public boolean containsMatcher(ItemMatcher matcher) {
        return matchers.containsKey(matcher);
    }

    private void buildOptimizedMatchers() {
        optimizedMatchers.clear();
        for(ItemMatcherImpl matcher : matchers.values()) {
            switch(matcher.getMatcherType()) {
                case MATERIAL: // Material will have already been checked
                    break;
                default:
                    optimizedMatchers.add(matcher);
            }
        }
    }

    public boolean canBeMetaMaterial() {
        return matchers.size() == 2 &&
            containsMatcher(ItemMatcher.MATERIAL) &&
            containsMatcher(ItemMatcher.ITEM_META) &&
            properties.hasItemMeta();
    }

    public boolean canBeMaterial() {
        return matchers.size() == 1 && containsMatcher(ItemMatcher.MATERIAL);
    }

    public boolean canBeMaterialValue() {
        return containsMatcher(ItemMatcher.MATERIAL);
    }

    public Material asMaterial() {
        return properties.getType();
    }

    public ItemProperties getItem() {
        return properties;
    }

    public int getAmount() {
        return properties.getAmount();
    }

    public Set<ItemMatcher> getMatchers() {
        return matchers.keySet();
    }

    public Map<ItemMatcher, ItemMatcherImpl> getMatchersMap() {
        return matchers;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + matchers.hashCode();
        hash = hash * 31 + properties.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ItemConfigValue)) return false;
        final ItemConfigValue other = (ItemConfigValue) obj;
        if(!matchers.equals(other.matchers)) return false;
        return properties.equals(other.properties);
    }

    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
            .put("item", properties.serialize())
            .put("matchers", matchers.values().stream()
                .map(v -> v.getMatcherType().toString())
                .collect(Collectors.toList()))
            .build();
    }

    public static ItemConfigValue deserialize(Map<String, Object> map) {
        if(!map.containsKey("item")) return null;
        if(!map.containsKey("matchers")) return null;
        return new ItemConfigValue(
            ItemProperties.deserialize((Map<String, Object>) map.get("item")),
            ((List<String>) map.get("matchers")).stream()
                .filter(str -> {
                    try {
                        ItemMatcher.valueOf(str);
                    } catch(IllegalArgumentException e) {
                        SimpleStack.getInstance().sendWarning(
                            Text.of("&c")
                                .concat("simplestack.warnings.invalid_item_matcher")
                                .placeholder(PlaceholderFormatter.of("matcher", str)));
                        return false;
                    }
                    return true;
                })
                .map(ItemMatcher::valueOf)
                .map(ItemMatcherRegistry.ALL_MATCHERS::get)
                .collect(Collectors.toSet()));
    }

    @Override
    public String toString() {
        return "ItemConfigValue{" +
            "matchers=" + matchers +
            ", optimizedMatchers=" + optimizedMatchers +
            ", properties=" + properties +
            '}';
    }
}
