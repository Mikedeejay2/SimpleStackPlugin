package com.mikedeejay2.simplestack.config;

import com.google.common.collect.ImmutableMap;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.PlaceholderFormatter;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.item.FastItemMeta;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.ImmutablePair;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.Pair;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.MatcherDataType;
import com.mikedeejay2.simplestack.api.MatcherOperatorType;
import com.mikedeejay2.simplestack.config.matcher.*;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class ItemConfigValue {
    public static final String DATA_KEY = "item_config_value";

    protected final Map<MatcherDataType, MatcherExecutor<?, ?>> matchers = new EnumMap<>(MatcherDataType.class);
    protected final Set<MatcherExecutor<?, ?>> optimizedMatchers = new ReferenceLinkedOpenHashSet<>();

    protected final ItemProperties properties;

    public ItemConfigValue(ItemProperties item) {
        this.properties = item;
    }

    public ItemConfigValue(ItemProperties item, Map<MatcherDataType, MatcherExecutor<?, ?>> matchers) {
        this.properties = item;
        this.matchers.putAll(matchers);
    }

    public boolean matchItem(ItemStack item) {
        final ItemMeta itemMeta = FastItemMeta.getItemMeta(item);
        final ItemMeta propertiesMeta = properties.getItemMeta();
        for(MatcherExecutor<?, ?> executor : optimizedMatchers) {
            if(!executor.check(itemMeta, propertiesMeta)) return false;
        }
        return true;
    }

    public ItemBuilder asItemBuilder() {
        // TODO: This, use MatcherItemBuilder
//        ItemBuilder builder = ItemBuilder.of(Base64Head.QUESTION_MARK_CYAN.get())
//            .setAmount(properties.getAmount())
//            .setName(Text.of("&r&f").concat("Any Item").color()); // TODO: Localization;
//        for(MatcherExecutor<?, ?> matcher : matchers.values()) {
//            builder = matcher.addToItem(builder, properties);
//        }
//        builder.addLore("");
//        if(!matchers.isEmpty()) {
//            builder = builder.addLore(Text.of("&7").concat("Matches").concat(": ").color()) // TODO: Localization
//                .addLoreText(
//                    matchers.values().stream()
//                        .map(MatcherExecutor::getNameKey)
//                        .map(Text::of)
//                        .map(text -> Text.of("&a • ").concat(text).color())
//                        .collect(Collectors.toList()));
//        } else {
//            builder = builder.addLore(Text.of("&c⚠ ").concat("No matchers on this item").color()); // TODO: Localization
//        }
//        return builder;
        return ItemBuilder.of(properties.asItemStack());
    }

    public ItemConfigValue addMatcher(MatcherDataType dataType, MatcherOperatorType operatorType) {
        final MatcherDataSource<?, ?> dataSource = MatcherDataSourceRegistry.get(dataType);
        final MatcherOperator<?> operator = MatcherOperatorRegistry.get(operatorType);
        Validate.isTrue(dataSource.getItemMetaClass().isAssignableFrom(properties.hasItemMeta() ? properties.getItemMeta().getClass() : ItemMeta.class));
        Validate.isTrue(operator.getInputClass().isAssignableFrom(dataSource.getDataTypeClass()));
        // TODO: Fix raw use of parameterized class
        matchers.put(dataType, new MatcherExecutor<>(dataSource, operator, dataSource.getDataTypeClass()));
        buildOptimizedMatchers();
        return this;
    }

    public ItemConfigValue removeMatcher(MatcherDataType dataType) {
        matchers.remove(dataType);
        buildOptimizedMatchers();
        return this;
    }

    public boolean containsMatcher(MatcherDataType dataType) {
        return matchers.containsKey(dataType);
    }

    private void buildOptimizedMatchers() {
        optimizedMatchers.clear();
        for(MatcherExecutor<?, ?> matcher : matchers.values()) {
            if(matcher.getDataType() == MatcherDataType.MATERIAL) continue; // Material will have already been checked
            optimizedMatchers.add(matcher);
        }
    }

    public boolean canBeMetaMaterial() {
        return matchers.size() == 2 &&
            containsMatcher(MatcherDataType.MATERIAL) &&
            containsMatcher(MatcherDataType.ITEM_META) &&
            properties.hasItemMeta();
    }

    public boolean canBeMaterial() {
        return matchers.size() == 1 && containsMatcher(MatcherDataType.MATERIAL);
    }

    public boolean canBeMaterialValue() {
        return containsMatcher(MatcherDataType.MATERIAL);
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

    public Set<MatcherDataType> getMatcherDataTypes() {
        return matchers.keySet();
    }

    public Map<MatcherDataType, MatcherExecutor<?, ?>> getMatchersMap() {
        return matchers;
    }

    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
            .put("item", properties.serialize())
            .put("matchers", matchers.values().stream()
                .map(v -> new ImmutablePair<>(v.getDataType().name(), v.getOperatorType().name()))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight)))
            .build();
    }

    public static ItemConfigValue deserialize(Map<String, Object> map) {
        if(!map.containsKey("item")) return null;
        if(!map.containsKey("matchers")) return null;
        Object itemObj = map.get("item");
        Object matchersObj = map.get("matchers");
        if(!(itemObj instanceof Map)) {
            SimpleStack.getInstance().sendWarning(
                Text.of("&c")
                    .concat("Expected Map type for item data, received %type% instead, skipping")
                    .placeholder(PlaceholderFormatter.of("type", itemObj.getClass().getSimpleName())));
            return null;
        }
        if(!(matchersObj instanceof Map)) {
            SimpleStack.getInstance().sendWarning(
                Text.of("&c")
                    .concat("Expected Map type for matcher data, received %type% instead, skipping")
                    .placeholder(PlaceholderFormatter.of("type", matchersObj.getClass().getSimpleName())));
            return null;
        }
        Map<String, Object> item = (Map<String, Object>) itemObj;
        Map<String, String> matchers = (Map<String, String>) matchersObj;
        return new ItemConfigValue(
            ItemProperties.deserialize(item),
            matchers.entrySet().stream()
                .filter(kv -> {
                    try {
                        MatcherDataType.valueOf(kv.getKey());
                    } catch(IllegalArgumentException e) {
                        SimpleStack.getInstance().sendWarning(
                            Text.of("&c")
                                .concat("An item matcher data type named %type% is invalid, skipping")
                                .placeholder(PlaceholderFormatter.of("type", kv.getKey())));
                        return false;
                    }
                    try {
                        MatcherOperatorType.valueOf(kv.getValue());
                    } catch(IllegalArgumentException e) {
                        SimpleStack.getInstance().sendWarning(
                            Text.of("&c")
                                .concat("An item matcher operator type named %type% is invalid, skipping")
                                .placeholder(PlaceholderFormatter.of("type", kv.getValue())));
                        return false;
                    }
                    return true;
                })
                .map(kv -> new ImmutablePair<>(
                    MatcherDataSourceRegistry.get(MatcherDataType.valueOf(kv.getKey())),
                    MatcherOperatorRegistry.get(MatcherOperatorType.valueOf(kv.getValue()))))
                // TODO: Validate equal generic types here (.filter)
                .map(kv -> new MatcherExecutor<>(kv.getKey(), kv.getValue(), kv.getKey().getDataTypeClass()))
                .collect(Collectors.<MatcherExecutor<?, ?>, MatcherDataType, MatcherExecutor<?, ?>>toMap(
                    MatcherExecutor::getDataType, v -> v)));
    }

    @Override
    public String toString() {
        return "ItemConfigValue{" +
            "matchers=" + matchers +
            ", optimizedMatchers=" + optimizedMatchers +
            ", properties=" + properties +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        ItemConfigValue that = (ItemConfigValue) o;
        return Objects.equals(matchers, that.matchers) &&
            Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchers, properties);
    }
}
