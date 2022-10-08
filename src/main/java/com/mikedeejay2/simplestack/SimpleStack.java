package com.mikedeejay2.simplestack;

import com.mikedeejay2.mikedeejay2lib.BukkitPlugin;
import com.mikedeejay2.mikedeejay2lib.commands.CommandManager;
import com.mikedeejay2.mikedeejay2lib.reflect.Reflector;
import com.mikedeejay2.mikedeejay2lib.reflect.ReflectorClass;
import com.mikedeejay2.mikedeejay2lib.text.language.TranslationManager;
import com.mikedeejay2.mikedeejay2lib.util.bstats.BStats;
import com.mikedeejay2.mikedeejay2lib.util.update.UpdateChecker;
import com.mikedeejay2.mikedeejay2lib.util.version.MinecraftVersion;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.SimpleStackConfig;
import com.mikedeejay2.simplestack.api.SimpleStackTimings;
import com.mikedeejay2.simplestack.bytebuddy.*;
import com.mikedeejay2.simplestack.commands.*;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl;
import com.mikedeejay2.simplestack.config.ConfigListener;
import com.mikedeejay2.simplestack.debug.SimpleStackTimingsImpl;

/**
 * Simple Stack v2.0 plugin for Minecraft TBD - 1.19
 * <p>
 * The source code for Simple Stack can be found here:
 * <a href="https://github.com/Mikedeejay2/SimpleStackPlugin">https://github.com/Mikedeejay2/SimpleStackPlugin</a>
 * <p>
 * Simple Stack is powered by Mikedeejay2Lib.
 * <p>
 * The source code for Mikedeejay2Lib can be found here:
 * <a href="https://github.com/Mikedeejay2/Mikedeejay2Lib">https://github.com/Mikedeejay2/Mikedeejay2Lib</a>
 *
 * @author Mikedeejay2
 */
public final class SimpleStack extends BukkitPlugin {
    private static SimpleStack instance;

    // The config of Simple Stack which stores all customizable data
    private SimpleStackConfigImpl config;
    private SimpleStackTimingsImpl timings;

    private BStats bStats;
    private UpdateChecker updateChecker;
    private CommandManager commandManager;

    private void registerCommands() {
        this.commandManager.addSubcommand(new HelpCommand(this));
        this.commandManager.addSubcommand(new ReloadCommand(this));
        this.commandManager.addSubcommand(new ResetCommand(this));
        this.commandManager.addSubcommand(new SetAmountCommand(this));
        this.commandManager.addSubcommand(new ConfigCommand(this));
        this.commandManager.addSubcommand(new AddItemCommand(this));
        this.commandManager.addSubcommand(new RemoveItemCommand(this));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        setPrefix("&f[&b" + this.getDescription().getName() + "&f]&r ");
        TranslationManager.GLOBAL.registerDirectory("lang/simplestack", true);

        this.timings = new SimpleStackTimingsImpl(this);
        this.config = new SimpleStackConfigImpl(this);
        registerEvent(new ConfigListener(config));
        setupApi();

        if(checkVersion() || installByteBuddyAgent() || installAgent()) return;

        this.bStats = new BStats(this);
        this.bStats.init(9379);
        this.updateChecker = new UpdateChecker(this);
        this.updateChecker.init("Mikedeejay2", "SimpleStackPlugin");
        this.updateChecker.checkForUpdates(10);

        this.commandManager = new CommandManager(this, "simplestack");
        registerCommands();
        commandManager.setDefaultSubCommand("help");
        registerCommand(commandManager);

        sendInfo("Finished initialization.");
    }

    private void setupApi() {
        ReflectorClass<SimpleStackAPI> apiClass = Reflector.of(SimpleStackAPI.class);
        apiClass.field("config").set(null, config);
        apiClass.field("timings").set(null, timings);
    }

    private boolean installAgent() {
        sendInfo("Installing Simple Stack transformer, this may take a moment...");
        if(SimpleStackAgent.install()) {
            disablePlugin(this);
            return true;
        }
        return false;
    }

    private boolean installByteBuddyAgent() {
        sendInfo("Creating Simple Stack transformer, this may take a moment...");
        if(ByteBuddyHolder.install()) {
            sendSevere("&cSimpleStack is not compatible with this installation of Java!");
            sendSevere("&cCommon solutions are to use Java 9 or greater OR use a Java 8 JDK.");
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
        if(!MappingsLookup.loadMappings(this)) {
            sendSevere(String.format(
                "Simple Stack %s is not compatible with Minecraft version %s!",
                this.getDescription().getVersion(),
                MinecraftVersion.getVersionString()));
            disablePlugin(this);
            return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        if(ByteBuddyHolder.getInstrumentation() != null) {
            SimpleStackAgent.reset();
        }

        super.onDisable();

        if(config != null && config.isModified()) {
            config.saveToDisk(true);
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public static SimpleStack getInstance() {
        return instance;
    }
}
