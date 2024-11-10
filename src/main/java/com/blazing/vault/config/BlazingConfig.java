package com.blazing.vault.config;

import com.blazing.vault.config.discord.DiscordConfig;
import com.blazing.vault.config.discord.DiscordPermissions;

public class BlazingConfig {

    private static BlazingConfig instance;
    public boolean isProduction = true;
    public DiscordConfig discord = new DiscordConfig();
    public DiscordPermissions discordPermissions = new DiscordPermissions();
    protected boolean enableMessages = false;
    protected boolean shouldResimulate = false;

    public BlazingConfig() {
        instance = this;
    }

    public static BlazingConfig get() {
        return instance;
    }

    public static BlazingStaffConfig staff() {
        return BlazingStaffConfig.get();
    }

    public boolean isProduction() {
        return this.isProduction;
    }

    public boolean shouldResimulate() {
        return this.shouldResimulate;
    }

    public boolean isMessagesEnabled() {
        return this.enableMessages;
    }
}
