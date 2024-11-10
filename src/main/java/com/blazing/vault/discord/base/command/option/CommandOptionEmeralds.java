package com.blazing.vault.discord.base.command.option;

import com.blazing.vault.util.emerald.Emeralds;
import com.blazing.vault.util.emerald.EmeraldsParser;
import com.blazing.vault.util.theme.BlazingMessage;
import com.blazing.vault.util.theme.BlazingMessages;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class CommandOptionEmeralds extends CommandOptionMulti<String, Emeralds> {

    public CommandOptionEmeralds(String name, String description, OptionType type) {
        super(name, description, type, OptionMapping::getAsString, EmeraldsParser::tryParse);
    }

    @Override
    public BlazingMessage getErrorMessage(CommandInteraction event) {
        String mapped = getMap1(event);
        if (mapped == null) return super.getErrorMessage(event);

        try {
            EmeraldsParser.parse(mapped);
        } catch (NumberFormatException e) {
            return BlazingMessages.stringMessage(e.getMessage());
        }

        return super.getErrorMessage(event);
    }
}
