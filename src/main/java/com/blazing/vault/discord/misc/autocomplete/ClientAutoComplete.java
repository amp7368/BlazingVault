package com.blazing.vault.discord.misc.autocomplete;

import com.blazing.vault.config.discord.DiscordPermissions;
import com.blazing.vault.database.model.entity.client.ClientSearch;
import com.blazing.vault.database.model.entity.client.DClient;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import org.jetbrains.annotations.NotNull;

public class ClientAutoComplete extends BlazingAutoComplete<DClient> {

    public ClientAutoComplete(String optionName) {
        super(optionName);
    }

    @Override
    protected List<DClient> autoComplete(@NotNull CommandAutoCompleteInteractionEvent event, String arg) {
        if (!event.isFromGuild()) return List.of();

        boolean isEmployee = DiscordPermissions.get().isEmployee(event.getMember());
        if (!isEmployee) return List.of();

        return ClientSearch.autoComplete(arg);
    }

    @NotNull
    @Override
    protected Choice choice(DClient client) {
        return new Choice(client.getEffectiveName(), client.getEffectiveName());
    }
}
