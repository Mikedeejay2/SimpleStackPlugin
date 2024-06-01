package com.mikedeejay2.simplestack.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;

/**
 * A {@link PrintStream} which prevents printing specified strings
 *
 * @author Mikedeejay2
 */
public final class BlacklistPrintStream extends PrintStream {
    private final List<String> blacklist;

    public BlacklistPrintStream(@NotNull OutputStream out, List<String> blacklist) {
        super(out);
        this.blacklist = blacklist;
    }

    @Override
    public void print(@Nullable String s) {
        if(isBlacklisted(s)) return;
        super.print(s);
    }

    @Override
    public void println(@Nullable String x) {
        if(isBlacklisted(x)) return;
        super.println(x);
    }

    private boolean isBlacklisted(String s) {
        for(String blacklisted : blacklist) {
            if(s.contains(blacklisted)) return true;
        }
        return false;
    }
}
