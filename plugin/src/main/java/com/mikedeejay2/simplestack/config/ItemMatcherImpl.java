package com.mikedeejay2.simplestack.config;

import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.simplestack.api.ItemMatcher;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemMatcherImpl {
    private final ItemMatcher matcherType;
    private String nameKey;
    private final List<String> descriptionKeys;

    ItemMatcherImpl(ItemMatcher matcherType) {
        this.matcherType = matcherType;
        this.descriptionKeys = new ArrayList<>();
        ItemMatcherRegistry.ALL_MATCHERS.put(matcherType, this);
    }

    public ItemMatcher getMatcherType() {
        return matcherType;
    }

    public String getNameKey() {
        return nameKey;
    }

    public List<String> getDescriptionKeys() {
        return descriptionKeys;
    }

    public ItemMatcherImpl setNameKey(String nameKey) {
        this.nameKey = nameKey;
        return this;
    }

    public ItemMatcherImpl addDescriptionKey(String descriptionKey) {
        this.descriptionKeys.add(descriptionKey);
        return this;
    }

    public abstract boolean check(Material material, ItemMeta itemMeta, ItemProperties properties);

    public abstract ItemBuilder addToItem(ItemBuilder builder, ItemProperties properties);

    public abstract boolean compatible(ItemProperties properties);

    public abstract boolean incompatible(ItemConfigValue configValue);

    public static abstract class Meta<I extends ItemMeta> extends ItemMatcherImpl {
        protected final Class<I> itemMetaClass;

        Meta(Class<I> itemMetaClass, ItemMatcher matcherType) {
            super(matcherType);
            this.itemMetaClass = itemMetaClass;
        }

        @Override
        public boolean check(Material material, ItemMeta itemMeta, ItemProperties properties) {
            return itemMeta != null && itemMetaClass.isAssignableFrom(itemMeta.getClass()) &&
                check(itemMetaClass.cast(itemMeta),
                      itemMetaClass.cast(properties.getItemMeta()));
        }

        @Override
        public ItemBuilder addToItem(ItemBuilder builder, ItemProperties properties) {
            final ItemMeta builderMeta = builder.getMeta();
            if(!itemMetaClass.isAssignableFrom(builderMeta.getClass())) return builder;
            final I castedBuilderMeta = itemMetaClass.cast(builderMeta);
            final I propertyMeta = itemMetaClass.cast(properties.getItemMeta());
            addToItemMeta(castedBuilderMeta, propertyMeta);
            return builder.setMeta(propertyMeta);
        }

        @Override
        public boolean compatible(ItemProperties properties) {
            return properties.hasItemMeta() &&
                itemMetaClass.isAssignableFrom(properties.getItemMeta().getClass());
        }

        @Override
        public Meta<I> setNameKey(String nameKey) {
            super.setNameKey(nameKey);
            return this;
        }

        @Override
        public Meta<I> addDescriptionKey(String descriptionKey) {
            super.addDescriptionKey(descriptionKey);
            return this;
        }

        public abstract boolean check(I itemMeta, I propertiesMeta);

        public abstract void addToItemMeta(I builderMeta, I configMeta);

        public static class Dynamic<I extends ItemMeta> extends Meta<I> {
            private CheckFunction<I> checkFunction;
            private AddToItemMetaFunction<I> addToItemMetaFunction;
            private IncompatibleFunction<I> incompatibleFunction;

            Dynamic(Class<I> itemMetaClass, ItemMatcher matcherType) {
                super(itemMetaClass, matcherType);
            }

            public Meta.Dynamic<I> setCheckFunction(CheckFunction<I> checkFunction) {
                this.checkFunction = checkFunction;
                return this;
            }

            public Meta.Dynamic<I> setAddToItemMetaFunction(AddToItemMetaFunction<I> addToItemMetaFunction) {
                this.addToItemMetaFunction = addToItemMetaFunction;
                return this;
            }

            public Meta.Dynamic<I> setIncompatibleFunction(IncompatibleFunction<I> incompatibleFunction) {
                this.incompatibleFunction = incompatibleFunction;
                return this;
            }

            @Override
            public boolean check(I itemMeta, I propertiesMeta) {
                Validate.notNull(checkFunction, "Check function not set for matcher %s", getMatcherType());
                return checkFunction.check(itemMeta, propertiesMeta);
            }

            @Override
            public void addToItemMeta(I builderMeta, I configMeta) {
                if(addToItemMetaFunction != null) addToItemMetaFunction.addToItemMeta(builderMeta, configMeta);
            }

            @Override
            public boolean incompatible(ItemConfigValue configValue) {
                return incompatibleFunction != null && incompatibleFunction.incompatible(configValue);
            }

            @Override
            public Meta.Dynamic<I> setNameKey(String nameKey) {
                super.setNameKey(nameKey);
                return this;
            }

            @Override
            public Meta.Dynamic<I> addDescriptionKey(String descriptionKey) {
                super.addDescriptionKey(descriptionKey);
                return this;
            }

            @FunctionalInterface
            public interface CheckFunction<I extends ItemMeta> {
                boolean check(I itemMeta, I propertiesMeta);
            }

            @FunctionalInterface
            public interface AddToItemMetaFunction<I extends ItemMeta> {
                void addToItemMeta(I builderMeta, I configMeta);
            }

            @FunctionalInterface
            public interface IncompatibleFunction<I extends ItemMeta> {
                boolean incompatible(ItemConfigValue configValue);
            }
        }
    }

    public static class Dynamic extends ItemMatcherImpl {
        private CheckFunction checkFunction;
        private AddToItemFunction addToItemFunction;
        private CompatibleFunction compatibleFunction;
        private IncompatibleFunction incompatibleFunction;

        Dynamic(ItemMatcher matcherType) {
            super(matcherType);
        }

        public Dynamic setCheck(CheckFunction checkFunction) {
            this.checkFunction = checkFunction;
            return this;
        }

        public Dynamic setAddToItem(AddToItemFunction addToItemFunction) {
            this.addToItemFunction = addToItemFunction;
            return this;
        }

        public Dynamic setCompatible(CompatibleFunction compatibleFunction) {
            this.compatibleFunction = compatibleFunction;
            return this;
        }

        public Dynamic setIncompatible(IncompatibleFunction incompatibleFunction) {
            this.incompatibleFunction = incompatibleFunction;
            return this;
        }

        @Override
        public Dynamic setNameKey(String nameKey) {
            super.setNameKey(nameKey);
            return this;
        }

        @Override
        public Dynamic addDescriptionKey(String descriptionKey) {
            super.addDescriptionKey(descriptionKey);
            return this;
        }

        @Override
        public boolean check(Material material, ItemMeta itemMeta, ItemProperties properties) {
            Validate.notNull(checkFunction, "Check function not set for matcher %s", getMatcherType());
            return checkFunction.check(material, itemMeta, properties);
        }

        @Override
        public ItemBuilder addToItem(ItemBuilder builder, ItemProperties properties) {
            return addToItemFunction != null ? addToItemFunction.addToItem(builder, properties) : builder;
        }

        @Override
        public boolean compatible(ItemProperties properties) {
            return compatibleFunction == null || compatibleFunction.compatible(properties);
        }

        @Override
        public boolean incompatible(ItemConfigValue configValue) {
            return incompatibleFunction != null && incompatibleFunction.incompatible(configValue);
        }

        @FunctionalInterface
        public interface CheckFunction {
            boolean check(Material material, ItemMeta itemMeta, ItemProperties properties);
        }

        @FunctionalInterface
        public interface AddToItemFunction {
            ItemBuilder addToItem(ItemBuilder builder, ItemProperties properties);
        }

        @FunctionalInterface
        public interface CompatibleFunction {
            boolean compatible(ItemProperties properties);
        }

        @FunctionalInterface
        public interface IncompatibleFunction {
            boolean incompatible(ItemConfigValue configValue);
        }
    }
}
