package com.mikedeejay2.simplestack.asm;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @see <a href=https://bukkit.org/threads/tutorial-extreme-beyond-reflection-asm-replacing-loaded-classes.99376/>https://bukkit.org/threads/tutorial-extreme-beyond-reflection-asm-replacing-loaded-classes.99376/</a>
 */
public class ByteUtils {
    private ByteUtils() {
        throw new UnsupportedOperationException("ByteUtils cannot be instantiated");
    }

    public static byte[] getBytesFromIS(InputStream stream) {
        try {
            return IOUtils.toByteArray(stream);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getBytesFromClass(Class<?> clazz) {
        return getBytesFromIS(clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class"));
    }
}
