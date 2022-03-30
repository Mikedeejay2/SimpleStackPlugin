package com.mikedeejay2.simplestack.asm;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * @see <a href=https://bukkit.org/threads/tutorial-extreme-beyond-reflection-asm-replacing-loaded-classes.99376/>https://bukkit.org/threads/tutorial-extreme-beyond-reflection-asm-replacing-loaded-classes.99376/</a>
 */
public class LateBindAttacher {
    public static String getPidFromRuntimeBean() {
        String jvm = ManagementFactory.getRuntimeMXBean().getName();
        return jvm.substring(0, jvm.indexOf('@'));
    }

    public static void attachAgentToJVM(Class<?>[] agentClasses, String JVMPid)
        throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        final File jarFile = File.createTempFile("agent", ".jar");
        jarFile.deleteOnExit();

        final Manifest manifest = new Manifest();
        final Attributes mainAttributes = manifest.getMainAttributes();
        mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        mainAttributes.put(new Attributes.Name("Agent-Class"), SimpleStackAgent.class.getName());
        mainAttributes.put(new Attributes.Name("Can-Retransform-Classes"), "true");
        mainAttributes.put(new Attributes.Name("Can-Redefine-Classes"), "true");
        final JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), manifest);

        for(Class<?> clazz : agentClasses) {
            final JarEntry agent = new JarEntry(clazz.getName().replace('.', '/') + ".class");
            jos.putNextEntry(agent);

            jos.write(ByteUtils.getBytesFromClass(clazz));
            jos.closeEntry();
        }

        VirtualMachine vm = VirtualMachine.attach(JVMPid);
        vm.loadAgent(jarFile.getAbsolutePath());
        vm.detach();
    }
}
