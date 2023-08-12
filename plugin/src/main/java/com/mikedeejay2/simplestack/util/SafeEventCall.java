package com.mikedeejay2.simplestack.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import java.util.logging.Level;

public final class SafeEventCall {
    private SafeEventCall() {
        throw new UnsupportedOperationException("SafeEventCall cannot be instantiated as an object");
    }

    /**
     * A stripped down version of {@link org.bukkit.plugin.SimplePluginManager#callEvent(Event) SimplePluginManager}
     * that does not check for synchronicity.
     * <p>
     * This method also excludes checking for {@link org.bukkit.plugin.AuthorNagException AuthorNagExceptions}, and does
     * <strong>not</strong> call Paper <code>ServerExceptionEvents</code> on an error.
     *
     * @param event The {@link Event} to call
     */
    public static void callEvent(Event event) {
        final HandlerList handlers = event.getHandlers();
        final RegisteredListener[] listeners = handlers.getRegisteredListeners();

        for (RegisteredListener registration : listeners) {
            if (!registration.getPlugin().isEnabled()) {
                continue;
            }

            try {
                registration.callEvent(event);
            } catch (Throwable ex) {
                String msg = "Could not pass event " + event.getEventName() + " to " + registration.getPlugin().getDescription().getName();
                Bukkit.getLogger().log(Level.SEVERE, msg, ex);
            }
        }
    }
}
