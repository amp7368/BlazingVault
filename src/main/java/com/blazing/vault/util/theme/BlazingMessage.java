package com.blazing.vault.util.theme;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public interface BlazingMessage {

    void replyError(CommandInteraction event);

    MessageCreateData createMsg();
}
