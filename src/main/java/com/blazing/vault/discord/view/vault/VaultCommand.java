package com.blazing.vault.discord.view.vault;

import com.blazing.vault.discord.base.command.BaseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class VaultCommand extends BaseCommand {

    @Override
    protected void onCheckedCommand(SlashCommandInteractionEvent event) {

    }

    @Override
    public SlashCommandData getData() {
        return null;
    }
}
