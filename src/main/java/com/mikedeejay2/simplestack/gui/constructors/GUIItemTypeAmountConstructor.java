package com.mikedeejay2.simplestack.gui.constructors;

import com.google.common.collect.ImmutableSet;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractType;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractExecutorList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.list.GUIInteractHandlerList;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractExecutorDefaultInv;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;

public class GUIItemTypeAmountConstructor extends GUIAbstractListConstructor<Map.Entry<Material, Integer>> {
    private static final Function<Map.Entry<Material, Integer>, GUIItem> MAPPER =
        (pair) -> new GUIItem(new ItemStack(pair.getKey(), pair.getValue())).setMovable(true);
    private static final Function<GUIItem, Map.Entry<Material, Integer>> UNMAPPER =
        (item) -> new AbstractMap.SimpleEntry<>(item.getType(), item.getAmount());

    public static final GUIItemTypeAmountConstructor INSTANCE = new GUIItemTypeAmountConstructor(SimpleStack.getInstance());

    private GUIItemTypeAmountConstructor(SimpleStack plugin) {
        super(plugin, Text.of("simplestack.gui.item_type_amts.title"), 6, MAPPER, UNMAPPER);
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = super.get();
        GUIInteractHandler interaction = new GUIInteractHandlerList(64);
        interaction.resetExecutors();
        interaction.addExecutor(new GUIInteractExecutorDefaultInv(64));
        interaction.addExecutor(new GUIInteractExecutorList(GUIInteractType.SINGLE_MATERIAL, 64, false));
        gui.setDefaultMoveState(true);
        gui.setInteractionHandler(interaction);
        return gui;
    }

    @Override
    protected Collection<Map.Entry<Material, Integer>> getUnmappedList() {
        return new MappedSet<>(plugin.config().getItemAmounts());
    }

    private static final class MappedSet<E, T> implements Set<Map.Entry<E, T>> {
        private final Map<E, T> map;

        private MappedSet(Map<E, T> map) {
            this.map = map;
        }

        @Override public int size() {return map.size();}
        @Override public boolean isEmpty() {return map.isEmpty();}
        @Override public boolean contains(Object o) {return map.containsKey(o);}
        @Override public Iterator<Map.Entry<E, T>> iterator() {return map.entrySet().iterator();}
        @Override public Object[] toArray() {return map.entrySet().toArray();}
        @Override public <Z> Z[] toArray(Z[] a) {return map.entrySet().toArray(a);}
        @Override public boolean add(Map.Entry<E, T> etEntry) {
            map.put(etEntry.getKey(), etEntry.getValue());
            return true;
        }
        @Override public boolean remove(Object o) {return map.remove(o) != null;}
        @Override public boolean containsAll(Collection<?> c) {
            for(Object o : c) {
                if(!map.containsKey(o)) return false;
            }
            return true;
        }
        @Override public boolean addAll(Collection<? extends Map.Entry<E, T>> c) {
            final Map<E, T> newMap = new HashMap<>();
            for(Map.Entry<E, T> entry : c) {
                newMap.put(entry.getKey(), entry.getValue());
            }
            map.putAll(newMap);
            return true;
        }
        @Override public boolean retainAll(Collection<?> c) {
            for(E e : ImmutableSet.copyOf(map.keySet())) {
                if(!c.contains(e)) map.remove(e);
            }
            return true;
        }
        @Override public boolean removeAll(Collection<?> c) {
            for(Object o : c) {
                map.remove(o);
            }
            return true;
        }
        @Override public void clear() {map.clear();}
    }
}
