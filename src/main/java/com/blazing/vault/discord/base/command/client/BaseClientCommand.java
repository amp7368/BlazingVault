package com.blazing.vault.discord.base.command.client;

import com.blazing.vault.discord.base.command.BaseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class BaseClientCommand extends BaseCommand implements ClientCommandUtil {

    @Override
    protected final void onCheckedCommand(SlashCommandInteractionEvent event) {
        getClientAndDoCommand(event);
    }
}