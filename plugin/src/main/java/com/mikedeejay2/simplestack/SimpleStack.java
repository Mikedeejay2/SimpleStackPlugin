package com.mikedeejay2.simplestack;

import com.mikedeejay2.mikedeejay2lib.BukkitPlugin;
import com.mikedeejay2.mikedeejay2lib.commands.CommandManager;
import com.mikedeejay2.mikedeejay2lib.reflect.Reflector;
import com.mikedeejay2.mikedeejay2lib.reflect.ReflectorClass;
import com.mikedeejay2.mikedeejay2lib.text.PlaceholderFormatter;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.text.language.TranslationManager;
import com.mikedeejay2.mikedeejay2lib.util.bstats.BStats;
import com.mikedeejay2.mikedeejay2lib.util.debug.CrashReport;
import com.mikedeejay2.mikedeejay2lib.util.update.UpdateChecker;
import com.mikedeejay2.mikedeejay2lib.util.version.MinecraftVersion;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.bytecode.*;
import com.mikedeejay2.simplestack.commands.*;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl;
import com.mikedeejay2.simplestack.config.ConfigListener;
import com.mikedeejay2.simplestack.debug.SimpleStackTimingsImpl;

import java.util.function.Consumer;

/**
 * Simple Stack v2
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
    public static final Text CRASH_INFO_1 = Text.literal("&c").concat(Text.translatable("simplestack.crash.info_message_l1"));
    public static final Text CRASH_INFO_2 = Text.literal("&c").concat(Text.translatable("simplestack.crash.info_message_l2")).placeholder(
        PlaceholderFormatter.of("url", Text.literal("&bhttps://github.com/Mikedeejay2/SimpleStackPlugin/issues&c")));
    public static final Text CRASH_INFO_3 = Text.literal("&c").concat(Text.translatable("simplestack.crash.info_message_l3")).placeholder(
        PlaceholderFormatter.of("path", Text.literal("plugins/SimpleStack/crash-reports")));

    private static SimpleStack instance;

    // The config of Simple Stack which stores all customizable data
    private SimpleStackConfigImpl config;
    private SimpleStackTimingsImpl timings;

    private BStats bStats;
    private UpdateChecker updateChecker;
    private CommandManager commandManager;

    private void registerCommands() {
        this.commandManager.addSubcommand(new HelpCommand(this));
        this.commandManager.addSubcommand(new ConfigCommand(this));
        this.commandManager.addSubcommand(new ReloadCommand(this));
        this.commandManager.addSubcommand(new ReportCommand(this));
        this.commandManager.addSubcommand(new ResetCommand(this));
        this.commandManager.addSubcommand(new SetAmountCommand(this));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        setPrefix("&f[&b" + this.getDescription().getName() + "&f]&r ");
        TranslationManager.GLOBAL.registerDirectory("lang/simplestack", true);

        this.timings = new SimpleStackTimingsImpl(this);
        this.config = new SimpleStackConfigImpl(this);
        this.config.load();
        registerEvent(new ConfigListener(config));
        setupApi();

        if(loadMappings() || installByteBuddyAgent() || installAndTransform()) return;

        this.bStats = new BStats(this);
        this.bStats.init(9379);
        this.updateChecker = new UpdateChecker(this);
        this.updateChecker.init("Mikedeejay2", "SimpleStackPlugin");
        this.updateChecker.checkForUpdates(10);

        this.commandManager = new CommandManager(this, "simplestack");
        registerCommands();
        commandManager.setDefaultSubCommand("help");
        registerCommand(commandManager);

        sendInfo(Text.of("simplestack.info.finish_init"));
    }

    private void setupApi() {
        ReflectorClass<SimpleStackAPI> apiClass = Reflector.of(SimpleStackAPI.class);
        apiClass.field("config").set(null, config);
        apiClass.field("timings").set(null, timings);
        apiClass.field("initialized").set(null, true);
    }

    private boolean loadMappings() {
        sendInfo(Text.of("simplestack.info.load_version").placeholder(
            PlaceholderFormatter.of("mcver", MinecraftVersion.VERSION)));
        if(!MappingsLookup.loadMappings(this)) {
            sendSevere(Text.of("&c").concat("simplestack.errors.incompatible_version").placeholder(
                PlaceholderFormatter.of("ssver", this.getDescription().getVersion())
                    .and("mcver", MinecraftVersion.VERSION)));
            disablePlugin(this);
            return true;
        }
        if(!MappingsLookup.validateMappings(this)) {
            disablePlugin(this);
            return true;
        }
        return false;
    }

    private boolean installByteBuddyAgent() {
        sendInfo(Text.of("simplestack.info.install_transformer"));
        if(ByteBuddyHolder.install()) {
            sendSevere(Text.of("&c").concat("simplestack.errors.incompatible_java_l1"));
            sendSevere(Text.of("&c").concat("simplestack.errors.incompatible_java_l2"));
            disablePlugin(this);
            return true;
        }
        return false;
    }

    private boolean installAndTransform() {
        sendInfo(Text.of("simplestack.info.apply_transformations"));
        if(SimpleStackAgent.registerTransformers() || SimpleStackAgent.install()) {
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
            config.save();
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public static SimpleStack getInstance() {
        return instance;
    }

    public void fillCrashReport(CrashReport crashReport) {
        config.fillCrashReportSection(crashReport.addSection("Simple Stack Configuration"));
        SimpleStackAgent.fillCrashReportSection(crashReport.addSection("Simple Stack Agent"));
    }

    public static void doCrash(String description, Throwable throwable, Consumer<CrashReport> consumer) {
        CrashReport crashReport = new CrashReport(SimpleStack.getInstance(), description, true, true);
        crashReport.setThrowable(throwable);

        consumer.accept(crashReport);
        SimpleStack.getInstance().fillCrashReport(crashReport);

        crashReport.addInfo(SimpleStack.CRASH_INFO_1)
            .addInfo(SimpleStack.CRASH_INFO_2)
            .addInfo(SimpleStack.CRASH_INFO_3);

        crashReport.execute();
    }
}
