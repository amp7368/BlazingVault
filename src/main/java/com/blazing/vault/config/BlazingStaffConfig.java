package com.blazing.vault.config;

import apple.lib.modules.configs.data.config.init.AppleConfigInit;
import com.blazing.vault.config.help.HelpCommandConfig;

public class BlazingStaffConfig extends AppleConfigInit {

    private static BlazingStaffConfig instance;

    protected HelpCommandConfig helpCommand = new HelpCommandConfig();

    public BlazingStaffConfig() {
        instance = this;
    }

    public static BlazingStaffConfig get() {
        return instance;
    }

    public HelpCommandConfig getHelp() {
        return helpCommand;
    }
}
