package com.mikedeejay2.simplestack;

import com.mikedeejay2.mikedeejay2lib.BukkitPlugin;
import com.mikedeejay2.mikedeejay2lib.commands.CommandManager;
import com.mikedeejay2.mikedeejay2lib.text.language.LangManager;
import com.mikedeejay2.mikedeejay2lib.util.bstats.BStats;
import com.mikedeejay2.mikedeejay2lib.util.update.UpdateChecker;
import com.mikedeejay2.mikedeejay2lib.util.version.MinecraftVersion;
import com.mikedeejay2.simplestack.bytebuddy.ByteBuddyHolder;
import com.mikedeejay2.simplestack.bytebuddy.StackSizeTransformer;
import com.mikedeejay2.simplestack.commands.*;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.config.DebugConfig;

/**
 * Simple Stack v2.0 plugin for Minecraft TBD - 1.19
 * <p>
 * The source code for Simple Stack can be found here:
 * <a href="https://github.com/Mikedeejay2/SimpleStackPlugin">https://github.com/Mikedeejay2/SimpleStackPlugin</a>
 * </p>
 * <p>
 * Simple Stack is powered by Mikedeejay2Lib.
 * </p>
 * <p>
 * The source code for Mikedeejay2Lib can be found here:
 * <a href="https://github.com/Mikedeejay2/Mikedeejay2Lib">https://github.com/Mikedeejay2/Mikedeejay2Lib</a>
 * </p>
 *
 * @author Mikedeejay2
 */
public final class SimpleStack extends BukkitPlugin {
    private static SimpleStack instance;

    private static final int MINIMUM_VERSION = 14;

    // Permission for general Simple Stack use
    private final String permission = "simplestack.use";

    // The config of Simple Stack which stores all customizable data
    private Config config;
    private DebugConfig debugConfig;

    private BStats bStats;
    private UpdateChecker updateChecker;
    private LangManager langManager;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;

        setPrefix("&b[&9" + this.getDescription().getName() + "&b]&r ");

        if(checkVersion() || installByteBuddyAgent()) return;

        this.langManager = new LangManager(this, "lang");

        this.bStats = new BStats(this);
        this.bStats.init(9379);
        this.updateChecker = new UpdateChecker(this);
        this.updateChecker.init("Mikedeejay2", "SimpleStackPlugin");
        this.updateChecker.checkForUpdates(10);

        this.commandManager = new CommandManager(this, "simplestack");

        this.commandManager.addSubcommand(new HelpCommand(this));
        this.commandManager.addSubcommand(new ReloadCommand(this));
        this.commandManager.addSubcommand(new ResetCommand(this));
        this.commandManager.addSubcommand(new SetAmountCommand(this));
        this.commandManager.addSubcommand(new ConfigCommand(this));
        registerCommand(commandManager);

        this.config = new Config(this);
        this.debugConfig = new DebugConfig();

        StackSizeTransformer.installAgents();
    }

    private boolean installByteBuddyAgent() {
        if(ByteBuddyHolder.initialize()) {
            sendSevere("SimpleStack is not compatible with this installation of Java!");
            sendSevere("Common solutions are to use Java 9 or greater OR use a Java 8 JDK that includes the required instrumentation toolkit");
            disablePlugin(this);
            return true;
        }
        return false;
    }

    /**
     * Helper method to check the Minecraft version that the Minecraft server is running on.
     * <p>
     * If the version is not compatible, disable this plugin.
     *
     * @return Whether the plugin has been disabled.
     */
    private boolean checkVersion() {
        if(MinecraftVersion.getVersionShort() < MINIMUM_VERSION || !NMSMappings.hasMappings()) {
            sendSevere(String.format(
                "Simple Stack %s is not compatible Minecraft version %s!",
                this.getDescription().getVersion(),
                MinecraftVersion.getVersionString()));
            disablePlugin(this);
            return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if(config.isModified()) {
            config.saveToDisk(true);
        }
    }

    /**
     * Get the simplestack.use permission as a string
     *
     * @return simplestack.use String permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Get the Config file for Simple Stack
     *
     * @return The config of Simple Stack
     */
    public Config config() {
        return config;
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public DebugConfig getDebugConfig() {
        return debugConfig;
    }

    public static SimpleStack getInstance() {
        return instance;
    }
}
