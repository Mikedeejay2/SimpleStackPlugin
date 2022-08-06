package com.mikedeejay2.simplestack;

import com.mikedeejay2.mikedeejay2lib.util.version.MinecraftVersion;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Map;

public class MappingsLookup {
    private static MappingsHolder holder = null;
    private static ClassMappings lastClass = null;
    private static MappingEntry lastMethod = null;
    private static MappingEntry lastField = null;

    static {
        register("1.19", new MappingsHolder()
            .add("Item", ClassMappings.of("net.minecraft.world.item.Item")
                .method("getMaxStackSize", MappingEntry.of("m")))
            .add("ItemStack", ClassMappings.of("net.minecraft.world.item.ItemStack")
                .method("getMaxStackSize", MappingEntry.of("f").descriptor("()I"))
                .method("split", MappingEntry.of("a").descriptor("(I)L%s;", "ItemStack"))
                .method("copy", MappingEntry.of("o").descriptor("()L%s;", "ItemStack"))
                .method("setCount", MappingEntry.of("e").descriptor("(I)V"))
                .method("shrink", MappingEntry.of("g").descriptor("(I)V")))
            .add("CraftItemStack", ClassMappings.of("org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack")
                .method("asBukkitCopy", MappingEntry.of("asBukkitCopy")))
            .add("ContainerUtil", ClassMappings.of("net.minecraft.world.ContainerUtil")
                .method("removeItem", MappingEntry.of("a")))
            .add("Slot", ClassMappings.of("net.minecraft.world.inventory.Slot")
                .method("tryRemove", MappingEntry.of("a"))
                .method("getItem", MappingEntry.of("e").descriptor("()L%s;", "ItemStack")))
        );
    }

    private static void register(String version, MappingsHolder holder) {
        if(version.equals(MinecraftVersion.getVersionString())) {
            MappingsLookup.holder = holder;
        }
    }

    public static boolean hasMappings() {
        return holder != null;
    }

    public static ClassMappings nms(String name) {
        return holder.clazz(name);
    }

    public static ClassMappings lastNms() {
        Validate.notNull(lastClass, "Tried to get last class, but was null");
        return lastClass;
    }

    public static MappingEntry lastNmsMethod() {
        Validate.notNull(lastClass, "Tried to get last method, but was null");
        return lastMethod;
    }

    public static MappingEntry lastNmsField() {
        Validate.notNull(lastClass, "Tried to get last field, but was null");
        return lastField;
    }

    public static final class MappingsHolder {
        private final Map<String, ClassMappings> mappings;

        private MappingsHolder() {
            this.mappings = new HashMap<>();
        }

        private MappingsHolder add(String name, ClassMappings mappings) {
            this.mappings.put(name, mappings);
            return this;
        }

        public ClassMappings clazz(String name) {
            Validate.isTrue(mappings.containsKey(name),
                            String.format("Tried to get invalid class mapping \"%s\"", name));
            return (lastClass = mappings.get(name));
        }
    }

    public static final class ClassMappings {
        private final String qualifiedName;
        private final String internalName;
        private final String descriptorName;
        private final Map<String, MappingEntry> methodMappings;
        private final Map<String, MappingEntry> fieldMappings;

        private ClassMappings(String qualifiedName) {
            this.qualifiedName = qualifiedName;
            this.internalName = qualifiedName.replace('.', '/');
            this.descriptorName = "L" + internalName + ";";
            this.methodMappings = new HashMap<>();
            this.fieldMappings = new HashMap<>();
        }

        private ClassMappings method(String name, MappingEntry entry) {
            methodMappings.put(name, entry);
            return this;
        }

        private ClassMappings field(String name, MappingEntry entry) {
            fieldMappings.put(name, entry);
            return this;
        }

        public String qualifiedName() {
            return qualifiedName;
        }

        public String internalName() {
            return internalName;
        }

        public String descriptorName() {
            return descriptorName;
        }

        public MappingEntry method(String name) {
            Validate.isTrue(methodMappings.containsKey(name),
                            String.format("Tried to get invalid method mapping \"%s\"", name));
            return (lastMethod = methodMappings.get(name));
        }

        public MappingEntry field(String name) {
            Validate.isTrue(methodMappings.containsKey(name),
                            String.format("Tried to get invalid field mapping \"%s\"", name));
            return (lastField = fieldMappings.get(name));
        }

        public Class<?> toClass() {
            try {
                return Class.forName(qualifiedName);
            } catch(ClassNotFoundException e) {
                throw new RuntimeException(String.format("Could not get class of name \"%s\"", qualifiedName), e);
            }
        }

        private static ClassMappings of(String qualifiedName) {
            return new ClassMappings(qualifiedName);
        }
    }

    public static final class MappingEntry {
        private final String name;
        private ClassMappings owner;
        private String descriptor; // nullable
        private String[] descriptorLookups;

        private MappingEntry(String name) {
            this.name = name;
        }

        private MappingEntry descriptor(String descriptor, String... referenceClasses) {
            this.descriptorLookups = referenceClasses;
            this.descriptor = descriptor;
            return this;
        }

        private MappingEntry owner(ClassMappings owner) {
            this.owner = owner;
            return this;
        }

        public String name() {
            return name;
        }

        public ClassMappings owner() {
            return owner;
        }

        public String descriptor() {
            Validate.notNull(descriptor,
                            String.format("Tried to get invalid descriptor \"%s\"", name));
            if(descriptorLookups != null) {
                String[] classes = new String[descriptorLookups.length];
                for(int i = 0; i < descriptorLookups.length; i++) {
                    classes[i] = nms(descriptorLookups[i]).internalName();
                }
                return String.format(descriptor, (Object[]) classes);
            }
            return descriptor;
        }

        private static MappingEntry of(String name) {
            return new MappingEntry(name);
        }
    }
}
