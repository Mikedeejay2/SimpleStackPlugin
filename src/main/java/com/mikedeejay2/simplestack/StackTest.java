package com.mikedeejay2.simplestack;

import com.mikedeejay2.mikedeejay2lib.reflect.Reflector;
import com.mikedeejay2.mikedeejay2lib.reflect.ReflectorField;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class StackTest {
    private static final String STONE = "b";
    private static final String maxStackSize = "d";

    public StackTest() {
        Object stone = Reflector.of("net.minecraft.world.item.Items").field(STONE).get(null);
        Reflector.of("net.minecraft.world.item.Item").field(maxStackSize).set(stone, 8);
    }
}
