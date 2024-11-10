package com.blazing.vault.discord.base.command;

import discord.util.dcf.slash.DCFSlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class BaseCommand extends DCFSlashCommand implements CommandCheckPermission, SendMessage {

    private boolean isOnlyEmployee = false;
    private boolean isOnlyManager = false;

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        if (this.isBadPermission(event)) return;
        this.onCheckedCommand(event);
    }

    public void setOnlyEmployee() {
        this.isOnlyEmployee = true;
    }

    public void setOnlyManager() {
        this.isOnlyManager = true;
    }

    @Override
    public boolean isOnlyEmployee() {
        return isOnlyEmployee;
    }

    @Override
    public boolean isOnlyManager() {
        return isOnlyManager;
    }

    protected abstract void onCheckedCommand(SlashCommandInteractionEvent event);
}
