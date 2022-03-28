package com.mikedeejay2.simplestack.asm.test;

import com.mikedeejay2.simplestack.asm.LateBindAttacher;
import com.mikedeejay2.simplestack.asm.SimpleStackAgent;

public class MainTest {
    public static void main(String[] args) {
        try {
            LateBindAttacher.attachAgentToJVM(new Class<?>[] {
                SimpleStackAgent.class,
                LateBindAttacher.class,
                TestProfile.class,
                ProfileClassVisitor.class,
                ProfileMethodVisitor.class
            }, LateBindAttacher.getPidFromRuntimeBean());
        } catch(Exception e) {
            e.printStackTrace();
        }

        sayHello(5);
        sayWorld();
    }

    public static void sayHello(int s) {
        System.out.println("Hello");
    }

    public static void sayWorld() {
        System.out.println("World");
    }
}
