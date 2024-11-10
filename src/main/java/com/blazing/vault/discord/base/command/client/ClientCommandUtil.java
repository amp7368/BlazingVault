package com.blazing.vault.discord.base.command.client;

import com.blazing.vault.database.model.entity.client.ClientApi.ClientQueryApi;
import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.discord.base.command.SendMessage;
import com.blazing.vault.util.theme.BlazingMessages.ErrorMessages;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

interface ClientCommandUtil extends SendMessage {

    default void getClientAndDoCommand(SlashCommandInteractionEvent event) {
        DClient client = ClientQueryApi.findByDiscord(event.getUser().getIdLong());
        if (client == null) {
            ErrorMessages.registerWithStaff().replyError(event);
            return;
        }
        this.onClientCommand(event, client);
    }

    void onClientCommand(SlashCommandInteractionEvent event, DClient client);
}
